package me.myogoo.extendedterminal.init.wt;

import appeng.api.features.GridLinkables;
import appeng.core.definitions.ItemDefinition;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.item.wtitem.ETWTItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class WTItems {
    public static final List<ItemDefinition<? extends Item>> WT_ITEMS = new ArrayList<>();

    public static final ItemDefinition<ItemWT> WIRELESS_ET_TERMINAL = createWTItem(
            "Extended Wireless Terminal",
            ExtendedTerminal.makeId("wireless_et_terminal"),
            ETWTItem::new
    );

    private static <T extends Item> ItemDefinition<T> createWTItem(String name, ResourceLocation id,
                                                                    Function<Item.Properties, T> itemFactory) {
        var definition = ETItems.createItem(name, id, itemFactory);
        GridLinkables.register(definition.asItem(), WirelessTerminalItem.LINKABLE_HANDLER);
        WT_ITEMS.add(definition);
        return definition;
    }

    public static void register() {
    }
}
