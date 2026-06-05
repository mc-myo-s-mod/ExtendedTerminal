package me.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class AdvancedTerminalPart extends ETTerminalBasePart {
    @PartModels
    public static final ResourceLocation ADVANCED_MODEL_BASE = ExtendedTerminal.makeId("part/extendedcrafting/advanced_terminal_base");

    public static final IPartModel MODELS_OFF = new PartModel(ADVANCED_MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(ADVANCED_MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(ADVANCED_MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public AdvancedTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ADVANCED_TERMINAL, ExtendedCraftingConfig.INSTANCE.getAdvancedConfig());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return AdvancedTerminalMenu.TYPE;
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF,MODELS_ON,MODELS_HAS_CHANNEL);
    }
}
