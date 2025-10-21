package me.myogoo.extendedterminal.item.wtitem;

import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ETWTItem extends ItemWT {
    @Override
    public @NotNull MenuType<?> getMenuType(ItemMenuHostLocator itemMenuHostLocator, Player player) {
        return ETWTMenu.TYPE;
    }
}
