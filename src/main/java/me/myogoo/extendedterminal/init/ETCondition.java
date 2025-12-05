package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.datagen.condition.serializer.ETModConditionSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;

public class ETCondition {
    public static void register() {
        CraftingHelper.register(ETModConditionSerializer.INSTANCE);
    }
}
