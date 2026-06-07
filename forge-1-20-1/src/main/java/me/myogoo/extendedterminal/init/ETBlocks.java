package me.myogoo.extendedterminal.init;

import appeng.core.definitions.BlockDefinition;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.block.MaterialConverterBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ETBlocks {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(Registries.BLOCK, ExtendedTerminal.MODID);
    public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static final MaterialConverterBlock MATERIAL_CONVERTER_BLOCK = new MaterialConverterBlock();
    public static final RegistryObject<MaterialConverterBlock> MATERIAL_CONVERTER = REGISTER.register(
            "material_converter", () -> MATERIAL_CONVERTER_BLOCK);
}
