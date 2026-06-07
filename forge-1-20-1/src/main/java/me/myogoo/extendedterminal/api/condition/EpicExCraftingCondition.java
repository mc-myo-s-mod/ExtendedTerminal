package me.myogoo.extendedterminal.api.condition;

import me.myogoo.myotus.api.integration.MyoCustomCondition;
import me.myogoo.myotus.dto.MyoModInfo;

public class EpicExCraftingCondition implements MyoCustomCondition {
    @Override
    public boolean test(MyoModInfo myoModInfo) {
        return myoModInfo.displayName().equals("Extended Crafting: Expanded");
    }
}
