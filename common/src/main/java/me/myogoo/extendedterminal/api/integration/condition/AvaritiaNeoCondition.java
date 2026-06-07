package me.myogoo.extendedterminal.api.integration.condition;

import me.myogoo.myotus.api.integration.MyoCustomCondition;
import me.myogoo.myotus.dto.MyoModInfo;

public class AvaritiaNeoCondition implements MyoCustomCondition {
    @Override
    public boolean test(MyoModInfo info) {
        return info.displayName().equals("Avaritia");
    }
}
