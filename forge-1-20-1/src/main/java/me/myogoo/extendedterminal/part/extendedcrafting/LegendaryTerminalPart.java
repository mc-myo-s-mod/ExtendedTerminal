package me.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.LegendaryTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class LegendaryTerminalPart extends ETTerminalBasePart {
    @PartModels
    public static final ResourceLocation MODEL_BASE =
            ExtendedTerminal.makeId("part/extendedcrafting/legendary_terminal_base");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL =
            new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public LegendaryTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.LEGENDARY_TERMINAL,
                ExtendedCraftingConfig.INSTANCE.getLegendaryConfig());
    }

    @Override
    public MenuType<?> getMenuType(Player player) {
        return LegendaryTerminalMenu.TYPE;
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }
}
