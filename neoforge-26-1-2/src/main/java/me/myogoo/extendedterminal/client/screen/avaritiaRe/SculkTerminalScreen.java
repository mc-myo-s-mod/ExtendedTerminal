package me.myogoo.extendedterminal.client.screen.avaritiaRe;

import appeng.client.gui.style.ScreenStyle;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SculkTerminalScreen extends ETTerminalBaseScreen<ITierCraftingRecipe, SculkTerminalMenu> {
    public SculkTerminalScreen(SculkTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
