package me.myogoo.extendedterminal.util.mod;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.ModAccessor;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

//TODO: Refactor this class
public class ModLoadHelper {
    private static final Map<Class<? extends Annotation>, Boolean> loadedAnnotations = new HashMap<>();
    private static final Map<Type, Boolean> loadedAnnotationTypes = new HashMap<>();
    private static final Map<String, Boolean> loadedModIds = new HashMap<>();

    private final static String ExCrafting_ID = "extendedcrafting";
    private final static String Avaritia_ID = "avaritia";
    private final static ModFileScanData ScanData = ModList.get()
            .getModFileById(ExtendedTerminal.MODID)
            .getFile()
            .getScanResult();

    public static void init() {
        if (ModList.get().isLoaded(ExCrafting_ID)) {
            loadedAnnotations.put(ModAccessor.ExtendedCrafting.class, true);
            loadedAnnotationTypes.put(Type.getType(ModAccessor.ExtendedCrafting.class), true);
            loadedModIds.put(ExCrafting_ID, true);
        }
        if(ModList.get().isLoaded(Avaritia_ID)) {
            loadedAnnotations.put(ModAccessor.Avaritia.class, true);
            loadedAnnotationTypes.put(Type.getType(ModAccessor.Avaritia.class), true);
            loadedModIds.put(Avaritia_ID, true);
        }
    }
    public static boolean get(Type type) {
        return loadedAnnotationTypes.getOrDefault(type, false);
    }

    public static boolean get(Class<?> clazz) {
        return loadedAnnotations.getOrDefault(clazz, false);
    }

    public  static boolean get(String modId) {
        return loadedModIds.getOrDefault(modId, false);
    }

    public static ModFileScanData getScanData() {
        return ScanData;
    }
}