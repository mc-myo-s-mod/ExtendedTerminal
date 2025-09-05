package me.myogoo.extendedterminal.integration.jei;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiGuiHandler;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.integration.ItemListModLoadHelper;
import me.myogoo.extendedterminal.util.SafeClass;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class JeiRegisterHelper {
    public static void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        ItemListModLoadHelper
                .invokeItemListMod(ETJeiRecipeCatalyst.class, IRecipeCatalystRegistration.class, registration);
    }

    public static void registerRecipeTransfer(@NotNull IRecipeTransferRegistration registration) {
        ItemListModLoadHelper
                .invokeItemListMod(ETJeiRecipeTransfer.class, IRecipeTransferRegistration.class, registration);
    }

    public static void registerGuiHandler(@NotNull IGuiHandlerRegistration registration) {
        ItemListModLoadHelper
                .invokeItemListMod(ETJeiGuiHandler.class, IGuiHandlerRegistration.class, registration);
    }
}
