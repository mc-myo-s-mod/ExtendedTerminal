package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.EpicTableCategory;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.extendedterminal.integration.jei.extendedcrafting.handler.ECJeiRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.extendedcrafting.*;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import me.myogoo.extendedterminal.api.annotation.EpicExCrafting;

@EpicExCrafting
@JEI
@RecipeTransfer
public class EXCRecipeTransferHandler {
    @MyotusSubscriber
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();
        registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(EpicTerminalMenu.class, EpicTerminalMenu.TYPE, EpicTableCategory.RECIPE_TYPE, helper), EpicTableCategory.RECIPE_TYPE);
    }
}
