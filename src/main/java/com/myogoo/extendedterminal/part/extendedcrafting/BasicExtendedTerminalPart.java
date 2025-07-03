package com.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import com.myogoo.extendedterminal.part.ETBaseTerminalPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class BasicExtendedTerminalPart extends ETBaseTerminalPart {
    @PartModels
    public static final ResourceLocation BASIC_MODEL_BASE = ExtendedTerminal.makeId("part/extendedcrafting/basic_terminal_base");

    public static final IPartModel MODELS_OFF = new PartModel(BASIC_MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(BASIC_MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(BASIC_MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public BasicExtendedTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.BASIC_TERMINAL);
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return BasicTerminalMenu.TYPE;
    }
    
    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF,MODELS_ON,MODELS_HAS_CHANNEL);
    }
}
