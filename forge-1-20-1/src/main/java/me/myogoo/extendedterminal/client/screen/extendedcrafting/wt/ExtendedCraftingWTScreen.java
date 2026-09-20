package me.myogoo.extendedterminal.client.screen.extendedcrafting.wt;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import de.mari_023.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.ExtendedCraftingWTMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ExtendedCraftingWTScreen extends ETTerminalBaseScreen<ITableRecipe, ExtendedCraftingWTMenu>
        implements IUniversalTerminalCapable {
    public ExtendedCraftingWTScreen(ExtendedCraftingWTMenu menu, Inventory inventory, Component title, ScreenStyle style) {
        super(menu, inventory, title, style);
        if (menu.isWUT()) {
            addToLeftToolbar(new CycleTerminalButton(btn -> cycleTerminal()));
        }
        widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }
}
