package me.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class AdvancedTerminalPart extends ETTerminalBasePart {
    public AdvancedTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ADVANCED_TERMINAL);
        this.getMainNode().setIdlePowerUsage(ExtendedCraftingConfig.INSTANCE.getAdvancedConfig().passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return AdvancedTerminalMenu.TYPE;
    }

}
