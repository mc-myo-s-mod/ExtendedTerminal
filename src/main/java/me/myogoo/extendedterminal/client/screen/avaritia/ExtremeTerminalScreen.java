package me.myogoo.extendedterminal.client.screen.avaritia;

import appeng.client.gui.style.ScreenStyle;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.avaritia.ExtremeTerminalMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ExtremeTerminalScreen extends ETTerminalBaseScreen<ITierCraftingRecipe, ExtremeTerminalMenu> {
    public ExtremeTerminalScreen(ExtremeTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
