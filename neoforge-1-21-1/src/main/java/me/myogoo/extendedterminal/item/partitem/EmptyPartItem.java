package me.myogoo.extendedterminal.item.partitem;

import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.items.parts.PartItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Function;

public class EmptyPartItem<T extends IPart> extends PartItem<T> {
    public EmptyPartItem(Properties properties, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        super(properties, partClass, factory);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(ETTranslationKey.ITEM.ITEM_UNREGISTERED_TERMINAL_TOOLTIP.key()));
    }

}
