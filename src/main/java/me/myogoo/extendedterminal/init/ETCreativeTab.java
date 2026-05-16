package me.myogoo.extendedterminal.init;

import appeng.api.config.Actionable;
import appeng.core.definitions.ItemDefinition;
import appeng.items.tools.powered.WirelessTerminalItem;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;

public class ETCreativeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExtendedTerminal.MODID);

    static {
        REGISTER.register("itemgroup.extendedcrafting",() -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.extendedterminal"))
                .icon(ETParts.ET_TERMINAL_PART::stack)
                .displayItems((params, output) -> {
                    for (ItemDefinition<?> item : ETItems.ITEMS) {
                        output.accept(item.stack());
                    }
                    if (MyotusAPI.modIntegrationManager().isLoaded(AE2WTLib.class)) {
                        for (ItemDefinition<?> item : WTItems.WT_ITEMS) {
                            output.accept(item.stack());
                            var chargedStack = item.stack().copy();
                            if(item.asItem() instanceof WirelessTerminalItem terminal) {
                                terminal.injectAEPower(chargedStack, terminal.getAEMaxPower(chargedStack), Actionable.MODULATE);
                                output.accept(chargedStack);
                            }
                        }
                    }
                })
                .build());
    }
}
