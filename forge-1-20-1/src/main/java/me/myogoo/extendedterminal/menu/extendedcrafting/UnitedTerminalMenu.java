package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.MEStorage;
import appeng.helpers.InventoryAction;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.myotus.client.MyoTranslateKey;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.UnitedCraftingTerminalSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import appeng.api.inventories.ISegmentedInventory;
import appeng.menu.slot.CraftingMatrixSlot;
import com.google.common.base.Preconditions;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.menu.SlotSemantic;

public class UnitedTerminalMenu extends ETTerminalBaseMenu<Recipe<?>> {
    protected final ETCraftingBaseSlot<?, ?> outputSlot;
    private final ISegmentedInventory craftingInventoryHost;
    protected final CraftingMatrixSlot[] craftingSlots;

    public static final String ACTION_REMEMBER_RECIPE_TYPE = "rememberRecipeType";
    private static final String ACTION_SELECT_NEXT_RECIPE_KIND = "selectNextRecipeKind";
    private static final String ACTION_SELECT_PREVIOUS_RECIPE_KIND = "selectPreviousRecipeKind";
    public static final MenuType<UnitedTerminalMenu> TYPE = MenuTypeBuilder
            .create(UnitedTerminalMenu::new, ITerminalHost.class)
            .build(ETMenuType.UNITED_TERMINAL.getIdAsString());

    @Nullable
    private UnitedRecipe currentUnitedRecipe;
    @Nullable
    private List<ItemStack> lastUnitedItems;
    @GuiSync(0)
    private UnitedRecipeKind selectedRecipeKind = UnitedRecipeKind.VANILLA;

