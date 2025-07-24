package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantic;
import appeng.menu.slot.CraftingMatrixSlot;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.google.common.base.Preconditions;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.slot.ETBaseCraftingSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

public class ExtendedTerminalBaseMenu extends ETBaseTerminalMenu<ITableRecipe> {
    private final ETBaseCraftingSlot outputSlot;
    private final ISegmentedInventory craftingInventoryHost;
    private final CraftingMatrixSlot[] craftingSlots;
    private final ETMenuType menuType;
    private final CraftingContainer recipeTestContainer;
    private final ETConfig.ExtendedCraftingConfig config;

    public ExtendedTerminalBaseMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, ETMenuType etMenuType, ETConfig.ExtendedCraftingConfig config) {
        super(menuType, id, ip, host);
        this.config = config;
        this.menuType = etMenuType;
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];
        this.recipeTestContainer = new TransientCraftingContainer(this,this.menuType.getGridSideLength() , this.menuType.getGridSideLength());
        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(this.menuType.getCraftingInventory());
        for(int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this,craftingGridInv,i), this.menuType.getSlotSemanticGrid());
        }

        this.addSlot(this.outputSlot = new ETBaseCraftingSlot(this.getPlayerInventory().player, this.getActionSource(),
                        this.powerSource, host.getInventory(), craftingGridInv, craftingGridInv, this, this.menuType),
                this.menuType.getSlotSemanticResult());

        updateCurrentRecipeAndOutput(true);
    }

    @Override
    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        CraftingMatrixSlot slot = craftingSlots[0];
        var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        NetworkHandler.instance().sendToServer(p);
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        if(config.enableCraftOnlyPowered() && (this.getNetworkNode() == null || (this.getNetworkNode() != null && !this.getNetworkNode().isActive()))) {
            return;
        }

        boolean hasChanged = forceUpdate;
        for (int x = 0; x < this.craftingSlots.length; x++) {
            var stack = this.craftingSlots[x].getItem();
            if (!ItemStack.isSameItemSameTags(stack, recipeTestContainer.getItem(x))) {
                hasChanged = true;
                recipeTestContainer.setItem(x, stack.copy());
            }
        }

        if (!hasChanged) {
            return;
        }

        var level = this.getPlayerInventory().player.level();
        this.currentRecipe = level.getRecipeManager().getRecipeFor(ModRecipeTypes.TABLE.get(), recipeTestContainer, level)
                .orElse(null);

        if(this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentRecipe.assemble(recipeTestContainer,level.registryAccess()));
        }
    }
    public ITableRecipe getCurrentRecipe() {
        return this.currentRecipe;
    }

    public ETMenuType getETMenuType() {
        return menuType;
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
    public boolean useRealItems() {
        return true;
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        var s = this.getSlot(slot);
        if(s instanceof ETBaseCraftingSlot craftingSlot) {
            switch (action) {
                case CRAFT_SHIFT:
                case CRAFT_ALL:
                case CRAFT_ITEM:
                case CRAFT_STACK:
                    craftingSlot.doClick(action, player);
            }
        } else {
            super.doAction(player, action, slot, id);
        }
    }
}
