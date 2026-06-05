package me.myogoo.extendedterminal.client.screen.extendedcrafting.wt;

import appeng.client.gui.style.ScreenStyle;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class UnitedWTScreen extends ETTerminalBaseScreen<ITableRecipe, UnitedWTMenu> {
    public UnitedWTScreen(UnitedWTMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
