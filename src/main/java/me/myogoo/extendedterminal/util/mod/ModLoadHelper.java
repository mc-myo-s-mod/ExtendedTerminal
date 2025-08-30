package me.myogoo.extendedterminal.util.mod;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.ModAccessor;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//TODO: Refactor this class
public class ModLoadHelper {
    private static final Map<Class<? extends Annotation>, Boolean> loadedAnnotations = new HashMap<>();
    private static final Logger logger = ExtendedTerminal.LOGGER;
    private final static String ExCrafting_ID = "extendedcrafting";
    private final static String Avaritia_ID = "avaritia";
    private final static String ReAvaritia = "Re-Avaritia";
    private final static String AvaritiaNeo = "Avaritia";
    private final static ModFileScanData ScanData = ModList.get()
            .getModFileById(ExtendedTerminal.MODID)
            .getFile()
            .getScanResult();

    public static void init() {
        if (ModList.get().isLoaded(ExCrafting_ID)) {
            loadedAnnotations.put(ModAccessor.ExtendedCrafting.class, true);
        }
        if (ModList.get().isLoaded(Avaritia_ID)) {
            var modContainer = ModList.get().getModContainerById(Avaritia_ID).orElse(null);
            if (modContainer != null) {
                if (modContainer.getModInfo().getDisplayName().equals(ReAvaritia)) {
                    loadedAnnotations.put(ModAccessor.ReAvaritia.class, true);
                } else if (modContainer.getModInfo().getDisplayName().equals(AvaritiaNeo)) {
                    loadedAnnotations.put(ModAccessor.AvaritiaNeo.class, true);
                } else {
                    logger.warn("Unknown Avaritia mod version: " + modContainer.getModInfo().getDisplayName());
                }
            }
        }
    }

    public static boolean get(Class<?> clazz) {
        return loadedAnnotations.getOrDefault(clazz, false);
    }

    public static ModFileScanData getScanData() {
        return ScanData;
    }

    public static Set<ModFileScanData.AnnotationData> getLoadedAnnotation() {
        return ScanData.getAnnotations();
    }
}