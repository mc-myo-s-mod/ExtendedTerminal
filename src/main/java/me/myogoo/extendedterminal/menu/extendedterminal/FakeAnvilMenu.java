package me.myogoo.extendedterminal.menu.extendedterminal;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

public class FakeAnvilMenu extends AnvilMenu {
    public FakeAnvilMenu(int containerId, Inventory playerInventory) {
        super(containerId, playerInventory);
    }

    public ItemStack getResultItem() {
        return slots.get(2).getItem();
    }

    public Container getInputSlots() {
        return this.inputSlots;
    }

    public void et$onTake(Player player, ItemStack stack) {
        super.onTake(player, stack);
    }

    public boolean et$mayPickup(Player player, boolean stack) {
        return super.mayPickup(player, stack);
    }
}
