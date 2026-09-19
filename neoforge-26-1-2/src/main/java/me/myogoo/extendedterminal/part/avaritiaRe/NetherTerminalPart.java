package me.myogoo.extendedterminal.part.avaritiaRe;

import appeng.api.parts.IPartItem;
import me.myogoo.extendedterminal.config.avaritiaRe.AvaritiaReConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class NetherTerminalPart extends ETTerminalBasePart {
    public NetherTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.NETHER_TERMINAL);
        this.getMainNode().setIdlePowerUsage(AvaritiaReConfig.INSTANCE.getNetherConfig().passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return NetherTerminalMenu.TYPE;
    }

}
