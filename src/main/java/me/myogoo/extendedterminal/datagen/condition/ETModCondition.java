package me.myogoo.extendedterminal.datagen.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.myogoo.extendedterminal.util.mod.ModIntegrationManager;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

public final class ETModCondition implements ICondition {
    public static final MapCodec<ETModCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("et_mod").forGetter(ETModCondition::getModId)
            ).apply(instance, ETModCondition::new)
    );

    @Nullable
    private Class<? extends Annotation> modId = null;

    public ETModCondition(String modId) {
        this.modId = ModIntegrationManager.getClass(modId);
    }

    @Override
    public boolean test(IContext context) {
        if(this.modId == null) return false;
        return ModIntegrationManager.isLoaded(this.modId);
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    public String getModId() {
        if(this.modId == null) return "null";
        return this.modId.getSimpleName();
    }
}