package me.myogoo.extendedterminal.client.screen.extendedcrafting.gui.config;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.AECheckbox;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import net.minecraft.network.chat.Component;

public class UnitedTerminalConfigScreen implements MyoConfigTabScreen {
    private UnitedTerminalMenu menu;
    private AECheckbox rememberRecipeType;

    @Override
    public void buildTab(WidgetContainer widgets, AEBaseScreen<?> screen) {
        if (!(screen.getMenu() instanceof UnitedTerminalMenu menu)) {
            return;
        }

        this.menu = menu;
        this.rememberRecipeType = widgets.addCheckbox(
                "rememberRecipeType",
                Component.translatable(ETTranslationKey.GUI.GUI_CONFIG_REMEMBER_UNITED_RECIPE_TYPE.key()),
                this::save
        );
        this.rememberRecipeType.setSelected(menu.rememberRecipeType());
    }

    private void save() {
        if (menu == null || rememberRecipeType == null) {
            return;
        }

        menu.setRememberRecipeType(rememberRecipeType.isSelected());
    }
}
