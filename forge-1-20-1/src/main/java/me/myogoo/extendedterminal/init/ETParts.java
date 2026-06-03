package me.myogoo.extendedterminal.init;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.item.partitem.EmptyPartItem;
import me.myogoo.extendedterminal.item.partitem.ExtendedCraftingPartItem;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.part.EmptyPart;
import me.myogoo.extendedterminal.part.avaritiaNeo.NeoExtremeTerminalPart;
import me.myogoo.extendedterminal.part.avaritiaRe.EndTerminalPart;
import me.myogoo.extendedterminal.part.avaritiaRe.ExtremeTerminalPart;
import me.myogoo.extendedterminal.part.avaritiaRe.NetherTerminalPart;
import me.myogoo.extendedterminal.part.avaritiaRe.SculkTerminalPart;
import me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart;
import me.myogoo.extendedterminal.part.extendedcrafting.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ETParts {

    //useless but call ETPart class to register
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM,ExtendedTerminal.MODID);

    public static final List<ItemDefinition<? extends PartItem<?>>> PARTS = new ArrayList<>();
    public static final List<ItemDefinition<? extends PartItem<?>>> TERMINAL_PARTS = new ArrayList<>();

    public static final ItemDefinition<PartItem<ETTerminalPart>> ET_TERMINAL_PART = createTerminalPart(ETMenuType.ET_TERMINAL, ETTerminalPart.class, ETTerminalPart::new);
    public static final ItemDefinition<PartItem<BasicExtendedTerminalPart>> BASIC_TERMINAL_PART = createExtendedCraftingPart(ETMenuType.BASIC_TERMINAL, BasicExtendedTerminalPart.class, BasicExtendedTerminalPart::new);
    public static final ItemDefinition<PartItem<AdvancedTerminalPart>> ADVANCED_TERMINAL_PART = createExtendedCraftingPart(ETMenuType.ADVANCED_TERMINAL, AdvancedTerminalPart.class, AdvancedTerminalPart::new);
    public static final ItemDefinition<PartItem<EliteTerminalPart>> ELITE_TERMINAL_PART = createExtendedCraftingPart(ETMenuType.ELITE_TERMINAL, EliteTerminalPart.class, EliteTerminalPart::new);
    public static final ItemDefinition<PartItem<UltimateTerminalPart>> ULTIMATE_TERMINAL_PART = createExtendedCraftingPart(ETMenuType.ULTIMATE_TERMINAL, UltimateTerminalPart.class, UltimateTerminalPart::new);
    public static final ItemDefinition<PartItem<EpicTerminalPart>> EPIC_TERMINAL_PART = createExtendedCraftingPart(ETMenuType.EPIC_TERMINAL, EpicTerminalPart.class, EpicTerminalPart::new);

    // Re:Avaritia parts
    public static final ItemDefinition<PartItem<SculkTerminalPart>> SCULK_TERMINAL_PART = createTerminalPart(ETMenuType.SCULK_TERMINAL, SculkTerminalPart.class, SculkTerminalPart::new);
    public static final ItemDefinition<PartItem<NetherTerminalPart>> NETHER_TERMINAL_PART = createTerminalPart(ETMenuType.NETHER_TERMINAL, NetherTerminalPart.class, NetherTerminalPart::new);
    public static final ItemDefinition<PartItem<EndTerminalPart>> END_TERMINAL_PART = createTerminalPart(ETMenuType.END_TERMINAL, EndTerminalPart.class, EndTerminalPart::new);
    public static final ItemDefinition<PartItem<ExtremeTerminalPart>> EXTREME_TERMINAL_PART = createTerminalPart(ETMenuType.EXTREME_TERMINAL, ExtremeTerminalPart.class, ExtremeTerminalPart::new);

    // AvaritiaNeo parts
    public static final ItemDefinition<PartItem<NeoExtremeTerminalPart>> NEO_EXTREME_TERMINAL_PART = createTerminalPart(ETMenuType.NEO_EXTREME_TERMINAL, NeoExtremeTerminalPart.class, NeoExtremeTerminalPart::new);

    @SuppressWarnings("unchecked")
    private static <T extends IPart, I extends PartItem<T>> ItemDefinition<I> createPart(ETMenuType menuType, Class<T> partClass, Function<Item.Properties, I> propertiesFactory, boolean terminalPart) {
        if(!menuType.canLoad()) {
            var definition = new ItemDefinition<PartItem<EmptyPart>>(menuType.getEnglishName(), menuType.getId(), new EmptyPartItem<>(new Item.Properties(), EmptyPart.class, EmptyPart::new));
            REGISTER.register(definition.id().getPath(), definition::asItem);
            return (ItemDefinition<I>) definition;
        };
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        var definition = ETItems.createItem(menuType.getEnglishName(), menuType.getId(), propertiesFactory);
        PARTS.add(definition);
        if (terminalPart) {
            TERMINAL_PARTS.add(definition);
        }
        return definition;
    }

    private static <T extends IPart> ItemDefinition<PartItem<T>> createTerminalPart(ETMenuType menuType, Class<T> partClass, Function<IPartItem<T>, T> partFactory) {
        return createPart(menuType,partClass, (p) -> new PartItem<>(p, partClass, partFactory), true);
    }

    private static <T extends IPart> ItemDefinition<PartItem<T>> createExtendedCraftingPart(ETMenuType menuType, Class<T> partClass, Function<IPartItem<T>, T> partFactory) {
        return createPart(menuType, partClass, (p) -> new ExtendedCraftingPartItem<>(p, menuType, partClass, partFactory), true);
    }
}
