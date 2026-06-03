package me.myogoo.extendedterminal.menu.avaritiaRe;

import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import me.myogoo.extendedterminal.config.avaritiaRe.AvaritiaReConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class NetherTerminalMenu extends AvaritiaTerminalBaseMenu {
    public static final MenuType<NetherTerminalMenu> TYPE = MenuTypeBuilder
            .create(NetherTerminalMenu::new, ITerminalHost.class)
            .build(ETMenuType.NETHER_TERMINAL.getIdAsString());

    public NetherTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.NETHER_TERMINAL, AvaritiaReConfig.INSTANCE.getNetherConfig());
    }
}
