package me.myogoo.extendedterminal.init;

import appeng.core.definitions.BlockDefinition;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.block.MaterialConverterBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ETBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(ExtendedTerminal.MODID);
    public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static final DeferredBlock<MaterialConverterBlock> MATERIAL_CONVERTER = REGISTER.register(
            "material_converter", MaterialConverterBlock::new);
}
