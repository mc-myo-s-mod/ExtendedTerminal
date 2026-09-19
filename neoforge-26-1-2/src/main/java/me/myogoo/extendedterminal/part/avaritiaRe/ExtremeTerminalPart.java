package me.myogoo.extendedterminal.part.avaritiaRe;

import appeng.api.parts.IPartItem;
import me.myogoo.extendedterminal.config.avaritiaRe.AvaritiaReConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class ExtremeTerminalPart extends ETTerminalBasePart {
    public ExtremeTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.EXTREME_TERMINAL);
        this.getMainNode().setIdlePowerUsage(AvaritiaReConfig.INSTANCE.getExtremeConfig().passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return ExtremeTerminalMenu.TYPE;
    }

}
