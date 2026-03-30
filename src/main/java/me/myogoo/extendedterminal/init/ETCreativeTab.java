package me.myogoo.extendedterminal.init;

import appeng.api.config.Actionable;
import appeng.core.definitions.AEParts;
import appeng.core.definitions.ItemDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ETCreativeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, ExtendedTerminal.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = REGISTER
            .register("creative_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + ExtendedTerminal.MODID))
                    .icon(AEParts.CRAFTING_TERMINAL::stack)
                    .displayItems((params, output) -> {
                        for (ItemDefinition<?> item : ETItems.ITEMS) {
                            output.accept(item);
                        }
                        if (MyotusAPI.get().modIntegrationManager().isLoaded(AE2WTLib.class)) {
                            for (var wt : WTItems.WT_ITEMS) {
                                var stack = wt.stack();
                                output.accept(stack.copy());
                                ItemWT item = (ItemWT) wt.get();
                                item.injectAEPower(stack, item.getAEMaxPower(stack), Actionable.MODULATE);
                                output.accept(stack);
                            }
                        }
                    })
                    .build());

}