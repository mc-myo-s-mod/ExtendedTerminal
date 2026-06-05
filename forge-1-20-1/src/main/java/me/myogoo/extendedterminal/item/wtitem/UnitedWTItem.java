package me.myogoo.extendedterminal.item.wtitem;

import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class UnitedWTItem extends ETWTItem {
    public UnitedWTItem(Properties properties) {
        super(properties);
    }

    @Override
    public MenuType<?> getMenuType(ItemStack stack) {
        return UnitedWTMenu.TYPE;
    }
}
