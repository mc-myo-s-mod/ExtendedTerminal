package me.myogoo.extendedterminal.me.host;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.InternalInventory;
import appeng.menu.ISubMenu;
import appeng.util.inv.AppEngInternalInventory;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class ExtendedCraftingWTHost extends WTMenuHost implements IViewCellStorage {
    private final ETMenuType terminalType;
    private final AppEngInternalInventory craftingGrid;

    public ExtendedCraftingWTHost(Player player, @Nullable Integer inventorySlot, ItemStack itemStack,
            BiConsumer<Player, ISubMenu> returnToMainMenu, ETMenuType terminalType) {
        super(player, inventorySlot, itemStack, returnToMainMenu);
        this.terminalType = terminalType;
        this.craftingGrid = new AppEngInternalInventory(this, terminalType.getGridSize());
        readFromNbt();
    }

    public ETMenuType getTerminalType() {
        return terminalType;
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return getItemStack();
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        return id.equals(terminalType.getCraftingInventory()) ? craftingGrid : super.getSubInventory(id);
    }

    @Override
    protected void readFromNbt() {
        super.readFromNbt();
        craftingGrid.readFromNBT(getItemStack().getOrCreateTag(), terminalType.getIdAsString() + "CraftingGrid");
    }

    @Override
    public void saveChanges() {
        super.saveChanges();
        craftingGrid.writeToNBT(getItemStack().getOrCreateTag(), terminalType.getIdAsString() + "CraftingGrid");
    }
}
