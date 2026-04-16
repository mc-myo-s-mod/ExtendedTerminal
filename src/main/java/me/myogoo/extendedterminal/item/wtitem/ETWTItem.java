package me.myogoo.extendedterminal.item.wtitem;

import de.mari_023.ae2wtlib.terminal.ItemWT;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class ETWTItem extends ItemWT {
    public ETWTItem(Properties properties) {
        super();
    }

    @Override
    public MenuType<?> getMenuType(ItemStack stack) {
        return ETWTMenu.TYPE;
    }
}
