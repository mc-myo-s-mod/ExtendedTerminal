package me.myogoo.extendedterminal.integration.jei;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiGuiHandler;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
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
        ModLoadHelper
                .getScanData()
                .getAnnotations()
                .stream()
                .filter(a -> a.annotationType().equals(Type.getType(ETJeiRecipeCatalyst.class)))
                .map(a -> SafeClass.forType(a.clazz()))
                .filter(a -> Arrays.stream(a.getDeclaredAnnotations()).anyMatch(b -> ModLoadHelper.get(b.annotationType())))
                .forEach(x -> {
                    try {
                        Method[] methods = x.getDeclaredMethods();
                        for(var method: methods) {
                            if(method.isAnnotationPresent(SubscribeLoadEvent.class)) {
                                if(!Modifier.isStatic(method.getModifiers())) {
                                    ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated with @SubscribeLoadEvent but is not static.", method.getName(), x.getName());
                                    continue;
                                }
                                if(method.getParameterCount() == 1 && method.getParameterTypes()[0] == IRecipeCatalystRegistration.class) {
                                    method.setAccessible(true);

                                    method.invoke(null, registration);
                                } else {
                                    ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated with @SubscribeLoadEvent but does not have the correct parameters.", method.getName(), x.getName());
                                }
                            }
                        }
                    } catch (InvocationTargetException | IllegalAccessException e) {
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
                .filter(a -> Arrays.stream(a.getDeclaredAnnotations()).anyMatch(b -> ModLoadHelper.get(b.annotationType())))
                .forEach(x -> {
                    try {
                        Method[] methods = x.getDeclaredMethods();
                        for(var method: methods) {
                            if(method.isAnnotationPresent(SubscribeLoadEvent.class)) {
                                if(!Modifier.isStatic(method.getModifiers())) {
                                    ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated with @SubscribeLoadEvent but is not static.", method.getName(), x.getName());
                                    continue;
                                }
                                if(method.getParameterCount() == 1 && method.getParameterTypes()[0] == IRecipeTransferRegistration.class) {
                                    method.setAccessible(true);
                                    method.invoke(null, registration);
                                } else {
                                    ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated with @SubscribeLoadEvent but does not have the correct parameters.", method.getName(), x.getName());
                                }
                            }
                        }
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        ExtendedTerminal.LOGGER.error("Failed to register JEI recipe catalyst for class: {}", x.getName(), e);
                    }
                });
    }

    public static void registerGuiHandler(@NotNull IGuiHandlerRegistration registration) {
        ModLoadHelper
                .getScanData()
                .getAnnotations()
                .stream()
                .filter(a -> a.annotationType().equals(Type.getType(ETJeiGuiHandler.class)))
                .map(a -> SafeClass.forType(a.clazz()))
                .filter(a -> Arrays.stream(a.getDeclaredAnnotations()).anyMatch(b -> ModLoadHelper.get(b.annotationType())))
                .forEach(x -> {
                    try {
                        Method[] methods = x.getDeclaredMethods();
                        for(var method: methods) {
                            if(method.isAnnotationPresent(SubscribeLoadEvent.class)) {
                                if(!Modifier.isStatic(method.getModifiers())) {
                                    ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated with @SubscribeLoadEvent but is not static.", method.getName(), x.getName());
                                    continue;
                                }
                                if(method.getParameterCount() == 1 && method.getParameterTypes()[0] == IGuiHandlerRegistration.class) {
                                    method.setAccessible(true);

                                    method.invoke(null, registration);
                                } else {
                                    ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated with @SubscribeLoadEvent but does not have the correct parameters.", method.getName(), x.getName());
                                }
                            }
                        }
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        ExtendedTerminal.LOGGER.error("Failed to register JEI recipe catalyst for class: {}", x.getName(), e);
                    }
                });
    }
}
