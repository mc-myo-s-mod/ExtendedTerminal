package me.myogoo.extendedterminal.item.partitem;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.items.parts.PartItem;
import com.blakebr0.extendedcrafting.lib.ModTooltips;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class ExtendedCraftingPartItem<T extends IPart> extends PartItem<T> {
    private final ETMenuType menuType;
    public ExtendedCraftingPartItem(Properties properties, ETMenuType menuType, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        super(properties, partClass, factory);
        this.menuType = menuType;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(ModTooltips.TIER.args(new Object[]{menuType.getTier()}).build());
    }
}
