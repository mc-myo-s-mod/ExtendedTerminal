package me.myogoo.extendedterminal.api.condition;

import com.blakebr0.extendedcrafting.block.LegendaryTableBlock;
import me.myogoo.myotus.api.integration.MyoCustomCondition;
import me.myogoo.myotus.dto.MyoModInfo;

public class LegendaryExCraftingCondition implements MyoCustomCondition {
    @Override
    public boolean test(MyoModInfo myoModInfo) {
        if (!myoModInfo.displayName().equals("Extended Crafting: Expanded")) {
            return false;
        }

        try {
            // Older Expanded releases have the same mod id/name but no Legendary table.
            return LegendaryTableBlock.class != null;
        } catch (LinkageError ignored) {
            return false;
        }
    }
}
