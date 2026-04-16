package me.myogoo.extendedterminal.menu.extendedterminal;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.menu.slot.CraftingTermSlot;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.myogoo.extendedterminal.api.host.IETTerminalHost;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.slot.ETAnvilSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.slot.ETSmithingSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.slot.ETStoneCutterSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.ANVIL_INVENTORY;
import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.SMITHING_INVENTORY;
import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.STONECUTTER_INVENTORY;

public class ETTerminalMenu extends ETTerminalBaseMenu<CraftingRecipe> {
    public static final MenuType<ETTerminalMenu> TYPE = MenuTypeBuilder
            .create(ETTerminalMenu::new, IETTerminalHost.class)
            .build(ETMenuType.ET_TERMINAL.getIdAsString());

    private static final String ACTION_SET_STONECUTTING_RECIPE_ID = "setStonecuttingRecipeId";
    private static final String ACTION_SET_MODE = "setMode";
    private static final String ACTION_SET_ANVIL_ITEM_NAME = "setAnvilItemName";

    private final ISegmentedInventory craftingInventoryHost;
    private final IETTerminalHost host;

    @GuiSync(0)
    private ETTerminalMode currentMode;

    private final CraftingMatrixSlot[] craftingSlots;
    private final CraftingTermSlot outputSlot;
    private final CraftingContainer craftingRecipeContainer;
    private CraftingRecipe craftingRecipe;

    private final CraftingMatrixSlot smithingTemplateSlot;
    private final CraftingMatrixSlot smithingBaseSlot;
    private final CraftingMatrixSlot smithingAdditionSlot;
    private final ETSmithingSlot smithingOutputSlot;
    private SmithingRecipe smithingRecipe;

    private final CraftingMatrixSlot stonecuttingSlot;
    private final ETStoneCutterSlot stoneCutterOutputSlot;
    private final List<StonecutterRecipe> stoneCutterRecipes = new ArrayList<>();
    @GuiSync(1)
    private ResourceLocation stoneCutterRecipeId = null;

    private final CraftingMatrixSlot anvilLeftSlot;
    private final CraftingMatrixSlot anvilRightSlot;
    private final ETAnvilSlot anvilOutputSlot;
    private final FakeAnvilMenu anvilDelegate;
    @GuiSync(2)
    private int anvilCost;
    private boolean handlingAnvilTake;

