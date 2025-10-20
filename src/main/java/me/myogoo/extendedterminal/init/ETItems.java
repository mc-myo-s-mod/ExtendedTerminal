package me.myogoo.extendedterminal.init;

import appeng.core.definitions.ItemDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.item.ChargedEnderPearlItem;
import me.myogoo.extendedterminal.item.wtitem.ETWTItem;
import me.myogoo.extendedterminal.util.mod.ModIntegrationManager;
import me.myogoo.extendedterminal.util.mod.SupportedMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


public class ETItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ExtendedTerminal.MODID);

    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();
    public static final List<ItemDefinition<? extends ItemWT>> WT_ITEMS = new ArrayList<>();

    public static final ItemDefinition<Item> COMPAT_PROCESSOR = createItem("compat processor",
            ExtendedTerminal.makeId("compat_processor"),
            Item::new);

    public static final ItemDefinition<Item> COMPAT_PRESS = createItem("compat press",
            ExtendedTerminal.makeId("compat_press"),
            Item::new);

    public static final ItemDefinition<Item> PRINTED_COMPAT_PROCESSOR = createItem("printed compat circuit",
            ExtendedTerminal.makeId("printed_compat_processor"),
            Item::new);

    public static final ItemDefinition<ETWTItem> WIRELESS_ET_TERMINAL = createWTItem("Extended Wireless Terminal",
            ExtendedTerminal.makeId("wireless_et_terminal"),
            ETWTItem::new);

    public static final ItemDefinition<ChargedEnderPearlItem> CHARGED_ENDER_PEARL = createItem("charged ender pearl",
            ExtendedTerminal.makeId("charged_ender_pearl"),
            ChargedEnderPearlItem::new);

    public static <T extends Item> ItemDefinition<T> createItem(String name, ResourceLocation id, Function<Item.Properties, T> itemFactory) {
        var item = new ItemDefinition<>(name, REGISTER.registerItem(id.getPath(), itemFactory));
        ITEMS.add(item);
        return item;
    }

    private static <T extends ItemWT> ItemDefinition<T> createWTItem(String name, ResourceLocation id, Supplier<T> supplier) {
        return createWTItem(name, id, p -> supplier.get());
    }

    private static <T extends ItemWT> ItemDefinition<T> createWTItem(String name, ResourceLocation id, Function<Item.Properties, T> itemFactory) {
        var item = REGISTER.registerItem(id.getPath(), p -> ModIntegrationManager.isLoaded(SupportedMod.AE2WTLib) ? itemFactory.apply(p) : (T) new Item(p));
        var definition = new ItemDefinition<>(name, item);
        WT_ITEMS.add(definition);
        return definition;
    }
}
