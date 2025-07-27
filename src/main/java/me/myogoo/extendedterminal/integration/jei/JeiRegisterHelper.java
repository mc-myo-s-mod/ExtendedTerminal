package me.myogoo.extendedterminal.integration.jei;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.api.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.util.SafeClass;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class JeiRegisterHelper {
    public static void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        ModLoadHelper
                .getScanData()
                .getAnnotations()
                .stream()
                .filter(a -> a.annotationType().equals(Type.getType(ETJeiRecipeCatalyst.class)))
                .map(a -> SafeClass.forType(a.clazz()))
                .filter(a -> Arrays.stream(a.getAnnotations()).anyMatch(b -> ModLoadHelper.get(b.annotationType())))
                .forEach(x -> {
                    try {
                        Method method = x.getDeclaredMethod("init", IRecipeCatalystRegistration.class);
                        method.invoke(null,registration);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        ExtendedTerminal.LOGGER.error("Failed to register JEI recipe catalyst for class: {}", x.getName(), e);
                    }
                });
    }

    public static void registerRecipeTransfer(@NotNull IRecipeTransferRegistration registration) {
        ModLoadHelper
                .getScanData()
                .getAnnotations()
                .stream()
                .filter(a -> a.annotationType().equals(Type.getType(ETJeiRecipeTransfer.class)))
                .map(a -> SafeClass.forType(a.clazz()))
                .filter(a -> Arrays.stream(a.getAnnotations()).anyMatch(b -> ModLoadHelper.get(b.annotationType())))
                .forEach(x -> {
                    try {
                        Method method = x.getDeclaredMethod("init", IRecipeTransferRegistration.class);
                        method.invoke(null,registration);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        ExtendedTerminal.LOGGER.error("Failed to register JEI recipe transfer for class: {}", x.getName(), e);
                    }
                });
    }
}
