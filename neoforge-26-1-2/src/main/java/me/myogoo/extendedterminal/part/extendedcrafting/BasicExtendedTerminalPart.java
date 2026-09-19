package me.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class BasicExtendedTerminalPart extends ETTerminalBasePart {
    public BasicExtendedTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.BASIC_TERMINAL);
        this.getMainNode().setIdlePowerUsage(ExtendedCraftingConfig.INSTANCE.getBasicConfig().passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return BasicTerminalMenu.TYPE;
    }

}
