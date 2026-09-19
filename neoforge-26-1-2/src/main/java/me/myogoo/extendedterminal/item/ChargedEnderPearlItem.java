package me.myogoo.extendedterminal.item;

import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ChargedEnderPearlItem extends me.myogoo.myotus.item.ChargedEnderPearlItem {
    public ChargedEnderPearlItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(ETTranslationKey.ITEM.ITEM_DEPRECATED_MATERIAL_TOOLTIP.key())
                .withStyle(ChatFormatting.YELLOW));
    }

}
