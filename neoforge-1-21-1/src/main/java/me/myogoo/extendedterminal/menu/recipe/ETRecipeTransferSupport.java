package me.myogoo.extendedterminal.menu.recipe;

import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Small shared support layer for ExtendedTerminal recipe-transfer behavior.
 *
 * <p>This is intentionally terminal/grid-facing only. Matrix result import belongs to
 * AE2HelpersMatrix's Matrix Result Import Core, which records accepted Matrix pattern outputs from
 * {@code ClusterAssemblerMatrix.pushCraftingJob} and imports adjacent expected-result inventories.
 * Keeping this class free of Matrix hooks prevents duplicate Matrix result-import behavior when
 * AE2HelpersMatrix is installed.</p>
 */
public final class ETRecipeTransferSupport {
    private ETRecipeTransferSupport() {
    }

    public static ItemStack takeIngredientFromOtherGrid(ETTerminalMenu menu, Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            return ItemStack.EMPTY;
        }

        var currentMode = menu.getMode();
        for (var mode : ETTerminalMode.loadableValues()) {
            if (mode == currentMode) {
                continue;
            }

            var inventory = menu.getInventory(mode.getInventoryId());
            for (int i = 0; i < inventory.size(); i++) {
                var item = inventory.getStackInSlot(i);
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
