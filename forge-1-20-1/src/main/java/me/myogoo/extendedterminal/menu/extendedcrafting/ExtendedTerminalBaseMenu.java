package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.MEStorage;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantic;
import appeng.menu.slot.CraftingMatrixSlot;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.google.common.base.Preconditions;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.ExCraftingTerminalSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ExtendedTerminalBaseMenu extends ETTerminalBaseMenu<ITableRecipe> {
    protected final ETCraftingBaseSlot<?, ?> outputSlot;
    private final ISegmentedInventory craftingInventoryHost;
    protected final CraftingMatrixSlot[] craftingSlots;
    private final CraftingContainer recipeTestContainer;
    @Nullable
    protected List<ItemStack> lastTestedItems;

    public ExtendedTerminalBaseMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, ETMenuType etMenuType, IETTerminalConfig config) {
        super(menuType, id, ip, host, etMenuType, config);
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];
        this.recipeTestContainer = new TransientCraftingContainer(this,this.menuType.getGridSideLength() , this.menuType.getGridSideLength());
        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(this.menuType.getCraftingInventory());
        for(int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this,craftingGridInv,i), this.menuType.getSlotSemanticGrid());
        }

        this.addSlot(this.outputSlot = createOutputSlot(host.getInventory(), craftingGridInv),
                this.menuType.getSlotSemanticResult());

        if (etMenuType != ETMenuType.UNITED_TERMINAL) {
            updateCurrentRecipeAndOutput(true);
        }
    }

    protected ETCraftingBaseSlot<?, ?> createOutputSlot(MEStorage storage, InternalInventory craftingGridInv) {
        return new ExCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                this.powerSource, storage, craftingGridInv, craftingGridInv, this, this.menuType);
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
        if(checkCraftingOnlyActive()) {
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

    protected List<ItemStack> getCraftingSlotItems() {
        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for (var craftingSlot : craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }
        return testItems;
    }

    public CraftingContainer createTableInput(List<ItemStack> items, @Nullable ITableRecipe recipe) {
        var positioned = NonNullList.withSize(this.menuType.getGridSize(), ItemStack.EMPTY);
        for (int i = 0; i < positioned.size() && i < items.size(); i++) {
            positioned.set(i, items.get(i).copy());
        }
        return new TransientCraftingContainer(this, this.menuType.getGridSideLength(), this.menuType.getGridSideLength(), positioned);
    }

    protected int getInputSideLength(@Nullable ITableRecipe recipe) {
        return this.menuType.getGridSideLength();
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
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        var s = this.getSlot(slot);
        if(s instanceof ExCraftingTerminalSlot craftingSlot) {
            switch (action) {
                case CRAFT_SHIFT:
                case CRAFT_ALL:
                case CRAFT_ITEM:
                case CRAFT_STACK:
                    craftingSlot.doClick(action, player);
                default:
                    return;
            }
        }
        super.doAction(player, action, slot, id);
    }
}
