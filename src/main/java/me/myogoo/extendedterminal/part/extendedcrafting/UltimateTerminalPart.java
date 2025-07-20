package me.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import me.myogoo.extendedterminal.part.ETBaseTerminalPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class UltimateTerminalPart extends ETBaseTerminalPart {
    @PartModels
    public static final ResourceLocation ULTIMATE_MODEL_BASE = ExtendedTerminal.makeId("part/extendedcrafting/ultimate_terminal_base");

    public static final IPartModel MODELS_OFF = new PartModel(ULTIMATE_MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(ULTIMATE_MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(ULTIMATE_MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public UltimateTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ULTIMATE_TERMINAL);
        this.getMainNode().setIdlePowerUsage(ETConfig.ULTIMATE_TERMINAL_CONFIG.passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) { return UltimateTerminalMenu.TYPE; }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF,MODELS_ON,MODELS_HAS_CHANNEL);
    }
}
