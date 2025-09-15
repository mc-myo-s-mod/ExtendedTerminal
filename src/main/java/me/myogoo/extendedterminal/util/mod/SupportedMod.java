package me.myogoo.extendedterminal.util.mod;

import me.myogoo.extendedterminal.api.ModAccessor;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

public enum SupportedMod {

    ExtendedCrafting("extendedcrafting", ModAccessor.ExtendedCrafting.class),
    ReAvaritia("avaritia", ModAccessor.ReAvaritia.class, "Re-Avaritia"),
    AvaritiaNeo("avaritia", ModAccessor.AvaritiaNeo.class, "Avaritia");

    private final String modId;
    private final Class<? extends Annotation> annotationClass;
    private final Predicate<IModInfo> customLoadCondition;

    SupportedMod(String modId, Class<? extends Annotation> annotationClass) {
        this(modId, annotationClass, modInfo -> true);
    }

    SupportedMod(String modId, Class<? extends Annotation> annotationClass, String displayModName) {
        this(modId, annotationClass, modInfo -> displayModName.equals(modInfo.getDisplayName()));
    }

    SupportedMod(String modId, Class<? extends Annotation> annotationClass, Predicate<IModInfo> customLoadCondition) {
        this.modId = modId;
        this.annotationClass = annotationClass;
        this.customLoadCondition = customLoadCondition;
    }

    public String getModId() {
        return modId;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public boolean isLoaded() {
        return ModList.get().getModContainerById(modId)
                .map(container -> customLoadCondition.test(container.getModInfo()))
                .orElse(false);
    }
}
