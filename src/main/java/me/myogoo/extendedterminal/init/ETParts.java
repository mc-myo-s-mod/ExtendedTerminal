package me.myogoo.extendedterminal.init;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.item.EmptyPartItem;
import me.myogoo.extendedterminal.item.ExtendedCraftingPartItem;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.part.EmptyPart;
import me.myogoo.extendedterminal.part.extendedcrafting.AdvancedTerminalPart;
import me.myogoo.extendedterminal.part.extendedcrafting.BasicExtendedTerminalPart;
import me.myogoo.extendedterminal.part.extendedcrafting.EliteTerminalPart;
import me.myogoo.extendedterminal.part.extendedcrafting.UltimateTerminalPart;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ETParts {
    //useless but call ETPart class to register
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ExtendedTerminal.MODID);

    public static final List<ItemDefinition<? extends PartItem<?>>> PARTS = new ArrayList<>();
    public static final List<ItemDefinition<? extends PartItem<?>>> TERMINAL_PARTS = new ArrayList<>();

    public static final ItemDefinition<PartItem<BasicExtendedTerminalPart>> BASIC_TERMINAL_PART = createExtendedCraftingPart(ETMenuType.BASIC_TERMINAL, BasicExtendedTerminalPart.class, BasicExtendedTerminalPart::new);
    public static final ItemDefinition<PartItem<AdvancedTerminalPart>> ADVANCED_TERMINAL_PART = createExtendedCraftingPart(ETMenuType.ADVANCED_TERMINAL, AdvancedTerminalPart.class, AdvancedTerminalPart::new);
    public static final ItemDefinition<PartItem<EliteTerminalPart>> ELITE_TERMINAL_PART = createExtendedCraftingPart(ETMenuType.ELITE_TERMINAL, EliteTerminalPart.class, EliteTerminalPart::new);
    public static final ItemDefinition<PartItem<UltimateTerminalPart>> ULTIMATE_TERMINAL_PART = createExtendedCraftingPart(ETMenuType.ULTIMATE_TERMINAL, UltimateTerminalPart.class, UltimateTerminalPart::new);

    @SuppressWarnings("unchecked")
    private static <T extends IPart, I extends PartItem<T>> ItemDefinition<I> createPart(ETMenuType menuType, Class<T> partClass, Function<Item.Properties, I> propertiesFactory, boolean terminalPart) {
        if(!menuType.canLoad()) {
            return (ItemDefinition<I>) new ItemDefinition<>(menuType.getEnglishName(),REGISTER.registerItem(menuType.getId().getPath(),(p) -> new EmptyPartItem<>(p, EmptyPart.class, EmptyPart::new)));
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
        return createPart(menuType, partClass, (p) -> new PartItem<>(p, partClass, partFactory), true);
    }

    private static <T extends IPart> ItemDefinition<PartItem<T>> createExtendedCraftingPart(ETMenuType menuType, Class<T> partClass, Function<IPartItem<T>, T> partFactory) {
        return createPart(menuType, partClass, (p) -> new ExtendedCraftingPartItem<>(p, menuType, partClass, partFactory), true);
    }
}
