package me.myogoo.extendedterminal.init;

import appeng.core.definitions.ItemDefinition;
import me.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;

public class ETCreativeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExtendedTerminal.MODID);

    static {
        REGISTER.register("itemgroup.extendedcrafting",() -> CreativeModeTab.builder()
                .title(Component.translatable("itemgroup.extendedcrafting.title"))
                .icon(ETParts.ULTIMATE_TERMINAL_PART::stack)
                .displayItems((params, output) -> {
                    for (ItemDefinition<?> item : ETItems.ITEMS) {
                        output.accept(item.stack());
                    }
                })
                .build());
    }
}