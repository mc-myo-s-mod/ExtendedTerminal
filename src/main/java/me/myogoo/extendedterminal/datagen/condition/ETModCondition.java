package me.myogoo.extendedterminal.datagen.condition;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.util.mod.ModIntegrationManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

public class ETModCondition implements ICondition {
    public static final String NAME = "et_mod";
    public static final ResourceLocation ID = new ResourceLocation(ExtendedTerminal.MODID, NAME);

    @Nullable
    private Class<? extends Annotation> modId = null;
    private final String modIdString;
    public ETModCondition(String modId)
    {
        this.modIdString = modId;
        this.modId = ModIntegrationManager.getClass(modId);
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        if(this.modId == null) return false;
        return ModIntegrationManager.isLoaded(this.modId);
    }

    public String getModId() {
        return this.modIdString;
    }
}
