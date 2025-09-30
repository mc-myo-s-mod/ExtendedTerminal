package me.myogoo.extendedterminal.init;

import appeng.api.features.GridLinkables;
import appeng.core.definitions.ItemDefinition;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import me.myogoo.extendedterminal.item.wtitem.ETWTItem;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraft.world.item.Item;

public class WTInit {
    public static void init() {
//        AddTerminalEvent.register(x -> {
//            x.builder("extended_terminal", ETWTHost::new, ETWTMenu.TYPE, (ItemWT) ETItems.ET_WT.asItem(), Icon.CRAFTING)
//                    .addTerminal();
//        });
    }
}
