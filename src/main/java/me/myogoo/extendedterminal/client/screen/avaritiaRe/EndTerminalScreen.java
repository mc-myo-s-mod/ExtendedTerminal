package me.myogoo.extendedterminal.client.screen.avaritiaRe;

import appeng.client.gui.style.ScreenStyle;
import committee.nova.mods.avaritia.common.crafting.recipe.ITierCraftingRecipe;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EndTerminalScreen extends ETTerminalBaseScreen<ITierCraftingRecipe, EndTerminalMenu> {
    public EndTerminalScreen(EndTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
