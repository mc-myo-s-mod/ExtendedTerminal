package me.myogoo.extendedterminal.integration.polymorph;

import com.illusivesoulworks.polymorph.api.client.PolymorphWidgets;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.integration.polymorph.widget.ETTerminalPolymorphWidget;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;

public class ETPolymorph {
    @SuppressWarnings("unchecked")
    public static void init() {
        PolymorphWidgets.getInstance().registerWidget(screen -> {
            if (screen instanceof ETTerminalScreen<?> etTerminalScreen) {
                return new ETTerminalPolymorphWidget<>((ETTerminalScreen<ETTerminalMenu>) etTerminalScreen);
            }
            return null;
        });
    }
}
