package me.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class UltimateTerminalPart extends ETTerminalBasePart {
    public UltimateTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ULTIMATE_TERMINAL);
        this.getMainNode().setIdlePowerUsage(ExtendedCraftingConfig.INSTANCE.getUltimateConfig().passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) { return UltimateTerminalMenu.TYPE; }

}
