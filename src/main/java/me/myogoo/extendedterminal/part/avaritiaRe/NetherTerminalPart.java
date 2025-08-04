package me.myogoo.extendedterminal.part.avaritiaRe;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class NetherTerminalPart extends ETTerminalBasePart {
    @PartModels
    public static final ResourceLocation BASIC_MODEL_BASE = ExtendedTerminal.makeId("part/avaritia/nether_terminal_base");

    public static final IPartModel MODELS_OFF = new PartModel(BASIC_MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(BASIC_MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(BASIC_MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public NetherTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.NETHER_TERMINAL);
        this.getMainNode().setIdlePowerUsage(ETConfig.NETHER_TERMINAL_CONFIG.passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return NetherTerminalMenu.TYPE;
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF,MODELS_ON,MODELS_HAS_CHANNEL);
    }
}

