package me.myogoo.extendedterminal.init;

import appeng.core.definitions.ItemDefinition;
import me.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ETItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM,ExtendedTerminal.MODID);

    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static final ItemDefinition<Item> COMPAT_PROCESSOR = createItem("compat processor",
            ExtendedTerminal.makeId("compat_processor"),
            Item::new);

    public static final ItemDefinition<Item> PRINTED_COMPAT_PROCESSOR = createItem("printed compat circuit",
            ExtendedTerminal.makeId("printed_compat_processor"),
            Item::new);

    public static <T extends Item> ItemDefinition<T> createItem(String name, ResourceLocation id, Function<Item.Properties, T> itemFactory) {
        ItemDefinition<T> item = new ItemDefinition<>(name, id, itemFactory.apply(new Item.Properties()));
        ITEMS.add(item);
        return item;
    }
}
