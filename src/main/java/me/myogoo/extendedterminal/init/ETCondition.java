package me.myogoo.extendedterminal.init;

import com.mojang.serialization.MapCodec;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.datagen.condition.ETModCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ETCondition {
    public static final DeferredRegister<MapCodec<? extends ICondition>> REGISTER =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, ExtendedTerminal.MODID);

    public static final Supplier<MapCodec<ETModCondition>> ET_MOD_LOAD =
            REGISTER.register("et_mod", () -> ETModCondition.CODEC);
}
