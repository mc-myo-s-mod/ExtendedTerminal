package me.myogoo.extendedterminal.init;

import appeng.core.definitions.ItemDefinition;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.item.ChargedEnderPearlItem;
import me.myogoo.extendedterminal.item.TooltipBlockItem;
import me.myogoo.extendedterminal.item.TooltipItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ETItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS,ExtendedTerminal.MODID);

    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static final ItemDefinition<Item> COMPAT_PRESS = createItem("compat_press",
            ExtendedTerminal.makeId("compat_press"),
            properties -> new TooltipItem(properties, "item.extendedterminal.item.tooltip.deprecated_material"));

    public static final ItemDefinition<Item> COMPAT_PROCESSOR = createItem("compat_processor",
            ExtendedTerminal.makeId("compat_processor"),
            properties -> new TooltipItem(properties, "item.extendedterminal.item.tooltip.deprecated_material"));

    public static final ItemDefinition<Item> PRINTED_COMPAT_PROCESSOR = createItem("printed_compat_processor",
            ExtendedTerminal.makeId("printed_compat_processor"),
            properties -> new TooltipItem(properties, "item.extendedterminal.item.tooltip.deprecated_material"));

    public static final ItemDefinition<ChargedEnderPearlItem> CHARGED_ENDER_PEARL = createItem("charged_ender_pearl",
            ExtendedTerminal.makeId("charged_ender_pearl"),
            ChargedEnderPearlItem::new);

    public static final ItemDefinition<TooltipBlockItem> MATERIAL_CONVERTER = createItem("material_converter",
            ExtendedTerminal.makeId("material_converter"),
            properties -> new TooltipBlockItem(ETBlocks.MATERIAL_CONVERTER_BLOCK, properties,
                    "item.extendedterminal.material_converter.tooltip"));

    public static <T extends Item> ItemDefinition<T> createItem(String name, ResourceLocation id, Function<Item.Properties, T> factory) {
        var item = factory.apply(new Item.Properties());
        ItemDefinition<T> itemDefinition = new ItemDefinition<>(name, id,item);
        ITEMS.add(itemDefinition);
        REGISTER.register(itemDefinition.id().getPath(),itemDefinition::asItem);
        return itemDefinition;
    }
}
