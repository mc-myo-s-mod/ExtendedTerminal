package me.myogoo.extendedterminal.item.wtitem;

import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class UnitedWTItem extends ItemWT {
    @Override
    public @NotNull MenuType<?> getMenuType(ItemMenuHostLocator itemMenuHostLocator, Player player) {
        return UnitedWTMenu.TYPE;
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
