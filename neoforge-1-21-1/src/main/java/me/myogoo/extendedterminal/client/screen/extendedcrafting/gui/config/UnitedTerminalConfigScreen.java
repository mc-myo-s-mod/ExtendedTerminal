package me.myogoo.extendedterminal.client.screen.extendedcrafting.gui.config;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.AECheckbox;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import net.minecraft.network.chat.Component;

public class UnitedTerminalConfigScreen implements MyoConfigTabScreen {
    private UnitedTerminalMenu menu;
    private AECheckbox rememberRecipeKind;

    @Override
    public void buildTab(WidgetContainer widget, AEBaseScreen<?> screen) {
        if (!(screen.getMenu() instanceof UnitedTerminalMenu menu)) {
            return;
        }

        this.menu = menu;
        this.rememberRecipeKind = widget.addCheckbox(
                "rememberRecipeKind",
                Component.translatable("gui.extendedterminal.config.remember_united_recipe_kind"),
                this::save
        );
        updateState();
    }

    private void updateState() {
        if (rememberRecipeKind != null && menu != null) {
            rememberRecipeKind.setSelected(menu.rememberRecipeKind());
        }
    }

    private void save() {
        if (menu == null || rememberRecipeKind == null) {
            return;
        }

        menu.setRememberRecipeKind(rememberRecipeKind.isSelected());
    }
}
