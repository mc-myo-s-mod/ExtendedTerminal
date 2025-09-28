package me.myogoo.extendedterminal.menu.extendedterminal;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.me.storage.LinkStatusRespectingInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.menu.slot.CraftingTermSlot;
import com.google.common.base.Preconditions;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.slot.ETSmithingSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.slot.ETStoneCutterSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.SmithingInventory;
import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.StoneCutterInventory;

public class ETTerminalMenu extends ETTerminalBaseMenu<CraftingRecipe> {
    public static final MenuType<ETTerminalMenu> TYPE = MenuTypeBuilder
            .create(ETTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ETMenuType.ET_TERMINAL.getId());

    private static final String ACTION_SET_STONECUTTING_RECIPE_ID = "setStonecuttingRecipeId";
    private static final String ACTION_SET_MODE = "setMode";
    private static final String ACTION_UPDATE_STONECUTTER_RECIPES = "updateStoneCutterRecipes";

    // ---------------------------------------------------------------------
    // Fields : 공용
    // ---------------------------------------------------------------------
    private final ISegmentedInventory craftingInventoryHost;
    @GuiSync(0)
    public ETTerminalMode currentMode = ETTerminalMode.CRAFTING;

    private final CraftingMatrixSlot[] craftingSlots;
    private final CraftingTermSlot outputSlot;
    private CraftingInput lastTestedCraftingInput;
    private RecipeHolder<CraftingRecipe> craftingRecipe;

    private final CraftingMatrixSlot smithingTemplateSlot;
    private final CraftingMatrixSlot smithingBaseSlot;
    private final CraftingMatrixSlot smithingAdditionSlot;
    private final ETSmithingSlot smithingOutputSlot;
    private SmithingRecipeInput lastTestedSmithingInput;
    private RecipeHolder<SmithingRecipe> smithingRecipe;

    private final CraftingMatrixSlot stonecuttingSlot;
    private final ETStoneCutterSlot stoneCutterOutputSlot;
    private List<RecipeHolder<StonecutterRecipe>> stoneCutterRecipes = new ArrayList<>();
    @GuiSync(1)
    private ResourceLocation stoneCutterRecipeId = null;
    private ItemStack lastTestedStoneCutterInputItem = ItemStack.EMPTY;

