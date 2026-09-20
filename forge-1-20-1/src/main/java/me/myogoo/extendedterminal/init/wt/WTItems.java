package me.myogoo.extendedterminal.init.wt;

import appeng.api.features.GridLinkables;
import appeng.core.definitions.ItemDefinition;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.item.TooltipItem;
import me.myogoo.extendedterminal.item.wtitem.ETWTItem;
import me.myogoo.extendedterminal.item.wtitem.UnitedWTItem;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.ExtendedCraftingWTMenu;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class WTItems {
    public static final List<ItemDefinition<? extends Item>> WT_ITEMS = new ArrayList<>();

    public static final ItemDefinition<ItemWT> WIRELESS_ET_TERMINAL = createWTItem(
            "Extended Wireless Terminal",
            ExtendedTerminal.makeId("wireless_et_terminal"),
            ETWTItem::new
    );
    public static final ItemDefinition<ItemWT> WIRELESS_UNITED_TERMINAL = createWTItem(
            "Wireless United Terminal",
            ExtendedTerminal.makeId("wireless_united_terminal"),
            UnitedWTItem::new
    );
    public static final ItemDefinition<? extends Item> WIRELESS_EPIC_TERMINAL = createCraftingWTItem(
            "Wireless Epic Terminal", ETMenuType.EPIC_TERMINAL, () -> ExtendedCraftingWTMenu.EPIC_TYPE
    );
    public static final ItemDefinition<? extends Item> WIRELESS_LEGENDARY_TERMINAL = createCraftingWTItem(
            "Wireless Legendary Terminal", ETMenuType.LEGENDARY_TERMINAL, () -> ExtendedCraftingWTMenu.LEGENDARY_TYPE
    );

    private static ItemDefinition<? extends Item> createCraftingWTItem(
            String name, ETMenuType terminalType, Supplier<MenuType<?>> menuType) {
        var id = ExtendedTerminal.makeId(terminalType.getWTIdAsString());
        if (terminalType.canLoad()) {
            return createWTItem(name, id, properties -> new ETWTItem(properties, menuType));
        }
        var definition = new ItemDefinition<>(name, id, new TooltipItem(new Item.Properties().stacksTo(1),
                ETTranslationKey.ITEM.ITEM_UNREGISTERED_TERMINAL_TOOLTIP.key()));
        ETItems.REGISTER.register(id.getPath(), definition::asItem);
        return definition;
    }

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
