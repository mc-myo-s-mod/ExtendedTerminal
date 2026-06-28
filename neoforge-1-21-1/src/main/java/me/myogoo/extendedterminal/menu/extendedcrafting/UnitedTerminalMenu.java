package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.MEStorage;
import appeng.helpers.InventoryAction;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.items.CraftingTermMenu;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.api.common.crafting.TierInput;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.myotus.client.MyoTranslateKey;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.UnitedCraftingTerminalSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import me.myogoo.myotus.api.MyotusAPI;
import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import appeng.api.inventories.ISegmentedInventory;
import appeng.menu.slot.CraftingMatrixSlot;
import com.google.common.base.Preconditions;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.me.storage.LinkStatusRespectingInventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class UnitedTerminalMenu extends ETTerminalBaseMenu<Recipe<RecipeInput>> {
    protected final ETCraftingBaseSlot<?, ?> outputSlot;
    private final ISegmentedInventory craftingInventoryHost;
    protected final CraftingMatrixSlot[] craftingSlots;

    public static final String ACTION_REMEMBER_RECIPE_KIND = "rememberRecipeKind";
    private static final String ACTION_SELECT_NEXT_RECIPE_KIND = "selectNextRecipeKind";
    private static final String ACTION_SELECT_PREVIOUS_RECIPE_KIND = "selectPreviousRecipeKind";
    public static final MenuType<UnitedTerminalMenu> TYPE = MenuTypeBuilder
            .create(UnitedTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ETMenuType.UNITED_TERMINAL.getId());

    @Nullable
    private UnitedRecipe currentUnitedRecipe;
    @GuiSync(0)
    private UnitedRecipeKind selectedRecipeKind = UnitedRecipeKind.EXTENDED_CRAFTING_ULTIMATE;
    @Nullable
    private List<ItemStack> lastUnitedItems;

    public UnitedTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.UNITED_TERMINAL, ExtendedCraftingConfig.INSTANCE.getUltimateConfig());
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];

        var craftingGridInv = this.craftingInventoryHost.getSubInventory(this.menuType.getCraftingInventory());
        for (int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this, craftingGridInv, i),
                    this.menuType.getSlotSemanticGrid());
        }

        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = createOutputSlot(linkStatusInventory, craftingGridInv),
                this.menuType.getSlotSemanticResult());

        registerClientAction(ACTION_REMEMBER_RECIPE_KIND, Boolean.class, this::setRememberRecipeKind);
        registerClientAction(ACTION_SELECT_NEXT_RECIPE_KIND, this::selectNextRecipeKind);
        registerClientAction(ACTION_SELECT_PREVIOUS_RECIPE_KIND, this::selectPreviousRecipeKind);
        loadRememberedRecipeKind();
        updateCurrentRecipeAndOutput(true);
    }

    protected ETCraftingBaseSlot<?, ?> createOutputSlot(MEStorage storage, InternalInventory craftingGridInv) {
        return new UnitedCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                this.energySource, storage, craftingGridInv, craftingGridInv, this, this.menuType);
    }


    @Override
    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        CraftingMatrixSlot slot = craftingSlots[0];
        var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        PacketDistributor.sendToServer(p);
    }

    protected List<ItemStack> getCraftingSlotItems() {
        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for (var craftingSlot : craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }
        return testItems;
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return this.craftingInventoryHost.getSubInventory(menuType.getCraftingInventory());
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        if (checkCraftingOnlyActive()) return;

        normalizeSelectedRecipeKind();
        var testItems = getCraftingSlotItems();
        if (!forceUpdate && sameItems(this.lastUnitedItems, testItems)) {
            return;
        }

        var recipe = findUnitedRecipe(testItems);

        this.currentUnitedRecipe = recipe;
        this.currentRecipe = recipe == null ? null : recipe.castRecipeHolder();
        this.lastUnitedItems = testItems;

        if (this.currentUnitedRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentUnitedRecipe.assemble(getPlayer().level()));
        }
    }

    @Nullable
    public UnitedRecipe getCurrentUnitedRecipe() {
        return currentUnitedRecipe;
    }

    private static boolean sameItems(@Nullable List<ItemStack> previous, List<ItemStack> current) {
        if (previous == null || previous.size() != current.size()) {
            return false;
        }
        for (int i = 0; i < previous.size(); i++) {
            if (!ItemStack.matches(previous.get(i), current.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public UnitedRecipe findUnitedRecipe(List<ItemStack> items) {
        normalizeSelectedRecipeKind();
        return findUnitedRecipe(items, this.selectedRecipeKind);
    }

    public UnitedRecipeKind getSelectedRecipeKind() {
        normalizeSelectedRecipeKind();
        return selectedRecipeKind;
    }

    public boolean hasActiveRecipeKinds() {
        return !getActiveRecipeKinds().isEmpty();
    }

    public boolean hasMultipleRecipeKinds() {
        return getActiveRecipeKinds().size() > 1;
    }

    public void setSelectedRecipeKind(UnitedRecipeKind selectedRecipeKind) {
        if (selectedRecipeKind == null
                || !selectedRecipeKind.isActive()
                || this.selectedRecipeKind == selectedRecipeKind) {
            return;
        }
        this.selectedRecipeKind = selectedRecipeKind;
        saveRememberedRecipeKind(selectedRecipeKind);
        this.currentUnitedRecipe = null;
        this.lastUnitedItems = null;
        updateCurrentRecipeAndOutput(true);
    }

    private void loadRememberedRecipeKind() {
        if (!(getHost() instanceof IUnitedTerminalHost host) || !host.rememberUnitedRecipeKind()) {
            return;
        }

        var remembered = host.getRememberedUnitedRecipeKind();
        if (remembered != null && remembered.isActive()) {
            this.selectedRecipeKind = remembered;
        }
    }

    private void saveRememberedRecipeKind(UnitedRecipeKind recipeKind) {
        if (!isClientSide() && getHost() instanceof IUnitedTerminalHost host && host.rememberUnitedRecipeKind()) {
            host.setRememberedUnitedRecipeKind(recipeKind);
        }
    }

    public boolean rememberRecipeKind() {
        return !(getHost() instanceof IUnitedTerminalHost host) || host.rememberUnitedRecipeKind();
    }

    public void setRememberRecipeKind(boolean remember) {
        if (isClientSide()) {
            sendClientAction(ACTION_REMEMBER_RECIPE_KIND, remember);
            return;
        }

        if (getHost() instanceof IUnitedTerminalHost host) {
            host.setRememberUnitedRecipeKind(remember);
            if (remember) {
                host.setRememberedUnitedRecipeKind(getSelectedRecipeKind());
            }
        }
    }

    public void selectNextRecipeKind() {
        selectRecipeKindOffset(1, ACTION_SELECT_NEXT_RECIPE_KIND);
    }

    public void selectPreviousRecipeKind() {
        selectRecipeKindOffset(-1, ACTION_SELECT_PREVIOUS_RECIPE_KIND);
    }

    private void selectRecipeKindOffset(int offset, String clientAction) {
        if (isClientSide()) {
            sendClientAction(clientAction);
            return;
        }
        var values = getActiveRecipeKinds();
        if (values.size() <= 1) {
            return;
        }
        var currentIndex = values.indexOf(this.selectedRecipeKind);
        setSelectedRecipeKind(values.get(Math.floorMod(currentIndex + offset, values.size())));
    }

    private void normalizeSelectedRecipeKind() {
        if (this.selectedRecipeKind != null && this.selectedRecipeKind.isActive()) {
            return;
        }
        var values = getActiveRecipeKinds();
        this.selectedRecipeKind = values.isEmpty() ? UnitedRecipeKind.EXTENDED_CRAFTING_ULTIMATE : values.getFirst();
    }

    public static List<UnitedRecipeKind> getActiveRecipeKinds() {
        var kinds = new ArrayList<UnitedRecipeKind>(UnitedRecipeKind.values().length);
        for (var kind : UnitedRecipeKind.values()) {
            if (kind.isActive()) {
                kinds.add(kind);
            }
        }
        return kinds;
    }

    @Nullable
    public UnitedRecipe findUnitedRecipe(List<ItemStack> items, UnitedRecipeKind kind) {
        if (!kind.isActive()) {
            return null;
        }
        var level = getPlayer().level();
        return switch (kind.family()) {
            case EXTENDED_CRAFTING -> findExtendedCraftingRecipe(level, items, kind);
            case AVARITIA_NEO -> findAvaritiaNeoRecipe(level, items, kind);
            case RE_AVARITIA -> findReAvaritiaRecipe(level, items, kind);
        };
    }

    public TableCraftingInput createTableInput(List<ItemStack> items, @Nullable ITableRecipe recipe) {
        return TableCraftingInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(), items, getInputTier(recipe));
    }

    protected int getInputTier(@Nullable ITableRecipe recipe) {
        if (recipe == null) {
            return 0;
        }
        var tier = recipe.getTier();
        return tier > 0 ? tier : 0;
    }

    @Nullable
    private UnitedRecipe findExtendedCraftingRecipe(Level level, List<ItemStack> items, UnitedRecipeKind kind) {
        for (var recipe : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.TABLE.get())) {
            if (!canCraftExtendedCraftingRecipeInKind(recipe.value(), kind)) {
                continue;
            }
            var input = createTableInput(items, recipe.value());
            if (recipe.value().matches(input, level)) {
                return new UnitedRecipe(this, kind, recipe, input);
            }
        }
        return null;
    }

    private static boolean canCraftExtendedCraftingRecipeInKind(ITableRecipe recipe, UnitedRecipeKind kind) {
        int recipeTier = recipe.getTier();
        if (recipe.hasRequiredTier()) {
            return recipeTier == kind.tier();
        }
        return recipeTier <= kind.tier();
    }

    @Nullable
    private UnitedRecipe findAvaritiaNeoRecipe(Level level, List<ItemStack> items, UnitedRecipeKind kind) {
        try {
            var input = CraftingInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(), items);
            return level.getRecipeManager().getRecipeFor(AvaritiaRecipes.EXTREME_CRAFTING.get(), input, level)
                    .map(recipe -> new UnitedRecipe(this, kind, recipe, input))
                    .orElse(null);
        } catch (LinkageError ignored) {
            return null;
        }
    }

    @Nullable
    private UnitedRecipe findReAvaritiaRecipe(Level level, List<ItemStack> items, UnitedRecipeKind kind) {
        try {
            var input = TierInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(), items, kind.tier());
            return level.getRecipeManager().getRecipeFor(committee.nova.mods.avaritia.init.registry.ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), input, level)
                    .map(recipe -> new UnitedRecipe(this, kind, recipe, input))
                    .orElse(null);
        } catch (LinkageError ignored) {
            return null;
        }
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        if (this.getSlot(slot) instanceof UnitedCraftingTerminalSlot craftingSlot) {
            switch (action) {
                case CRAFT_SHIFT:
                case CRAFT_ALL:
                case CRAFT_ITEM:
                case CRAFT_STACK:
                    craftingSlot.doClick(action, player);
                    return;
            }
        }
        super.doAction(player, action, slot, id);
    }

    public enum UnitedRecipeKind {
        EXTENDED_CRAFTING_BASIC("extended_crafting/basic", RecipeFamily.EXTENDED_CRAFTING, ExtendedCrafting.class, 1, ETTranslationKey.GUI.UNITED_RECIPE_KIND_EXTENDED_CRAFTING_BASIC, "extendedcrafting", "basic_table"),
        EXTENDED_CRAFTING_ADVANCED("extended_crafting/advanced", RecipeFamily.EXTENDED_CRAFTING, ExtendedCrafting.class, 2, ETTranslationKey.GUI.UNITED_RECIPE_KIND_EXTENDED_CRAFTING_ADVANCED, "extendedcrafting", "advanced_table"),
        EXTENDED_CRAFTING_ELITE("extended_crafting/elite", RecipeFamily.EXTENDED_CRAFTING, ExtendedCrafting.class, 3, ETTranslationKey.GUI.UNITED_RECIPE_KIND_EXTENDED_CRAFTING_ELITE, "extendedcrafting", "elite_table"),
        EXTENDED_CRAFTING_ULTIMATE("extended_crafting/ultimate", RecipeFamily.EXTENDED_CRAFTING, ExtendedCrafting.class, 4, ETTranslationKey.GUI.UNITED_RECIPE_KIND_EXTENDED_CRAFTING_ULTIMATE, "extendedcrafting", "ultimate_table"),
        AVARITIA_NEO_EXTREME("avaritia_neo/extreme", RecipeFamily.AVARITIA_NEO, AvaritiaNeo.class, 4, ETTranslationKey.GUI.UNITED_RECIPE_KIND_AVARITIA_NEO_EXTREME, "avaritia", "extreme_crafting_table"),
        RE_AVARITIA_SCULK("re_avaritia/sculk", RecipeFamily.RE_AVARITIA, ReAvaritia.class, 1, ETTranslationKey.GUI.UNITED_RECIPE_KIND_RE_AVARITIA_SCULK, "avaritia", "sculk_crafting_table"),
        RE_AVARITIA_NETHER("re_avaritia/nether", RecipeFamily.RE_AVARITIA, ReAvaritia.class, 2, ETTranslationKey.GUI.UNITED_RECIPE_KIND_RE_AVARITIA_NETHER, "avaritia", "nether_crafting_table"),
        RE_AVARITIA_END("re_avaritia/end", RecipeFamily.RE_AVARITIA, ReAvaritia.class, 3, ETTranslationKey.GUI.UNITED_RECIPE_KIND_RE_AVARITIA_END, "avaritia", "end_crafting_table"),
        RE_AVARITIA_EXTREME("re_avaritia/extreme", RecipeFamily.RE_AVARITIA, ReAvaritia.class, 4, ETTranslationKey.GUI.UNITED_RECIPE_KIND_RE_AVARITIA_EXTREME, "avaritia", "extreme_crafting_table");

        private final String serializedName;
        private final RecipeFamily family;
        @Nullable
        private final Class<? extends Annotation> integration;
        private final int tier;
        private final MyoTranslateKey labelKey;
        private final String iconNamespace;
        private final String iconPath;

        UnitedRecipeKind(String serializedName, RecipeFamily family, @Nullable Class<? extends Annotation> integration,
                         int tier, MyoTranslateKey labelKey, String iconNamespace, String iconPath) {
            this.serializedName = serializedName;
            this.family = family;
            this.integration = integration;
            this.tier = tier;
            this.labelKey = labelKey;
            this.iconNamespace = iconNamespace;
            this.iconPath = iconPath;
        }

        public String serializedName() { return serializedName; }
        public RecipeFamily family() { return family; }
        public int tier() { return tier; }
        public String labelKey() { return labelKey.key(); }
        public String iconNamespace() { return iconNamespace; }
        public String iconPath() { return iconPath; }

        @Nullable
        public static UnitedRecipeKind bySerializedName(String name) {
            for (var kind : values()) {
                if (kind.serializedName.equals(name)) {
                    return kind;
                }
            }
            return null;
        }

        @Nullable
        public static UnitedRecipeKind fromExtendedCraftingTier(int tier) {
            return switch (tier) {
                case 1 -> EXTENDED_CRAFTING_BASIC;
                case 2 -> EXTENDED_CRAFTING_ADVANCED;
                case 3 -> EXTENDED_CRAFTING_ELITE;
                case 4 -> EXTENDED_CRAFTING_ULTIMATE;
                default -> null;
            };
        }

        @Nullable
        public static UnitedRecipeKind fromReAvaritiaTier(int tier) {
            return switch (tier) {
                case 1 -> RE_AVARITIA_SCULK;
                case 2 -> RE_AVARITIA_NETHER;
                case 3 -> RE_AVARITIA_END;
                case 4 -> RE_AVARITIA_EXTREME;
                default -> null;
            };
        }

        public boolean isActive() {
            return integration == null || MyotusAPI.integrations().isLoaded(integration);
        }
    }

    public enum RecipeFamily {
        EXTENDED_CRAFTING,
        AVARITIA_NEO,
        RE_AVARITIA
    }

    public record UnitedRecipe(UnitedTerminalMenu menu, UnitedRecipeKind kind,
                               RecipeHolder<? extends Recipe<? extends RecipeInput>> recipe,
                               RecipeInput input) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        public RecipeHolder<Recipe<RecipeInput>> castRecipeHolder() {
            return (RecipeHolder) recipe;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public RecipeHolder<ITableRecipe> castTableRecipeHolder() {
            return (RecipeHolder) recipe;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public boolean matches(Level level) {
            return ((Recipe) recipe.value()).matches(input, level);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public ItemStack assemble(Level level) {
            return ((Recipe) recipe.value()).assemble(input, level.registryAccess());
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public NonNullList<ItemStack> getRemainingItems() {
            return ((Recipe) recipe.value()).getRemainingItems(input);
        }

        public int top() {
            if (input instanceof TableCraftingInput tableInput) {
                return tableInput.top();
            }
            if (input instanceof TierInput tierInput) {
                return tierInput.top();
            }
            return 0;
        }

        public int left() {
            if (input instanceof TableCraftingInput tableInput) {
                return tableInput.left();
            }
            if (input instanceof TierInput tierInput) {
                return tierInput.left();
            }
            return 0;
        }

        public int width() {
            if (input instanceof TableCraftingInput tableInput) {
                return tableInput.width();
            }
            if (input instanceof TierInput tierInput) {
                return tierInput.width();
            }
            if (input instanceof CraftingInput craftingInput) {
                return craftingInput.width();
            }
            return menu.menuType.getGridSideLength();
        }
    }
}
