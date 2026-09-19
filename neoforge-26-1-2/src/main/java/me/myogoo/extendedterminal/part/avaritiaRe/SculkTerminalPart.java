package me.myogoo.extendedterminal.part.avaritiaRe;

import appeng.api.parts.IPartItem;
import me.myogoo.extendedterminal.config.avaritiaRe.AvaritiaReConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class SculkTerminalPart extends ETTerminalBasePart {
    public SculkTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.SCULK_TERMINAL);
        this.getMainNode().setIdlePowerUsage(AvaritiaReConfig.INSTANCE.getSculkConfig().passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return SculkTerminalMenu.TYPE;
    }

}
