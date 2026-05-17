package me.myogoo.extendedterminal.integration.polymorph;

import appeng.menu.SlotSemantics;
import com.illusivesoulworks.polymorph.api.PolymorphApi;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.integration.polymorph.widget.ETTerminalPolymorphWidget;

public final class ETPolymorph {
    private ETPolymorph() {
    }

    public static void init() {
        PolymorphApi.client().registerWidget(screen -> {
            if (screen instanceof ETTerminalScreen<?> etTerminalScreen) {
                var slots = etTerminalScreen.getMenu().getSlots(SlotSemantics.CRAFTING_RESULT);
                if (!slots.isEmpty()) {
                    return new ETTerminalPolymorphWidget(etTerminalScreen, slots.get(0));
                }
            }
            return null;
        });
    }
}
