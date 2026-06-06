package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.MEStorage;
import appeng.helpers.InventoryAction;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.api.common.crafting.TierInput;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.Polymorph;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
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
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.illusivesoulworks.polymorph.api.PolymorphApi;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UnitedTerminalMenu extends ExtendedTerminalBaseMenu {
    private static final String ACTION_SELECT_NEXT_RECIPE_KIND = "selectNextRecipeKind";
    public static final MenuType<UnitedTerminalMenu> TYPE = MenuTypeBuilder
            .create(UnitedTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ETMenuType.UNITED_TERMINAL.getId());

    @Nullable
    private UnitedRecipe currentUnitedRecipe;
    @GuiSync(0)
    private UnitedRecipeKind selectedRecipeKind = UnitedRecipeKind.EXTENDED_CRAFTING;
    @Nullable
    private List<ItemStack> lastUnitedItems;
    @Nullable
    private RecipeInput lastUnitedInput;

    public UnitedTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.UNITED_TERMINAL, ExtendedCraftingConfig.INSTANCE.getUltimateConfig());
        registerClientAction(ACTION_SELECT_NEXT_RECIPE_KIND, this::selectNextRecipeKind);
        updateCurrentRecipeAndOutput(true);
    }

    @Override
    protected ETCraftingBaseSlot<?, ?> createOutputSlot(MEStorage storage, InternalInventory craftingGridInv) {
        return new UnitedCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                this.energySource, storage, craftingGridInv, craftingGridInv, this, this.menuType);
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        if (checkCraftingOnlyActive()) return;

        normalizeSelectedRecipeKind();
        var testItems = getCraftingSlotItems();
        var recipe = findUnitedRecipe(testItems);
        var testInput = recipe == null
                ? (this.selectedRecipeKind == UnitedRecipeKind.VANILLA_CRAFTING
                ? createVanillaCraftingInput(testItems)
                : createTableInput(testItems, null))
                : recipe.input();

        if (!forceUpdate && Objects.equals(this.lastUnitedInput, testInput)) {
            return;
        }

        this.currentUnitedRecipe = recipe;
        this.currentRecipe = recipe != null && recipe.kind == UnitedRecipeKind.EXTENDED_CRAFTING
                ? recipe.castTableRecipeHolder()
                : null;
        this.lastUnitedInput = testInput;

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
        this.currentUnitedRecipe = null;
        this.lastUnitedItems = null;
        updateCurrentRecipeAndOutput(true);
    }

    public void selectNextRecipeKind() {
        if (isClientSide()) {
            sendClientAction(ACTION_SELECT_NEXT_RECIPE_KIND);
            return;
        }
        var values = getActiveRecipeKinds();
        if (values.size() <= 1) {
            return;
        }
        var currentIndex = values.indexOf(this.selectedRecipeKind);
        setSelectedRecipeKind(values.get((currentIndex + 1) % values.size()));
    }

    private void normalizeSelectedRecipeKind() {
        if (this.selectedRecipeKind != null && this.selectedRecipeKind.isActive()) {
            return;
        }
        var values = getActiveRecipeKinds();
        this.selectedRecipeKind = values.isEmpty() ? UnitedRecipeKind.VANILLA_CRAFTING : values.getFirst();
    }

    public static List<UnitedRecipeKind> getActiveRecipeKinds() {
        var kinds = new ArrayList<UnitedRecipeKind>(4);
        kinds.add(UnitedRecipeKind.VANILLA_CRAFTING);
        if (UnitedRecipeKind.EXTENDED_CRAFTING.isActive()) {
            kinds.add(UnitedRecipeKind.EXTENDED_CRAFTING);
        }
        if (UnitedRecipeKind.AVARITIA_NEO.isActive()) {
            kinds.add(UnitedRecipeKind.AVARITIA_NEO);
        }
        if (UnitedRecipeKind.RE_AVARITIA.isActive()) {
            kinds.add(UnitedRecipeKind.RE_AVARITIA);
        }
        return kinds;
    }

    @Nullable
    public UnitedRecipe findUnitedRecipe(List<ItemStack> items, UnitedRecipeKind kind) {
        if (kind != UnitedRecipeKind.VANILLA_CRAFTING && !kind.isActive()) {
            return null;
        }
        var level = getPlayer().level();
        return switch (kind) {
            case VANILLA_CRAFTING -> findVanillaCraftingRecipe(level, items);
            case EXTENDED_CRAFTING -> findExtendedCraftingRecipe(level, items);
            case AVARITIA_NEO -> findAvaritiaNeoRecipe(level, items);
            case RE_AVARITIA -> findReAvaritiaRecipe(level, items);
        };
    }

    @Nullable
    private UnitedRecipe findVanillaCraftingRecipe(Level level, List<ItemStack> items) {
        var input = createVanillaCraftingInput(items);
        var recipe = MyotusAPI.integrations().isLoaded(Polymorph.class)
                ? PolymorphApi.getInstance().getRecipeManager().getPlayerRecipe(this, RecipeType.CRAFTING, input, level, getPlayer()).orElse(null)
                : level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).orElse(null);
        return recipe == null ? null : new UnitedRecipe(this, UnitedRecipeKind.VANILLA_CRAFTING, recipe, input);
    }

    private CraftingInput createVanillaCraftingInput(List<ItemStack> items) {
        var positioned = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int source = y * this.menuType.getGridSideLength() + x;
                int target = y * 3 + x;
                if (source >= 0 && source < items.size()) {
                    positioned.set(target, items.get(source).copy());
                }
            }
        }
        return CraftingInput.of(3, 3, positioned);
    }

    @Nullable
    private UnitedRecipe findExtendedCraftingRecipe(Level level, List<ItemStack> items) {
        for (var recipe : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.TABLE.get())) {
            var input = createTableInput(items, recipe.value());
            if (recipe.value().matches(input, level)) {
                return new UnitedRecipe(this, UnitedRecipeKind.EXTENDED_CRAFTING, recipe, input);
            }
        }
        return null;
    }

    @Nullable
    private UnitedRecipe findAvaritiaNeoRecipe(Level level, List<ItemStack> items) {
        try {
            var input = CraftingInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(), items);
            return level.getRecipeManager().getRecipeFor(AvaritiaRecipes.EXTREME_CRAFTING.get(), input, level)
                    .map(recipe -> new UnitedRecipe(this, UnitedRecipeKind.AVARITIA_NEO, recipe, input))
                    .orElse(null);
        } catch (LinkageError ignored) {
            return null;
        }
    }

    @Nullable
    private UnitedRecipe findReAvaritiaRecipe(Level level, List<ItemStack> items) {
        try {
            for (int tier = 4; tier >= 1; tier--) {
                var input = TierInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(), items, tier);
                var recipe = level.getRecipeManager().getRecipeFor(committee.nova.mods.avaritia.init.registry.ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), input, level);
                if (recipe.isPresent()) {
                    return new UnitedRecipe(this, UnitedRecipeKind.RE_AVARITIA, recipe.get(), input);
                }
            }
            return null;
        } catch (LinkageError ignored) {
            return null;
        }
    }

    @Override
    protected int getInputTier(@Nullable ITableRecipe recipe) {
        if (recipe == null) {
            return 0;
        }
        var tier = recipe.getTier();
        return tier > 0 ? tier : 0;
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
        VANILLA_CRAFTING(null),
        EXTENDED_CRAFTING(ExtendedCrafting.class),
        AVARITIA_NEO(AvaritiaNeo.class),
        RE_AVARITIA(ReAvaritia.class);

        @Nullable
        private final Class<? extends Annotation> integration;

        UnitedRecipeKind(@Nullable Class<? extends Annotation> integration) {
            this.integration = integration;
        }

        public boolean isActive() {
            return integration == null || MyotusAPI.integrations().isLoaded(integration);
        }
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
