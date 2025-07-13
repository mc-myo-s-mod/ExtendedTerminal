package me.myogoo.extendedterminal.init;

import appeng.core.definitions.ItemDefinition;
import me.myogoo.extendedterminal.ExtendedTerminal;
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

    public static final ItemDefinition<Item> COMPAT_PROCESSOR = createItem("compat_processor",
            ExtendedTerminal.makeId("compat_processor"),
            Item::new);

    public static final ItemDefinition<Item> PRINTED_COMPAT_PROCESSOR = createItem("printed_compat_processor",
            ExtendedTerminal.makeId("printed_compat_processor"),
            Item::new);

    public static <T extends Item> ItemDefinition<T> createItem(String name, ResourceLocation id, Function<Item.Properties, T> factory) {
        var item = factory.apply(new Item.Properties());
        ItemDefinition<T> itemDefinition = new ItemDefinition<>(name, id,item);
        ITEMS.add(itemDefinition);
        REGISTER.register(itemDefinition.id().getPath(),itemDefinition::asItem);
        return itemDefinition;
    }
}
