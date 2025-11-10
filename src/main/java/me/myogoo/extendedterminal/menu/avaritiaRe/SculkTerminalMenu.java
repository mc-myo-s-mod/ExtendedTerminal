package me.myogoo.extendedterminal.menu.avaritiaRe;


import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import me.myogoo.extendedterminal.config.avaritiaRe.AvaritiaReConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SculkTerminalMenu extends AvaritiaTerminalBaseMenu {
    public static final MenuType<SculkTerminalMenu> TYPE = MenuTypeBuilder
            .create(SculkTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ETMenuType.SCULK_TERMINAL.getId());

    public SculkTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.SCULK_TERMINAL, AvaritiaReConfig.INSTANCE.getSculkConfig());
    }
}
