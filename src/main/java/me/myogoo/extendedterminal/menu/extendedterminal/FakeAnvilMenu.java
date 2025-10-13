package me.myogoo.extendedterminal.menu.extendedterminal;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

public class FakeAnvilMenu extends AnvilMenu {
    public FakeAnvilMenu(int containerId, Inventory playerInventory) {
        super(containerId, playerInventory);
    }

    public ItemStack getResultItem() {
        return slots.get(2).getItem();
    }
}