    public ETTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.ET_TERMINAL, ETConfig.ADVANCED_TERMINAL_CONFIG);
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];

        var player = getPlayerInventory().player;
        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(this.menuType.getCraftingInventory());
        for (int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this, craftingGridInv, i), this.menuType.getSlotSemanticGrid());
        }

        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = new CraftingTermSlot(player, this.getActionSource(),
                        this.energySource, linkStatusInventory, craftingGridInv, craftingGridInv, this),
                this.menuType.getSlotSemanticResult());

        // Smithing Table
        var smithingInv = this.craftingInventoryHost.getSubInventory(SmithingInventory);
        this.addSlot(this.smithingTemplateSlot = new CraftingMatrixSlot(this, smithingInv, 0), SlotSemantics.SMITHING_TABLE_TEMPLATE);
        this.addSlot(this.smithingBaseSlot = new CraftingMatrixSlot(this, smithingInv, 1), SlotSemantics.SMITHING_TABLE_BASE);
        this.addSlot(this.smithingAdditionSlot = new CraftingMatrixSlot(this, smithingInv, 2), SlotSemantics.SMITHING_TABLE_ADDITION);
        this.addSlot(this.smithingOutputSlot = new ETSmithingSlot(player, this.getActionSource(),
                this.energySource, linkStatusInventory, smithingInv, smithingInv, this), SlotSemantics.SMITHING_TABLE_RESULT);

        // Stonecutting
        var stonecuttingInv = this.craftingInventoryHost.getSubInventory(StoneCutterInventory);
        this.addSlot(this.stonecuttingSlot = new CraftingMatrixSlot(this, stonecuttingInv, 0), SlotSemantics.STONECUTTING_INPUT);
        this.addSlot(this.stoneCutterOutputSlot = new ETStoneCutterSlot(player, this.getActionSource(),
                this.energySource, linkStatusInventory, stonecuttingInv, stonecuttingInv, this), ETSlotSemantics.STONECUTTING_RESULT);

        updateCurrentRecipeAndOutput(true);

        registerClientAction(ACTION_SET_STONECUTTING_RECIPE_ID, ResourceLocation.class, this::setStoneCutterRecipeId);
        registerClientAction(ACTION_SET_MODE, ETTerminalMode.class, this::setMode);

    }

    public void setMode(ETTerminalMode mode) {
        if(isClientSide()) {
            sendClientAction(ACTION_SET_MODE, mode);
        } else {
            this.currentMode = mode;
            updateCurrentRecipeAndOutput(true);
        }
    }

    public ETTerminalMode getMode() {
        return this.currentMode;
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        switch (currentMode) {
            case CRAFTING -> updateCraftingOutput(forceUpdate);
            case SMITHING -> updateSmithingOutput(forceUpdate);
            case STONECUTTING -> updateStonecuttingOutput(forceUpdate);
            case null, default -> {}
        }
    }

    @Override
    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        CraftingMatrixSlot slot = craftingSlots[0];
        var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        PacketDistributor.sendToServer(p);
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return this.craftingInventoryHost.getSubInventory(menuType.getCraftingInventory());
    }

    public InternalInventory getSmithingInventory() {
        return this.craftingInventoryHost.getSubInventory(SmithingInventory);
    }

    public InternalInventory getStoneCutterInventory() {
        return this.craftingInventoryHost.getSubInventory(StoneCutterInventory);
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        if (this.getSlot(slot) instanceof ETCraftingBaseSlot<?, ?> craftingSlot) {
            switch (action) {
                case CRAFT_SHIFT, CRAFT_ALL, CRAFT_ITEM, CRAFT_STACK -> craftingSlot.doClick(action, player);
            }
            return;
        }
        super.doAction(player, action, slot, id);
    }

    // ---------------------------------------------------------------------
    // Crafting 섹션
    // ---------------------------------------------------------------------
    private void updateCraftingOutput(boolean forceUpdate) {
        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for (var craftingSlot : this.craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }
        var testInput = CraftingInput.of(3, 3, testItems);

        if (!forceUpdate && Objects.equals(lastTestedCraftingInput, testInput)) {
            return;
        }

        var level = getPlayer().level();
        this.currentRecipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, testInput, level)
                .orElse(null);
        this.lastTestedCraftingInput = testInput;

        if (this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            var outputItem = this.currentRecipe.value().getResultItem(level.registryAccess());
            this.outputSlot.set(outputItem);
        }
    }

    public @Nullable RecipeHolder<CraftingRecipe> getCraftingRecipe() {
        return this.currentRecipe;
    }

    private void updateSmithingOutput(boolean forceUpdate) {
        var smithingTestInput = new SmithingRecipeInput(smithingTemplateSlot.getItem().copy(), smithingBaseSlot.getItem().copy(), smithingAdditionSlot.getItem().copy());

        if (!forceUpdate && Objects.equals(lastTestedSmithingInput, smithingTestInput)) {
            return;
        }

        var level = getPlayer().level();
        var smithingRecipes = level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, smithingTestInput, level);
        if(smithingRecipes.isEmpty()) {
            this.smithingOutputSlot.set(ItemStack.EMPTY);
        } else {
            RecipeHolder<SmithingRecipe> recipeHolder = smithingRecipes.getFirst();
            var result = recipeHolder.value().assemble(smithingTestInput, level.registryAccess());;
            if(result.isItemEnabled(level.enabledFeatures())) {
                this.smithingRecipe = recipeHolder;
                this.smithingOutputSlot.set(result);
            }
        }
    }

    public @Nullable RecipeHolder<SmithingRecipe> getSmithingRecipe() {
        return this.smithingRecipe;
    }

    // ---------------------------------------------------------------------
    // Stonecutting 섹션
    // ---------------------------------------------------------------------
    private void updateStonecuttingOutput(boolean forceUpdate) {
        var input = stonecuttingSlot.getItem();
        lastTestedStoneCutterInputItem = input;
        stoneCutterRecipes.clear();

        if (input.isEmpty()) {
            stoneCutterRecipeId = null;
            stoneCutterOutputSlot.set(ItemStack.EMPTY);
            return;
        }

        updateStonecutterRecipes();
    }

    public void updateStonecutterRecipes() {
        var input = stonecuttingSlot.getItem();
        stoneCutterRecipes.clear();

        var level = getPlayer().level();
        var recipeManager = level.getRecipeManager();
        var recipeInput = new SingleRecipeInput(input);
        stoneCutterRecipes.addAll(
                recipeManager.getRecipesFor(RecipeType.STONECUTTING, recipeInput, level));

        if (stoneCutterRecipeId != null
                && stoneCutterRecipes.stream().noneMatch(r -> r.id().equals(stoneCutterRecipeId))) {
            stoneCutterRecipeId = null;
        }

    }

    public void setStoneCutterRecipeId(ResourceLocation stoneCutterRecipeId) {
        if (isClientSide()) {
            sendClientAction(ACTION_SET_STONECUTTING_RECIPE_ID, stoneCutterRecipeId);
        } else {
            this.stoneCutterRecipeId = stoneCutterRecipeId;
            var optionalRecipeHolder = getPlayer().level().getRecipeManager().byKey(stoneCutterRecipeId);
            if(optionalRecipeHolder.isPresent()) {
                var recipe = optionalRecipeHolder.get().value();
                stoneCutterOutputSlot.set(recipe.getResultItem(getPlayer().level().registryAccess()));
            } else {
                stoneCutterOutputSlot.set(ItemStack.EMPTY);
            }
        }
    }

    public ResourceLocation getStoneCutterRecipeId() {
        return stoneCutterRecipeId;
    }

    public List<RecipeHolder<StonecutterRecipe>> getStoneCutterRecipes() {
        return stoneCutterRecipes;
    }
}
