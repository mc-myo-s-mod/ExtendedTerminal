package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.EpicTableCategory;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.integration.jei.extendedcrafting.handler.ECJeiRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.extendedcrafting.*;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@ModAccessor.EpicExCrafting
@ETJeiRecipeTransfer
public class EXCRecipeTransferHandler {
    @SubscribeLoadEvent
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();
        registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(EpicTerminalMenu.class, EpicTerminalMenu.TYPE, EpicTableCategory.RECIPE_TYPE, helper), EpicTableCategory.RECIPE_TYPE);
    }
}
