package me.myogoo.extendedterminal.client.screen.extendedcrafting.gui.config;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import net.minecraft.network.chat.Component;

public class UnitedTerminalConfigScreen implements MyoConfigTabScreen {
    @Override
    public void buildTab(WidgetContainer widgets, AEBaseScreen<?> screen) {
        if (!(screen.getMenu() instanceof UnitedTerminalMenu menu)) {
            return;
        }

        var rememberRecipeType = widgets.addCheckbox(
                "rememberRecipeType",
                Component.translatable(ETTranslationKey.GUI.GUI_CONFIG_REMEMBER_UNITED_RECIPE_TYPE.key()),
                null
        );
        rememberRecipeType.setSelected(menu.rememberRecipeType());
        rememberRecipeType.setChangeListener(() -> menu.setRememberRecipeType(rememberRecipeType.isSelected()));

        if (!MyotusAPI.integrations().isLoaded(AE2WTLib.class) || !(menu instanceof UnitedWTMenu wirelessMenu)) {
            return;
        }

        var pickBlock = widgets.addCheckbox("pickBlock", TextConstants.PICK_BLOCK, null);
        var craftIfMissing = widgets.addCheckbox("craftIfMissing", TextConstants.CRAFT_IF_MISSING, null);
        var restock = widgets.addCheckbox("restock", TextConstants.RESTOCK, null);
        var magnet = widgets.addCheckbox("magnet", TextConstants.MAGNET, null);
        var pickupToME = widgets.addCheckbox("pickupToME", TextConstants.PICKUP_TO_ME, null);

        pickBlock.setSelected(wirelessMenu.isPickBlock());
        craftIfMissing.setSelected(wirelessMenu.isCraftIfMissing());
        craftIfMissing.active = pickBlock.isSelected();
        restock.setSelected(wirelessMenu.isRestock());
        pickBlock.setChangeListener(() -> {
            wirelessMenu.setPickBlock(pickBlock.isSelected());
            craftIfMissing.active = pickBlock.isSelected();
        });
        craftIfMissing.setChangeListener(() -> wirelessMenu.setCraftIfMissing(craftIfMissing.isSelected()));
        restock.setChangeListener(() -> wirelessMenu.setRestock(restock.isSelected()));

        var mode = wirelessMenu.getMagnetMode();
        magnet.setSelected(mode.magnet());
        pickupToME.setSelected(mode.pickupToME());
        magnet.active = pickupToME.active = mode != MagnetMode.NO_CARD && mode != MagnetMode.INVALID;
        Runnable saveMagnet = () -> {
            if (magnet.active) {
                wirelessMenu.setMagnetMode(wirelessMenu.getMagnetMode().set(
                        magnet.isSelected(), pickupToME.isSelected()));
            }
        };
        magnet.setChangeListener(saveMagnet);
        pickupToME.setChangeListener(saveMagnet);
    }
}
