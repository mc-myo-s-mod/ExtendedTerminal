package me.myogoo.extendedterminal.integration.polymorph;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.integration.polymorph.widget.ETTerminalPolymorphWidget;

public final class ETPolymorph {
    private ETPolymorph() {
    }

    public static void init() {
        PolymorphApi.client().registerWidget(screen -> {
            if (screen instanceof ETTerminalBaseScreen<?, ?> etTerminalScreen
                    && (screen instanceof ETTerminalScreen<?> || screen instanceof UnitedTerminalScreen<?>)) {
                var slots = etTerminalScreen.getMenu().getSlots(etTerminalScreen.getMenu().getOutputSlotSemantic());
                if (!slots.isEmpty()) {
                    return new ETTerminalPolymorphWidget(etTerminalScreen, slots.get(0));
                }
            }
            return null;
        });
    }
}
