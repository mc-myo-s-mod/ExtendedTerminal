package me.myogoo.extendedterminal.menu.extendedterminal;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.util.mod.ModIntegrationManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public enum ETTerminalMode {
    CRAFTING,
    STONECUTTING,
    SMITHING;

    public boolean canLoad() {
        try {
            Field field = ETTerminalMode.class.getField(this.name());
            if(field.getDeclaredAnnotations().length == 0) {
                return true;
            }
            return Arrays.stream(field.getDeclaredAnnotations())
                    .map(Annotation::annotationType)
                    .allMatch(ModIntegrationManager::isLoaded);

        } catch (NoSuchFieldException e) {
            ExtendedTerminal.LOGGER.error("Terminal Mode {} is not loaded due to missing field in ETTerminalMode", this.name());
        }
        return false;
    }

    public static List<ETTerminalMode> loadableValues() {
        return Arrays.stream(values()).filter(ETTerminalMode::canLoad).toList();
    }
}
