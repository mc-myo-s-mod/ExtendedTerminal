package me.myogoo.extendedterminal.item.wtitem;

import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class ETWTItem extends ItemWT {
    private final Supplier<MenuType<?>> menuType;

    public ETWTItem(Properties properties) {
        this(properties, () -> ETWTMenu.TYPE);
    }

    public ETWTItem(Properties properties, Supplier<MenuType<?>> menuType) {
        super();
        this.menuType = menuType;
    }

    @Override
    public MenuType<?> getMenuType(ItemStack stack) {
        return menuType.get();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) {
            return;
        }
        MagnetHandler.handle(player, stack);
    }
}
