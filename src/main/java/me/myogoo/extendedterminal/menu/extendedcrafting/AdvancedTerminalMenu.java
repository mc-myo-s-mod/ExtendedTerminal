package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class AdvancedTerminalMenu extends ExtendedTerminalBaseMenu {
    public static final MenuType<AdvancedTerminalMenu> TYPE = MenuTypeBuilder
            .create(AdvancedTerminalMenu::new, ITerminalHost.class)
            .build(ETMenuType.ADVANCED_TERMINAL.getIdAsString());

    public AdvancedTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.ADVANCED_TERMINAL);
    }
}
