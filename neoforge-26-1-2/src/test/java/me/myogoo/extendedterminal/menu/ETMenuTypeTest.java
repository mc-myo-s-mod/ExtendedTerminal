package me.myogoo.extendedterminal.menu;

import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.impl.MyotusAPIImpl;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ETMenuTypeTest {
    @Test
    void baseTerminalsRemainAvailableWithoutOptionalIntegrations() {
        var baseTerminals = EnumSet.of(ETMenuType.ET_TERMINAL,
                ETMenuType.EX_PATTERN_TERMINAL, ETMenuType.UNITED_TERMINAL);
        for (var type : baseTerminals) {
            assertTrue(type.canLoad(), type.name());
        }

        MyotusAPI._setInstance(MyotusAPIImpl.INSTANCE);
        for (var type : ETMenuType.values()) {
            assertEquals(baseTerminals.contains(type), type.canLoad(), type.name());
        }
    }
}
