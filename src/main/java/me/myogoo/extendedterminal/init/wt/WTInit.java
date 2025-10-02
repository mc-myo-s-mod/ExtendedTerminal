package me.myogoo.extendedterminal.init.wt;

import appeng.api.features.GridLinkables;
import appeng.core.definitions.ItemDefinition;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.extendedterminal.util.mod.ModIntegrationManager;
import me.myogoo.extendedterminal.util.mod.SupportedMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class WTInit {
    private final static ResourceLocation ICON_SRC = ExtendedTerminal.makeId("textures/guis/icons.png");
    private final static Icon.Texture TEXTURE = new Icon.Texture(ICON_SRC, 128,128);

    public static void init(RegisterEvent event) {
        if(event.getRegistryKey().equals(Registries.ITEM) && ModIntegrationManager.isLoaded(SupportedMod.AE2WTLib)) {
            register(ETItems.WIRELESS_ET_TERMINAL, ETMenuType.ET_TERMINAL, ETWTHost::new, ETWTMenu.TYPE, new Icon(0,0,16,16,TEXTURE));
        }
    }

    private static void register(ItemLike terminal, ETMenuType etMenuType, WTDefinition.WTMenuHostFactory host, MenuType<?> menuType, Icon icon) {
        AddTerminalEvent.register(e ->
                e.builder(etMenuType.getWTIdAsString(), host, menuType, (ItemWT) terminal.asItem(), icon)
                        .addTerminal());

        GridLinkables.register(terminal, WirelessTerminalItem.LINKABLE_HANDLER);
    }
}
