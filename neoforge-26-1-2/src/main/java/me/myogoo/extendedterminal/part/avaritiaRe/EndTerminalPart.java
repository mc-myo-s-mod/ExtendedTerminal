package me.myogoo.extendedterminal.part.avaritiaRe;

import appeng.api.parts.IPartItem;
import me.myogoo.extendedterminal.config.avaritiaRe.AvaritiaReConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class EndTerminalPart extends ETTerminalBasePart {
    public EndTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.END_TERMINAL);
        this.getMainNode().setIdlePowerUsage(AvaritiaReConfig.INSTANCE.getEndConfig().passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return EndTerminalMenu.TYPE;
    }

}
