package me.myogoo.extendedterminal.network.serverbound;

import appeng.helpers.ICraftingGridMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface IETFillRecipeBasePacket {
    default ItemStack takeIngredientFromOtherGrid(ICraftingGridMenu cct, Ingredient ingredient) {
        if (!(cct instanceof ETTerminalMenu menu)) {
            return ItemStack.EMPTY;
        }
        var currentMode = menu.getMode();
        for (var mode : ETTerminalMode.loadableValues()) {
            if (mode == currentMode) {
                continue;
            }
            var inv = menu.getInventory(mode.getInventoryId());
            for (int i = 0; i < inv.size(); i++) {
                var item = inv.getStackInSlot(i);
                if (ingredient.test(item)) {
                    var result = item.split(1);
                    if (!result.isEmpty()) {
                        return result;
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }
}