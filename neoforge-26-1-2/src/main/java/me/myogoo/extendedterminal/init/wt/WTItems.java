package me.myogoo.extendedterminal.init.wt;

import appeng.core.definitions.ItemDefinition;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.item.wtitem.ETWTItem;
import me.myogoo.extendedterminal.item.wtitem.UnitedWTItem;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class WTItems {
    public static final List<ItemDefinition<? extends Item>> WT_ITEMS = new ArrayList<>();

    public static final ItemDefinition<Item> WIRELESS_ET_TERMINAL = createWTItem("Extended Wireless Terminal",
            ExtendedTerminal.makeId("wireless_et_terminal"),
            ETWTItem::new);
    public static final ItemDefinition<Item> WIRELESS_UNITED_TERMINAL = createWTItem("Wireless United Terminal",
            ExtendedTerminal.makeId("wireless_united_terminal"),
            UnitedWTItem::new);

    private static <T extends Item> ItemDefinition<T> createWTItem(String name, Identifier id,
                                                                   Function<Item.Properties, T> itemFactory) {
        var item = ETItems.REGISTER.registerItem(id.getPath(), itemFactory);
        var definition = new ItemDefinition<>(name, item);
        WT_ITEMS.add(definition);
        return definition;
    }

    public static void register() {}
}
