package me.myogoo.extendedterminal.integration.itemList.module.avaritia;

import me.myogoo.extendedterminal.api.integration.itemList.IGuiSlotToIngredientMap;
import me.myogoo.extendedterminal.integration.itemList.module.extendedcrafting.ECRecipeTransferHelper;

public class AVRecipeTransferHelper {
    public static IGuiSlotToIngredientMap GuiSlotToIngredientMap =
            ECRecipeTransferHelper::getGuiSlotToIngredientMap;
}
