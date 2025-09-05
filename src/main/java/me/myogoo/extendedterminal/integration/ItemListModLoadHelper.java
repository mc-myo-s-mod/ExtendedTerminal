package me.myogoo.extendedterminal.integration;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.util.SafeClass;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ItemListModLoadHelper {
    public static <R> void invokeItemListMod(
            Type markerAnnotation,
            Class<R> parameterType,
            R parameter
    ) {
        ModLoadHelper
                .getLoadedAnnotation()
                .stream()
                .filter(a -> a.annotationType().equals(markerAnnotation))
                .map(a -> SafeClass.forType(a.clazz()))
                .filter(c -> Arrays.stream(c.getDeclaredAnnotations()).anyMatch(a -> ModLoadHelper.get(a.annotationType())))
                .forEach(clazz -> invokeMethod(clazz,parameterType, parameter));
    }

    public static <R> void invokeItemListMod(
            Class<?> markerAnnotation,
            Class<R> parameterType,
            R parameter
    ) {
        invokeItemListMod(
                Type.getType(markerAnnotation),
                parameterType,
                parameter
        );
    }

    private static <R> void invokeMethod(Class<?> clazz, Class<R> parameterType, R registration) {
        try {
            Method[] methods = clazz.getDeclaredMethods();
            for(var method: methods) {
                if(method.isAnnotationPresent(SubscribeLoadEvent.class)) {
                    if(!Modifier.isStatic(method.getModifiers())) {
                        ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated with @SubscribeLoadEvent but is not static.", method.getName(), clazz.getName());
                        continue;
                    }
                    if(method.getParameterCount() == 1 && method.getParameterTypes()[0] == parameterType) {
                        method.setAccessible(true);
                        method.invoke(null, registration);
                    } else {
                        ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated with @SubscribeLoadEvent but does not have the correct parameters.", method.getName(), clazz.getName());
                    }
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            ExtendedTerminal.LOGGER.error("Failed to register ItemListMod for class: {}", clazz.getName(), e);
        }
    }
}
