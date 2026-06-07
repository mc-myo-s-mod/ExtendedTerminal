package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.blockentity.MaterialConverterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ETBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(
            ForgeRegistries.BLOCK_ENTITY_TYPES, ExtendedTerminal.MODID);

    public static final RegistryObject<BlockEntityType<MaterialConverterBlockEntity>> MATERIAL_CONVERTER = REGISTER
            .register("material_converter", () -> BlockEntityType.Builder
                    .of(MaterialConverterBlockEntity::new, ETBlocks.MATERIAL_CONVERTER_BLOCK)
                    .build(null));
}
