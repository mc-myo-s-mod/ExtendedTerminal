package me.myogoo.extendedterminal.client.screen.avaritiaNeo;

import appeng.client.gui.style.ScreenStyle;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class NeoExtremeTerminalScreen extends ETTerminalBaseScreen<RecipeExtremeCrafting, NeoExtremeTerminalMenu> {
    public NeoExtremeTerminalScreen(NeoExtremeTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
