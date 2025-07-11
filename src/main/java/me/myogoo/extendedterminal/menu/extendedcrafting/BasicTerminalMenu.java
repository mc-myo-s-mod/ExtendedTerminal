package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class BasicTerminalMenu extends ExtendedTerminalBaseMenu  {
    public static final MenuType<BasicTerminalMenu> TYPE = MenuTypeBuilder
            .create(BasicTerminalMenu::new, ITerminalHost.class)
            .build(ETMenuType.BASIC_TERMINAL.getIdAsString());

    public BasicTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.BASIC_TERMINAL);
    }
}
