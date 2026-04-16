package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.integration.jei.extendedcrafting.handler.ECJeiRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@ModAccessor.ExtendedCrafting
@ETJeiRecipeTransfer
public class ECRecipeTransferHandler {
    @MyotusSubscriber
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();

        registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(BasicTerminalMenu.class, BasicTerminalMenu.TYPE, BasicTableCategory.RECIPE_TYPE, helper), BasicTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(AdvancedTerminalMenu.class, AdvancedTerminalMenu.TYPE, AdvancedTableCategory.RECIPE_TYPE, helper), AdvancedTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(EliteTerminalMenu.class, EliteTerminalMenu.TYPE, EliteTableCategory.RECIPE_TYPE, helper), EliteTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(UltimateTerminalMenu.class, UltimateTerminalMenu.TYPE, UltimateTableCategory.RECIPE_TYPE, helper), UltimateTableCategory.RECIPE_TYPE);
    }
}
