package me.myogoo.extendedterminal.util.mod;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.ModAccessor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class ModLoadHelper {
    public final static String ECCrafting_ID = "extendedcrafting";
    public final static String Avaritia_ID = "avaritia";

    private static final Map<Class<? extends Annotation>, Boolean> loadedAnnotations = new HashMap<>();
    private static final Logger logger = ExtendedTerminal.LOGGER;
    private final static String ReAvaritia = "Re-Avaritia-forged";
    private final static String AvaritiaNeo = "Avaritia";

    private final static ModFileScanData ScanData = ModList.get()
            .getModFileById(ExtendedTerminal.MODID)
            .getFile()
            .getScanResult();

    public static void init() {
        loadedAnnotations.clear();
        if (ModList.get().isLoaded(ECCrafting_ID)) {
            loadedAnnotations.put(ModAccessor.ExtendedCrafting.class, true);
        }
        if (ModList.get().isLoaded(Avaritia_ID)) {
            var modContainer = ModList.get().getModContainerById(Avaritia_ID).orElse(null);
            if (modContainer != null) {
                if (modContainer.getModInfo().getDisplayName().equals(ReAvaritia)) {
                    loadedAnnotations.put(ModAccessor.ReAvaritia.class, true);
                } else {
                    logger.warn("Unknown Avaritia mod version: {}", modContainer.getModInfo().getDisplayName());
                }
            }
        }
    }

    public static boolean get(Class<? extends Annotation> clazz) {
        return loadedAnnotations.getOrDefault(clazz, false);
    }

    public static ModFileScanData getScanData() {
        return ScanData;
    }

}
