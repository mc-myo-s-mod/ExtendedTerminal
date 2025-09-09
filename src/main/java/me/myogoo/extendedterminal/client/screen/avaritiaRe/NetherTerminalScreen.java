package me.myogoo.extendedterminal.client.screen.avaritiaRe;

import appeng.client.gui.style.ScreenStyle;
import committee.nova.mods.avaritia.common.crafting.recipe.ITierCraftingRecipe;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class NetherTerminalScreen extends ETTerminalBaseScreen<ITierCraftingRecipe, NetherTerminalMenu> {
    public NetherTerminalScreen(NetherTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
