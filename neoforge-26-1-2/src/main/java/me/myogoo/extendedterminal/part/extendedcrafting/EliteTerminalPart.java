package me.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class EliteTerminalPart extends ETTerminalBasePart {
    public EliteTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ELITE_TERMINAL);
        this.getMainNode().setIdlePowerUsage(ExtendedCraftingConfig.INSTANCE.getEliteConfig().passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) { return EliteTerminalMenu.TYPE; }
}