    public UnitedTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.UNITED_TERMINAL, ExtendedCraftingConfig.INSTANCE.getUltimateConfig());
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];

        var craftingGridInv = this.craftingInventoryHost.getSubInventory(this.menuType.getCraftingInventory());
        for (int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this, craftingGridInv, i),
                    this.menuType.getSlotSemanticGrid());
        }

        this.addSlot(this.outputSlot = createOutputSlot(host.getInventory(), craftingGridInv),
                this.menuType.getSlotSemanticResult());

        registerClientAction(ACTION_REMEMBER_RECIPE_TYPE, Boolean.class, this::setRememberRecipeType);
        registerClientAction(ACTION_SELECT_NEXT_RECIPE_KIND, this::selectNextRecipeKind);
        registerClientAction(ACTION_SELECT_PREVIOUS_RECIPE_KIND, this::selectPreviousRecipeKind);
        loadSavedRecipeKind();
        updateCurrentRecipeAndOutput(true);
    }

    protected ETCraftingBaseSlot<?, ?> createOutputSlot(MEStorage storage, InternalInventory craftingGridInv) {
        return new UnitedCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                this.powerSource, storage, craftingGridInv, craftingGridInv, this, this.menuType);
    }


    @Override
    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        CraftingMatrixSlot slot = craftingSlots[0];
        var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        NetworkHandler.instance().sendToServer(p);
    }

    protected List<ItemStack> getCraftingSlotItems() {
        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for (var craftingSlot : craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }
        return testItems;
    }

    @Override
    public SlotSemantic getCraftingGridSlotSemantic() {
        return this.menuType.getSlotSemanticGrid();
    }

    @Override
    public SlotSemantic getOutputSlotSemantic() {
        return this.menuType.getSlotSemanticResult();
    }

    @Override
    public int getCraftingGridSize() {
        return this.menuType.getGridSize();
    }

    @Override
    public int getCraftingGridWidth() {
        return this.menuType.getGridSideLength();
    }

    @Override
    public int getCraftingGridHeight() {
        return this.menuType.getGridSideLength();
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
        if (!forceUpdate && sameItems(this.lastUnitedItems, testItems)
                && (this.currentUnitedRecipe == null || getPlayer().level().getRecipeManager()
                        .byKey(this.currentUnitedRecipe.recipe().getId()).orElse(null) == this.currentUnitedRecipe.recipe())) {
            return;
        }
        if (forceUpdate) {
            this.currentUnitedRecipe = null;
        }

        var recipe = findUnitedRecipe(testItems);

        this.currentUnitedRecipe = recipe;
        this.currentRecipe = recipe == null ? null : recipe.recipe;
        this.lastUnitedItems = testItems;

        if (this.currentUnitedRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentUnitedRecipe.assemble(getPlayer().level()));
        }
    }

    @Nullable
    public Recipe<?> getCurrentUnitedRecipe() {
        return currentUnitedRecipe == null ? null : currentUnitedRecipe.recipe();
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
    public UnitedRecipe getCurrentUnitedRecipeRecord() {
        return currentUnitedRecipe;
    }

    @Nullable
    public CraftingContainer getCurrentUnitedInput() {
        return currentUnitedRecipe == null ? null : currentUnitedRecipe.input();
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
        saveSelectedRecipeKind(selectedRecipeKind);
        this.currentUnitedRecipe = null;
        this.lastUnitedItems = null;
        updateCurrentRecipeAndOutput(true);
    }

    private void loadSavedRecipeKind() {
        if (!(getHost() instanceof IUnitedTerminalHost host) || !host.shouldRememberRecipeType()) {
            return;
        }

        var remembered = host.getLastRecipeKind();
        if (remembered != null && remembered.isActive()) {
            this.selectedRecipeKind = remembered;
        }
    }

    private void saveSelectedRecipeKind(UnitedRecipeKind recipeKind) {
        if (!isClientSide() && getHost() instanceof IUnitedTerminalHost host && host.shouldRememberRecipeType()) {
            host.setLastRecipeKind(recipeKind);
        }
    }

    public boolean rememberRecipeType() {
        return !(getHost() instanceof IUnitedTerminalHost host) || host.shouldRememberRecipeType();
    }

    public void setRememberRecipeType(boolean remember) {
        if (isClientSide()) {
            sendClientAction(ACTION_REMEMBER_RECIPE_TYPE, remember);
            return;
        }

        if (getHost() instanceof IUnitedTerminalHost host) {
            host.setRememberRecipeType(remember);
            if (remember) {
                host.setLastRecipeKind(getSelectedRecipeKind());
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
        this.selectedRecipeKind = values.isEmpty() ? UnitedRecipeKind.VANILLA : values.get(0);
        saveSelectedRecipeKind(this.selectedRecipeKind);
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
        var previous = this.currentUnitedRecipe;
        if (previous != null && previous.kind() == kind && sameItems(this.lastUnitedItems, items)
                && level.getRecipeManager().byKey(previous.recipe().getId()).orElse(null) == previous.recipe()) {
            // Reuse the recipe, not its mutable input or assembled NBT/remainders.
            var input = createInput(items, previous.input().getWidth(), previous.left(), previous.top());
            if (input != null && previous.matches(input, level)) {
                return new UnitedRecipe(this, kind, previous.recipe(), input, previous.left(), previous.top());
            }
        }
        return switch (kind.family()) {
            case VANILLA -> findVanillaRecipe(level, items, kind);
            case EXTENDED_CRAFTING -> ExtendedCraftingLookup.findRecipe(this, level, items, kind);
            case AVARITIA_NEO -> AvaritiaNeoLookup.findRecipe(this, level, items, kind);
            case RE_AVARITIA -> ReAvaritiaLookup.findRecipe(this, level, items, kind);
        };
    }

    @Nullable
    public CraftingContainer createTableInput(List<ItemStack> items, int side) {
        int offset = Math.floorDiv(this.menuType.getGridSideLength() - side, 2);
        return createInput(items, side, offset, offset);
    }

    @Nullable
    private CraftingContainer createInput(List<ItemStack> items, int side, int left, int top) {
        var positioned = NonNullList.withSize(side * side, ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                continue;
            }
            int x = i % this.menuType.getGridSideLength() - left;
            int y = i / this.menuType.getGridSideLength() - top;
            if (x < 0 || y < 0 || x >= side || y >= side) {
                return null;
            }
            positioned.set(y * side + x, items.get(i).copy());
        }
        return new TransientCraftingContainer(this, side, side, positioned);
    }

    @Nullable
    private UnitedRecipe findVanillaRecipe(Level level, List<ItemStack> items, UnitedRecipeKind kind) {
        int side = menuType.getGridSideLength();
        int left = side;
        int top = side;
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) {
                left = Math.min(left, i % side);
                top = Math.min(top, i / side);
            }
        }
        // Vanilla custom recipes can use fixed 3x3 indices when assembling their NBT-bearing result.
        var compact = createInput(items, 3, left, top);
        CraftingContainer input = null;

        for (CraftingRecipe recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            if (compact != null && recipe.matches(compact, level)) {
                return new UnitedRecipe(this, kind, recipe, compact, left, top);
            }
            // A larger empty border cannot change these vanilla matches. Mod subclasses may differ.
            if (recipe.getClass() == ShapedRecipe.class && recipe.canCraftInDimensions(3, 3)
                    || compact != null && recipe.getClass() == ShapelessRecipe.class) {
                continue;
            }
            if (input == null) {
                input = createFullGridInput(items);
            }
            if (recipe.matches(input, level)) {
                return new UnitedRecipe(this, kind, recipe, input, 0, 0);
            }
        }
        return null;
    }

    private CraftingContainer createFullGridInput(List<ItemStack> items) {
        var positioned = NonNullList.withSize(menuType.getGridSize(), ItemStack.EMPTY);
        for (int i = 0; i < positioned.size() && i < items.size(); i++) {
            positioned.set(i, items.get(i).copy());
        }
        return new TransientCraftingContainer(this, menuType.getGridSideLength(), menuType.getGridSideLength(), positioned);
    }

    @ExtendedCrafting
    private static final class ExtendedCraftingLookup {
        private ExtendedCraftingLookup() {
        }

        @Nullable
        private static UnitedRecipe findRecipe(UnitedTerminalMenu menu, Level level, List<ItemStack> items,
                                               UnitedRecipeKind kind) {
            try {
                var inputs = new CraftingContainer[kind.tier() + 1];
                var testedTiers = new boolean[inputs.length];
                for (var recipe : level.getRecipeManager()
                        .getAllRecipesFor(com.blakebr0.extendedcrafting.init.ModRecipeTypes.TABLE.get())) {
                    if (!canCraftInKind(recipe, kind)) {
                        continue;
                    }
                    int tier = recipe.getTier();
                    if (!testedTiers[tier]) {
                        inputs[tier] = menu.createTableInput(items, tier * 2 + 1);
                        testedTiers[tier] = true;
                    }
                    var input = inputs[tier];
                    if (input != null && recipe.matches(input, level)) {
                        return new UnitedRecipe(menu, kind, recipe, input);
                    }
                }
                return null;
            } catch (LinkageError ignored) {
                return null;
            }
        }

        private static boolean canCraftInKind(com.blakebr0.extendedcrafting.api.crafting.ITableRecipe recipe,
                                              UnitedRecipeKind kind) {
            int recipeTier = recipe.getTier();
            if (recipe.hasRequiredTier()) {
                return recipeTier == kind.tier();
            }
            return recipeTier <= kind.tier();
        }
    }

    @AvaritiaNeo
    private static final class AvaritiaNeoLookup {
        private AvaritiaNeoLookup() {
        }

        @Nullable
        private static UnitedRecipe findRecipe(UnitedTerminalMenu menu, Level level, List<ItemStack> items,
                                               UnitedRecipeKind kind) {
            try {
                var input = menu.createFullGridInput(items);
                return level.getRecipeManager().getRecipeFor(net.byAqua3.avaritia.loader.AvaritiaRecipes.EXTREME_CRAFTING.get(), input, level)
                        .map(recipe -> new UnitedRecipe(menu, kind, recipe, input))
                        .orElse(null);
            } catch (LinkageError ignored) {
                return null;
            }
        }
    }

    @ReAvaritia
    private static final class ReAvaritiaLookup {
        private ReAvaritiaLookup() {
        }

        @Nullable
        private static UnitedRecipe findRecipe(UnitedTerminalMenu menu, Level level, List<ItemStack> items,
                                               UnitedRecipeKind kind) {
            try {
                var input = menu.createTableInput(items, kind.tier() * 2 + 1);
                if (input == null) {
                    return null;
                }
                for (var recipe : level.getRecipeManager()
                        .getAllRecipesFor(committee.nova.mods.avaritia.init.registry.ModRecipeTypes.CRAFTING_TABLE_RECIPE.get())) {
                    if (recipe.getTier() == kind.tier() && recipe.matches(input, level)) {
                        return new UnitedRecipe(menu, kind, recipe, input);
                    }
                }
                return null;
            } catch (LinkageError ignored) {
                return null;
            }
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
        VANILLA("vanilla", RecipeFamily.VANILLA, null, -1, ETTranslationKey.BLOCK.MINECRAFT_CRAFTING_TABLE, "minecraft", "crafting_table"),
        EXTENDED_CRAFTING_BASIC("extended_crafting/basic", RecipeFamily.EXTENDED_CRAFTING, ExtendedCrafting.class, 1, ETTranslationKey.BLOCK.EXTENDED_CRAFTING_BASIC, "extendedcrafting", "basic_table"),
        EXTENDED_CRAFTING_ADVANCED("extended_crafting/advanced", RecipeFamily.EXTENDED_CRAFTING, ExtendedCrafting.class, 2, ETTranslationKey.BLOCK.EXTENDED_CRAFTING_ADVANCED, "extendedcrafting", "advanced_table"),
        EXTENDED_CRAFTING_ELITE("extended_crafting/elite", RecipeFamily.EXTENDED_CRAFTING, ExtendedCrafting.class, 3, ETTranslationKey.BLOCK.EXTENDED_CRAFTING_ELITE, "extendedcrafting", "elite_table"),
        EXTENDED_CRAFTING_ULTIMATE("extended_crafting/ultimate", RecipeFamily.EXTENDED_CRAFTING, ExtendedCrafting.class, 4, ETTranslationKey.BLOCK.EXTENDED_CRAFTING_ULTIMATE, "extendedcrafting", "ultimate_table"),
        AVARITIA_NEO_EXTREME("avaritia_neo/extreme", RecipeFamily.AVARITIA_NEO, AvaritiaNeo.class, 4, ETTranslationKey.BLOCK.AVARITIA_NEO_EXTREME, "avaritia", "extreme_crafting_table"),
        RE_AVARITIA_SCULK("re_avaritia/sculk", RecipeFamily.RE_AVARITIA, ReAvaritia.class, 1, ETTranslationKey.BLOCK.RE_AVARITIA_SCULK, "avaritia", "sculk_crafting_table"),
        RE_AVARITIA_NETHER("re_avaritia/nether", RecipeFamily.RE_AVARITIA, ReAvaritia.class, 2, ETTranslationKey.BLOCK.RE_AVARITIA_NETHER, "avaritia", "nether_crafting_table"),
        RE_AVARITIA_END("re_avaritia/end", RecipeFamily.RE_AVARITIA, ReAvaritia.class, 3, ETTranslationKey.BLOCK.RE_AVARITIA_END, "avaritia", "end_crafting_table"),
        RE_AVARITIA_EXTREME("re_avaritia/extreme", RecipeFamily.RE_AVARITIA, ReAvaritia.class, 4, ETTranslationKey.BLOCK.RE_AVARITIA_EXTREME, "avaritia", "extreme_crafting_table");

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
        VANILLA,
        EXTENDED_CRAFTING,
        AVARITIA_NEO,
        RE_AVARITIA
    }

    public record UnitedRecipe(UnitedTerminalMenu menu, UnitedRecipeKind kind,
                               Recipe<?> recipe, CraftingContainer input, int left, int top) {
        public UnitedRecipe(UnitedTerminalMenu menu, UnitedRecipeKind kind, Recipe<?> recipe, CraftingContainer input) {
            this(menu, kind, recipe, input,
                    (menu.getCraftingGridWidth() - input.getWidth()) / 2,
                    (menu.getCraftingGridHeight() - input.getHeight()) / 2);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public boolean matches(CraftingContainer input, Level level) {
            return ((Recipe) recipe).matches(input, level);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public ItemStack assemble(Level level) {
            return ((Recipe) recipe).assemble(input, level.registryAccess());
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public NonNullList<ItemStack> remainingItems() {
            NonNullList<ItemStack> compact = ((Recipe) recipe).getRemainingItems(input);
            var remaining = NonNullList.withSize(menu.getCraftingGridSize(), ItemStack.EMPTY);
            for (int y = 0; y < input.getHeight(); y++) {
                for (int x = 0; x < input.getWidth(); x++) {
                    if (left + x < menu.getCraftingGridWidth() && top + y < menu.getCraftingGridHeight()) {
                        remaining.set((top + y) * menu.getCraftingGridWidth() + left + x,
                                compact.get(y * input.getWidth() + x));
                    }
                }
            }
            return remaining;
        }
    }
}
