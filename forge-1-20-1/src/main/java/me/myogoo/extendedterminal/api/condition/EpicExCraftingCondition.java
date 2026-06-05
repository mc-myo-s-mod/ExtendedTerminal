package me.myogoo.extendedterminal.api.condition;

import me.myogoo.myotus.api.integration.MyoCustomCondition;
import me.myogoo.myotus.dto.MyoModInfoDto;

public class EpicExCraftingCondition implements MyoCustomCondition {
    @Override
    public boolean test(MyoModInfoDto myoModInfoDto) {
        return myoModInfoDto.displayName().equals("Extended Crafting: Expanded");
    }
}
