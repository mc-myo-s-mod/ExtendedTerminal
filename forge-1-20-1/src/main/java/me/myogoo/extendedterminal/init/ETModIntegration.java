package me.myogoo.extendedterminal.init;

/**
 * Extended Terminal optional integration markers are declared with {@code @MyoMod}
 * on {@code api.annotation} annotations. Myotus discovers them from the public API jar,
 * so no runtime registrar call is needed here anymore.
 */
public final class ETModIntegration {
    private ETModIntegration() {
    }

    public static void initialize() {
        // Kept as a stable lifecycle hook for existing call sites.
    }
}
