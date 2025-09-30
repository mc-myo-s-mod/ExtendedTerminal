package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class ETDataComponent {
    public static DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ExtendedTerminal.MODID);

    public static final DataComponentType<ItemContainerContents> CRAFTING_INV = register("crafting_inv", bulder -> bulder
            .persistent(ItemContainerContents.CODEC)
            .networkSynchronized(ItemContainerContents.STREAM_CODEC));

    public static final DataComponentType<ItemContainerContents> SMITHING_INV = register("smithing_inv",bulder -> bulder
            .persistent(ItemContainerContents.CODEC)
            .networkSynchronized(ItemContainerContents.STREAM_CODEC));

    public static final DataComponentType<ItemContainerContents> STONECUTTER_INV = register("stonecutter_inv", bulder -> bulder
            .persistent(ItemContainerContents.CODEC)
            .networkSynchronized(ItemContainerContents.STREAM_CODEC));


    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        REGISTER.register(name, () -> componentType);
        return componentType;
    }


}

