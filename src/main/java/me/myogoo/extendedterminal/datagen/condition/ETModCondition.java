package me.myogoo.extendedterminal.datagen.condition;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class ETModCondition implements ICondition {
    public static final String NAME = "et_mod";
    public static final ResourceLocation ID = new ResourceLocation(ExtendedTerminal.MODID, NAME);

    private final String modIdString;
    public ETModCondition(String modId)
    {
        this.modIdString = modId;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        return MyotusAPI.modIntegrationManager().isLoaded(this.modIdString);
    }

    public String getModId() {
        return this.modIdString;
    }
}
