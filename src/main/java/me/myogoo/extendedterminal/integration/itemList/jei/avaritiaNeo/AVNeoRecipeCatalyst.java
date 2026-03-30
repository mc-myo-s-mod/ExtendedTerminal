package me.myogoo.extendedterminal.integration.itemList.jei.avaritiaNeo;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.config.avaritiaNeo.AvaritiaNeoConfig;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;

@ETJeiRecipeCatalyst
@ModAccessor.AvaritiaNeo
public class AVNeoRecipeCatalyst {
    @MyotusSubscriber
    public static void init(IRecipeCatalystRegistration registration) {
        if(AvaritiaNeoConfig.INSTANCE.getExtremeConfig().enableTerminal()) {
            registration.addRecipeCatalyst(ETParts.NEO_EXTREME_TERMINAL_PART, AvaritiaJEIPlugin.EXTREME_CRAFTING);
        }
    }
}
