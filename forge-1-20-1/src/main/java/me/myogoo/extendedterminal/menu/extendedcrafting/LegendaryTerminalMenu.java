package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class LegendaryTerminalMenu extends ExtendedTerminalBaseMenu {
    public static final MenuType<LegendaryTerminalMenu> TYPE = MenuTypeBuilder
            .create(LegendaryTerminalMenu::new, ITerminalHost.class)
            .build(ETMenuType.LEGENDARY_TERMINAL.getIdAsString());

    public LegendaryTerminalMenu(MenuType<?> menuType, int id, Inventory playerInventory, ITerminalHost host) {
        super(menuType, id, playerInventory, host, ETMenuType.LEGENDARY_TERMINAL,
                ExtendedCraftingConfig.INSTANCE.getLegendaryConfig());
    }
}
