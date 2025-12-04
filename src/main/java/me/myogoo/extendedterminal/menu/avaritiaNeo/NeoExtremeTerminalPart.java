package me.myogoo.extendedterminal.menu.avaritiaNeo;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.parts.PartModel;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class NeoExtremeTerminalPart extends ETTerminalBasePart {
    public NeoExtremeTerminalPart(IPartItem<?> partItem, ETMenuType menuType, IETTerminalConfig config) {
        super(partItem, menuType, config);
    }

    public static final ResourceLocation BASIC_MODEL_BASE = ExtendedTerminal.makeId("part/avaritia/extreme_terminal_base");

    public static final IPartModel MODELS_OFF = new PartModel(BASIC_MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(BASIC_MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(BASIC_MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    @Override
    public MenuType<?> getMenuType(Player player) {
        return super.getMenuType(player);
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF,MODELS_ON, MODELS_HAS_CHANNEL);
    }
}
