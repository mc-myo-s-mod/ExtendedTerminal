package me.myogoo.extendedterminal.util;

import me.myogoo.extendedterminal.ExtendedTerminal;
import org.objectweb.asm.Type;

public class SafeClass {
    public static Class<?> forName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            ExtendedTerminal.LOGGER.error("Can't find Class for : {}", String.valueOf(e));
            return null;
        }
    }

    public static Class<?> forType(Type type) {
        return forName(type.getClassName());
    }
}
