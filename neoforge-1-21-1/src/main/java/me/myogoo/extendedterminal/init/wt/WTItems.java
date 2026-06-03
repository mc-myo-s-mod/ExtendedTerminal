package me.myogoo.extendedterminal.init.wt;

import appeng.core.definitions.ItemDefinition;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.item.wtitem.ETWTItem;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class WTItems {
    public static final List<ItemDefinition<? extends Item>> WT_ITEMS = new ArrayList<>();

    public static final ItemDefinition<Item> WIRELESS_ET_TERMINAL = createWTItem("Extended Wireless Terminal",
            ExtendedTerminal.makeId("wireless_et_terminal"),
            ETWTItem::new);

    private static <T extends Item> ItemDefinition<T> createWTItem(String name, ResourceLocation id,
                                                                   Supplier<T> supplier) {
        return createWTItem(name, id, p -> supplier.get());
    }

    private static <T extends Item> ItemDefinition<T> createWTItem(String name, ResourceLocation id,
                                                                   Function<Item.Properties, T> itemFactory) {
        var item = ETItems.REGISTER.registerItem(id.getPath(), itemFactory);
        var definition = new ItemDefinition<>(name, item);
        WT_ITEMS.add(definition);
        return definition;
    }

    public static void register() {}
}
