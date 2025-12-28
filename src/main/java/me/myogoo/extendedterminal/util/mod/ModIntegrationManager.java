package me.myogoo.extendedterminal.util.mod;
import me.myogoo.extendedterminal.ExtendedTerminal;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.*;

public final class ModIntegrationManager {
    private static final Logger LOGGER = ExtendedTerminal.LOGGER;

    private static final Map<SupportedMod, Class<? extends Annotation>> activeIntegrations = new HashMap<>();

    public static void initialize() {
        LOGGER.info("Checking for mod integrations...");
        activeIntegrations.clear();

        for (SupportedMod mod : SupportedMod.values()) {
            if (mod.isLoaded()) {
                activeIntegrations.put(mod, mod.getAnnotationClass());
                LOGGER.info("Integration enabled for: {}", mod.name());
            }
        }
    }

    public static Class<? extends Annotation> getClass(SupportedMod mod) {
        return activeIntegrations.get(mod);
    }

    public static Class<? extends Annotation> getClass(String modId) {
        for (var value : activeIntegrations.values()) {
            if(value.getSimpleName().equals(modId)) {
                return value;
            }
        }
        return null;
    }

    public static boolean isLoaded(Class<? extends Annotation> annotationClass) {
        return activeIntegrations.containsValue(annotationClass);
    }

    public static boolean isLoaded(SupportedMod mod) {
        return activeIntegrations.containsKey(mod);
    }

    private ModIntegrationManager() {}
}