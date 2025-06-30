package com.myogoo.extendedterminal.init;

import appeng.core.definitions.ItemDefinition;
import com.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ETItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ExtendedTerminal.MODID);

    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static <T extends Item> ItemDefinition<T> createItem(String name, ResourceLocation id, Function<Item.Properties, T> itemFactory) {
        var item = new ItemDefinition<>(name, REGISTER.registerItem(id.getPath(), itemFactory));
        ITEMS.add(item);
        return item;
    }
}
