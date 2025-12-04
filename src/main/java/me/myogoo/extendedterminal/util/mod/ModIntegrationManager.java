package me.myogoo.extendedterminal.util.mod;

import me.myogoo.extendedterminal.ExtendedTerminal;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 다른 모드와의 호환성 및 연동을 관리하는 유틸리티 클래스.
 * 어떤 모드가 로드되었는지 확인하고, 관련 어노테이션을 활성화합니다.
 */
public final class ModIntegrationManager { // final 클래스로 변경하여 상속 방지
    private static final Logger LOGGER = ExtendedTerminal.LOGGER;

    private static final Map<SupportedMod, Class<? extends Annotation>> activeIntegrations = new HashMap<>();

    public static void initialize() {
        LOGGER.info("Checking for mod integrations...");
        activeIntegrations.clear(); // 초기화 시 항상 비움

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

    /**
     * 특정 어노테이션 클래스에 해당하는 모드 연동이 활성화되었는지 확인합니다.
     *
     * @param annotationClass 확인할 어노테이션 클래스
     * @return 활성화 여부
     */
    public static boolean isLoaded(Class<? extends Annotation> annotationClass) {
        return activeIntegrations.containsValue(annotationClass);
    }

    /**
     * 특정 SupportedMod Enum에 해당하는 모드 연동이 활성화되었는지 확인합니다.
     *
     * @param mod 확인할 SupportedMod
     * @return 활성화 여부
     */
    public static boolean isLoaded(SupportedMod mod) {
        return activeIntegrations.containsKey(mod);
    }

    // 유틸리티 클래스이므로 인스턴스화 방지
    private ModIntegrationManager() {}
}