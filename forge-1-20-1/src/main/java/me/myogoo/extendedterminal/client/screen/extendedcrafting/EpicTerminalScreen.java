package me.myogoo.extendedterminal.client.screen.extendedcrafting;

import appeng.client.gui.style.ScreenStyle;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.extendedcrafting.EpicTerminalMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EpicTerminalScreen extends ETTerminalBaseScreen<ITableRecipe, EpicTerminalMenu> {
    public EpicTerminalScreen(EpicTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