    public ETTerminalMenu(MenuType<?> menuType, int id, Inventory ip, IETTerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.ET_TERMINAL,
                ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig());
        this.host = host;
        this.currentMode = host.getMode();
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];
        this.craftingRecipeContainer = new TransientCraftingContainer(this,
                this.menuType.getGridSideLength(), this.menuType.getGridSideLength());

        var craftingGridInv = this.craftingInventoryHost.getSubInventory(this.menuType.getCraftingInventory());
        for (int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this, craftingGridInv, i),
                    this.menuType.getSlotSemanticGrid());
        }

        this.addSlot(this.outputSlot = new CraftingTermSlot(this.getPlayerInventory().player, this.getActionSource(),
                        this.powerSource, host.getInventory(), craftingGridInv, craftingGridInv, this),
                this.menuType.getSlotSemanticResult());

        var smithingInv = this.craftingInventoryHost.getSubInventory(SMITHING_INVENTORY);
        this.addSlot(this.smithingTemplateSlot = new CraftingMatrixSlot(this, smithingInv, 0),
                ETSlotSemantics.SMITHING_TABLE_TEMPLATE);
        this.addSlot(this.smithingBaseSlot = new CraftingMatrixSlot(this, smithingInv, 1),
                ETSlotSemantics.SMITHING_TABLE_BASE);
        this.addSlot(this.smithingAdditionSlot = new CraftingMatrixSlot(this, smithingInv, 2),
                ETSlotSemantics.SMITHING_TABLE_ADDITION);
        this.addSlot(this.smithingOutputSlot = new ETSmithingSlot(this.getPlayerInventory().player,
                        this.getActionSource(), this.powerSource, host.getInventory(), smithingInv, smithingInv, this),
                SlotSemantics.SMITHING_TABLE_RESULT);

        var stonecuttingInv = this.craftingInventoryHost.getSubInventory(STONECUTTER_INVENTORY);
        this.addSlot(this.stonecuttingSlot = new CraftingMatrixSlot(this, stonecuttingInv, 0),
                ETSlotSemantics.STONECUTTING_INPUT);
        this.addSlot(this.stoneCutterOutputSlot = new ETStoneCutterSlot(this.getPlayerInventory().player,
                        this.getActionSource(), this.powerSource, host.getInventory(), stonecuttingInv, stonecuttingInv, this),
                ETSlotSemantics.STONECUTTING_RESULT);

        var anvilInv = this.craftingInventoryHost.getSubInventory(ANVIL_INVENTORY);
        this.anvilDelegate = new FakeAnvilMenu(0, this.getPlayerInventory());
        this.addSlot(this.anvilLeftSlot = new CraftingMatrixSlot(this, anvilInv, 0), ETSlotSemantics.ANVIL_LEFT_INPUT);
        this.addSlot(this.anvilRightSlot = new CraftingMatrixSlot(this, anvilInv, 1), ETSlotSemantics.ANVIL_RIGHT_INPUT);
        this.addSlot(this.anvilOutputSlot = new ETAnvilSlot(this.getPlayerInventory().player, anvilInv, this.anvilDelegate, this),
                ETSlotSemantics.ANVIL_RESULT);
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return ETTerminalMenu.this.anvilCost;
            }

            @Override
            public void set(int value) {
                ETTerminalMenu.this.anvilCost = value;
            }
        });

        this.stoneCutterRecipeId = host.getStoneCutterRecipeId();
        registerClientAction(ACTION_SET_STONECUTTING_RECIPE_ID, ResourceLocation.class, this::setStoneCutterRecipeId);
        registerClientAction(ACTION_SET_MODE, ETTerminalMode.class, this::setMode);
        registerClientAction(ACTION_SET_ANVIL_ITEM_NAME, String.class, this::setAnvilItemName);

        updateCurrentRecipeAndOutput(true);
    }

    public ETTerminalMode getMode() {
        return this.currentMode;
    }

    public void setMode(ETTerminalMode mode) {
        if (isClientSide()) {
            this.currentMode = mode;
            sendClientAction(ACTION_SET_MODE, mode);
        } else {
            this.currentMode = mode;
            this.host.setMode(mode);
            updateCurrentRecipeAndOutput(true);
        }
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        switch (this.currentMode) {
            case CRAFTING -> updateCraftingOutput(forceUpdate);
            case SMITHING -> updateSmithingOutput(forceUpdate);
            case STONECUTTING -> updateStonecuttingOutput(forceUpdate);
            case ANVIL -> updateAnvilOutput(forceUpdate);
        }
    }

    @Override
    public void clearCraftingGrid() {
        clearInventory(this.craftingSlots[0]);
    }

    @Override
    public appeng.menu.SlotSemantic getCraftingGridSlotSemantic() {
        return this.menuType.getSlotSemanticGrid();
    }

    @Override
    public appeng.menu.SlotSemantic getOutputSlotSemantic() {
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

    public void clearSmithingGrid() {
        clearInventory(this.smithingTemplateSlot);
        clearInventory(this.smithingBaseSlot);
        clearInventory(this.smithingAdditionSlot);
    }

    private void clearInventory(AppEngSlot slot) {
        Preconditions.checkState(isClientSide());
        var packet = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        NetworkHandler.instance().sendToServer(packet);
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return getInventory(this.menuType.getCraftingInventory());
    }

    public InternalInventory getSmithingInventory() {
        return getInventory(SMITHING_INVENTORY);
    }

    public InternalInventory getStoneCutterInventory() {
        return getInventory(STONECUTTER_INVENTORY);
    }

    public InternalInventory getInventory(ResourceLocation id) {
        return this.craftingInventoryHost.getSubInventory(id);
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        if (this.getSlot(slot) instanceof ETCraftingBaseSlot<?, ?> craftingSlot) {
            switch (action) {
                case CRAFT_SHIFT, CRAFT_ALL, CRAFT_ITEM, CRAFT_STACK -> craftingSlot.doClick(action, player);
                default -> {
                }
            }
            return;
        }
        if (this.getSlot(slot) instanceof ETAnvilSlot) {
            return;
        }
        super.doAction(player, action, slot, id);
    }

    private void updateCraftingOutput(boolean forceUpdate) {
        if (checkCraftingOnlyActive()) {
            return;
        }

        boolean changed = forceUpdate;
        for (int i = 0; i < this.craftingSlots.length; i++) {
            var stack = this.craftingSlots[i].getItem();
            if (!ItemStack.isSameItemSameTags(stack, this.craftingRecipeContainer.getItem(i))) {
                this.craftingRecipeContainer.setItem(i, stack.copy());
                changed = true;
            }
        }

        if (!changed) {
            return;
        }

        var level = this.getPlayerInventory().player.level();
        this.currentRecipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, this.craftingRecipeContainer, level)
                .orElse(null);
        this.craftingRecipe = this.currentRecipe;
        if (this.craftingRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.craftingRecipe.assemble(this.craftingRecipeContainer, level.registryAccess()));
        }
    }

    public @Nullable SmithingRecipe getSmithingRecipe() {
        return this.smithingRecipe;
    }

    private void updateSmithingOutput(boolean forceUpdate) {
        var input = new SimpleContainer(
                this.smithingTemplateSlot.getItem().copy(),
                this.smithingBaseSlot.getItem().copy(),
                this.smithingAdditionSlot.getItem().copy());
        var level = getPlayer().level();
        this.smithingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMITHING, input, level).orElse(null);
        if (this.smithingRecipe == null) {
            this.smithingOutputSlot.set(ItemStack.EMPTY);
        } else {
            this.smithingOutputSlot.set(this.smithingRecipe.assemble(input, level.registryAccess()));
        }
    }

    private void updateStonecuttingOutput(boolean forceUpdate) {
        this.stoneCutterRecipes.clear();
        var input = this.stonecuttingSlot.getItem();
        if (input.isEmpty()) {
            this.stoneCutterRecipeId = null;
            this.stoneCutterOutputSlot.set(ItemStack.EMPTY);
            return;
        }
        updateStonecutterRecipes();
        if (this.stoneCutterRecipeId != null) {
            var recipe = this.getPlayer().level().getRecipeManager().byKey(this.stoneCutterRecipeId);
            if (recipe.isPresent() && recipe.get() instanceof StonecutterRecipe stonecutterRecipe) {
                this.stoneCutterOutputSlot.set(stonecutterRecipe.getResultItem(this.getPlayer().level().registryAccess()));
                return;
            }
        }
        this.stoneCutterOutputSlot.set(ItemStack.EMPTY);
    }

    public void updateStonecutterRecipes() {
        this.stoneCutterRecipes.clear();
        var input = this.stonecuttingSlot.getItem();
        if (input.isEmpty()) {
            return;
        }

        var level = this.getPlayer().level();
        this.stoneCutterRecipes.addAll(level.getRecipeManager().getRecipesFor(RecipeType.STONECUTTING, new SimpleContainer(input), level));
        if (this.stoneCutterRecipeId != null
                && this.stoneCutterRecipes.stream().noneMatch(recipe -> recipe.getId().equals(this.stoneCutterRecipeId))) {
            this.stoneCutterRecipeId = null;
        }
    }

    public void setStoneCutterRecipeId(ResourceLocation stoneCutterRecipeId) {
        if (isClientSide()) {
            this.stoneCutterRecipeId = stoneCutterRecipeId;
            sendClientAction(ACTION_SET_STONECUTTING_RECIPE_ID, stoneCutterRecipeId);
            return;
        }

        this.stoneCutterRecipeId = stoneCutterRecipeId;
        this.host.setStoneCutterRecipeId(stoneCutterRecipeId);
        var recipe = getPlayer().level().getRecipeManager().byKey(stoneCutterRecipeId);
        if (recipe.isPresent() && recipe.get() instanceof StonecutterRecipe stonecutterRecipe) {
            this.stoneCutterOutputSlot.set(stonecutterRecipe.getResultItem(getPlayer().level().registryAccess()));
        } else {
            this.stoneCutterOutputSlot.set(ItemStack.EMPTY);
        }
    }

    public @Nullable ResourceLocation getStoneCutterRecipeId() {
        return this.stoneCutterRecipeId;
    }

    public List<StonecutterRecipe> getStoneCutterRecipes() {
        return this.stoneCutterRecipes;
    }

    private void updateAnvilOutput(boolean forceUpdate) {
        if (this.handlingAnvilTake) {
            return;
        }
        this.anvilDelegate.slots.get(0).set(this.anvilLeftSlot.getItem());
        this.anvilDelegate.slots.get(1).set(this.anvilRightSlot.getItem());
        this.anvilDelegate.createResult();
        this.anvilOutputSlot.set(this.anvilDelegate.getResultItem());
        this.anvilCost = this.anvilDelegate.getCost();
    }

    public int getAnvilCost() {
        return this.anvilCost;
    }

    public void setAnvilItemName(String name) {
        if (isServerSide()) {
            this.anvilDelegate.slots.get(0).set(this.anvilLeftSlot.getItem());
            this.anvilDelegate.slots.get(1).set(this.anvilRightSlot.getItem());
            if (this.anvilDelegate.setItemName(name)) {
                updateCurrentRecipeAndOutput(true);
            }
        } else {
            sendClientAction(ACTION_SET_ANVIL_ITEM_NAME, name);
        }
    }

    public void onAnvilTake(Runnable updateLogic) {
        this.handlingAnvilTake = true;
        try {
            updateLogic.run();
        } finally {
            this.handlingAnvilTake = false;
        }
        updateAnvilOutput(true);
    }

    @Override
    public boolean hasIngredient(Ingredient ingredient, Object2IntOpenHashMap<Object> reservedAmounts) {
        List<Slot> slots = ETTerminalMode.loadableValues().stream()
                .map(ETTerminalMode::getSlotSemantics)
                .flatMap(Collection::stream)
                .map(this::getSlots)
                .flatMap(Collection::stream)
                .toList();

        for (var slot : slots) {
            var stackInSlot = slot.getItem();
            if (!stackInSlot.isEmpty() && ingredient.test(stackInSlot)) {
                var reservedAmount = reservedAmounts.getOrDefault(slot, 0);
                if (stackInSlot.getCount() > reservedAmount) {
                    reservedAmounts.merge(slot, 1, Integer::sum);
                    return true;
                }
            }
        }

        return super.hasIngredient(ingredient, reservedAmounts);
    }
}
