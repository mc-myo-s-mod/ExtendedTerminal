package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.LegendaryTableCategory;
import me.myogoo.extendedterminal.api.annotation.LegendaryExCrafting;
import me.myogoo.extendedterminal.integration.jei.extendedcrafting.handler.ECJeiRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.extendedcrafting.LegendaryTerminalMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@LegendaryExCrafting
@JEI
@RecipeTransfer
public class LegendaryRecipeTransferHandler {
    @MyotusSubscriber
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();
        registration.addRecipeTransferHandler(
                new ECJeiRecipeTransferHandler<>(LegendaryTerminalMenu.class, LegendaryTerminalMenu.TYPE,
                        LegendaryTableCategory.RECIPE_TYPE, helper),
                LegendaryTableCategory.RECIPE_TYPE);
    }
}
