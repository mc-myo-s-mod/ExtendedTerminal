package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.blockentity.MaterialConverterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ETBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE, ExtendedTerminal.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MaterialConverterBlockEntity>> MATERIAL_CONVERTER =
            REGISTER.register("material_converter", () -> BlockEntityType.Builder
                    .of(MaterialConverterBlockEntity::new, ETBlocks.MATERIAL_CONVERTER.get())
                    .build(null));
}
