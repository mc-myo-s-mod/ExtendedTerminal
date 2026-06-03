package me.myogoo.extendedterminal.datagen.condition.serializer;

import com.google.gson.JsonObject;
import me.myogoo.extendedterminal.datagen.condition.ETModCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ETModConditionSerializer implements IConditionSerializer<ETModCondition> {
    public static ETModConditionSerializer INSTANCE = new ETModConditionSerializer();
    @Override
    public void write(JsonObject json, ETModCondition value) {
        json.addProperty(ETModCondition.NAME, value.getModId());
    }

    @Override
    public ETModCondition read(JsonObject json) {
        return new ETModCondition(json.get(ETModCondition.NAME).getAsString());
    }

    @Override
    public ResourceLocation getID() {
        return ETModCondition.ID;
    }
}
