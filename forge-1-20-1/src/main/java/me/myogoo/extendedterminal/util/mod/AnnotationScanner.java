package me.myogoo.extendedterminal.util.mod;

import me.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.util.Set;

public final class AnnotationScanner {
    public static ModFileScanData getModScanData() {
        return ModList.get()
                .getModFileById(ExtendedTerminal.MODID)
                .getFile()
                .getScanResult();
    }

    public static Set<ModFileScanData.AnnotationData> getModAnnotations() {
        return getModScanData().getAnnotations();
    }

    private AnnotationScanner() {}
}
