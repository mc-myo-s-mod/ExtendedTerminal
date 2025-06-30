package com.myogoo.extendedterminal.init;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.part.extendedcrafting.AdvancedTerminalPart;
import com.myogoo.extendedterminal.part.extendedcrafting.BasicExtendedTerminalPart;
import com.myogoo.extendedterminal.part.extendedcrafting.EliteTerminalPart;
import com.myogoo.extendedterminal.part.extendedcrafting.UltimateTerminalPart;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ETParts {
    //useless but call ETPart class to register
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ExtendedTerminal.MODID);

    public static final List<ItemDefinition<? extends PartItem<?>>> PARTS = new ArrayList<>();
    public static final List<ItemDefinition<? extends PartItem<?>>> TERMINAL_PARTS = new ArrayList<>();

    public static final ItemDefinition<PartItem<BasicExtendedTerminalPart>> BASIC_TERMINAL_PART = createTerminalPart(ETMenuType.BASIC_TERMINAL, BasicExtendedTerminalPart.class, BasicExtendedTerminalPart::new);
    public static final ItemDefinition<PartItem<AdvancedTerminalPart>> ADVANCED_TERMINAL_PART = createTerminalPart(ETMenuType.ADVANCED_TERMINAL, AdvancedTerminalPart.class, AdvancedTerminalPart::new);
    public static final ItemDefinition<PartItem<EliteTerminalPart>> ELITE_TERMINAL_PART = createTerminalPart(ETMenuType.ELITE_TERMINAL, EliteTerminalPart.class, EliteTerminalPart::new);
    public static final ItemDefinition<PartItem<UltimateTerminalPart>> ULTIMATE_TERMINAL_PART = createTerminalPart(ETMenuType.ULTIMATE_TERMINAL, UltimateTerminalPart.class, UltimateTerminalPart::new);

    private static <T extends IPart>ItemDefinition<PartItem<T>> createPart(String name, ResourceLocation id, Class<T> partClass, Function<IPartItem<T>, T> partFactory, boolean terminalPart) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        var definition = ETItems.createItem(name,id, (p) -> new PartItem<>(p, partClass, partFactory));
        PARTS.add(definition);
        if (terminalPart) { TERMINAL_PARTS.add(definition);}
        return definition;
    }

    private static <T extends IPart>ItemDefinition<PartItem<T>> createTerminalPart(ETMenuType etMenuType, Class<T> partClass, Function<IPartItem<T>, T> partFactory) {
        return createPart(etMenuType.getEnglishName(), etMenuType.getId(), partClass, partFactory,true);
    }
}
