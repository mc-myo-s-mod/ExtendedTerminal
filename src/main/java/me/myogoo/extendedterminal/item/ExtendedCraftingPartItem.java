package me.myogoo.extendedterminal.item;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.items.parts.PartItem;
import com.blakebr0.extendedcrafting.lib.ModTooltips;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Function;

public class ExtendedCraftingPartItem<T extends IPart> extends PartItem<T> {
    private final ETMenuType menuType;
    public ExtendedCraftingPartItem(Properties properties, ETMenuType menuType, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        super(properties, partClass, factory);
        this.menuType = menuType;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(ModTooltips.TIER.args(new Object[]{menuType.getTier()}).build());
    }
}
