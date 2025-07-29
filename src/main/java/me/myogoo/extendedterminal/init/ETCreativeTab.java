package me.myogoo.extendedterminal.init;

import appeng.core.definitions.ItemDefinition;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.config.ETConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ETCreativeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExtendedTerminal.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = REGISTER.register("creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + ExtendedTerminal.MODID))
            .icon(ETParts.ULTIMATE_TERMINAL_PART::stack)
            .displayItems((params, output) -> {
                for (ItemDefinition<?> item : ETItems.ITEMS) {
                    if (!ETConfig.DISABLED_TERMINALS.getOrDefault(item, false)) {
                        output.accept(item);
                    }
                }
            })
            .build());
}