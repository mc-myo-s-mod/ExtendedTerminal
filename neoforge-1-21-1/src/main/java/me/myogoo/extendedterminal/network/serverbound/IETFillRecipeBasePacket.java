package me.myogoo.extendedterminal.network.serverbound;

import appeng.helpers.ICraftingGridMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.recipe.ETRecipeTransferSupport;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface IETFillRecipeBasePacket {
    default ItemStack takeIngredientFromOtherGrid(ICraftingGridMenu cct, Ingredient ingredient) {
        if (!(cct instanceof ETTerminalMenu menu)) {
            return ItemStack.EMPTY;
        }
        return ETRecipeTransferSupport.takeIngredientFromOtherGrid(menu, ingredient);
    }
}