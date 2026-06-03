package me.myogoo.extendedterminal.integration;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.util.mod.AnnotationScanner;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.MyoMod;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

public class ItemListModLoadHelper {
    public static <R> void invokeItemListMod(
            Type markerAnnotation,
            Class<R> parameterType,
            R parameter
    ) {
        AnnotationScanner
                .getModAnnotations()
                .stream()
                .filter(a -> a.annotationType().equals(markerAnnotation))
                .map(a -> SafeClass.forType(a.clazz()))
                .filter(Objects::nonNull)
                .filter(ItemListModLoadHelper::hasNoIntegrationMarkerOrActiveIntegration)
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

    private static boolean hasNoIntegrationMarkerOrActiveIntegration(Class<?> clazz) {
        var integrationAnnotations = Arrays.stream(clazz.getDeclaredAnnotations())
                .filter(annotation -> isIntegrationAnnotation(annotation.annotationType()))
                .toList();

        return integrationAnnotations.isEmpty()
                || integrationAnnotations.stream()
                .anyMatch(annotation -> MyotusAPI.integrations().isLoaded(annotation.annotationType()));
    }

    private static boolean isIntegrationAnnotation(Class<? extends Annotation> annotationClass) {
        return annotationClass.isAnnotationPresent(MyoMod.class)
                || MyotusAPI.integrations().isRegistered(annotationClass);
    }

    private static <R> void invokeMethod(Class<?> clazz, Class<R> parameterType, R registration) {
        try {
            Method[] methods = clazz.getDeclaredMethods();
            for(var method: methods) {
                if(method.isAnnotationPresent(MyotusSubscriber.class)) {
                    if(!Modifier.isStatic(method.getModifiers())) {
                        ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated as a subscriber but is not static.", method.getName(), clazz.getName());
                        continue;
                    }
                    if(method.getParameterCount() == 1 && method.getParameterTypes()[0] == parameterType) {
                        method.setAccessible(true);
                        method.invoke(null, registration);
                    } else {
                        ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated as a subscriber but does not have the correct parameters.", method.getName(), clazz.getName());
                    }
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            ExtendedTerminal.LOGGER.error("Failed to register ItemListMod for class: {}", clazz.getName(), e);
        }
    }
}
