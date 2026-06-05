package me.myogoo.extendedterminal.me.host;

import appeng.api.inventories.InternalInventory;
import appeng.items.contents.StackDependentSupplier;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.SupplierInternalInventory;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static me.myogoo.extendedterminal.init.ETDataComponent.CRAFTING_INV;

public class UnitedWTHost extends WCTMenuHost {
    private final SupplierInternalInventory<InternalInventory> craftingGrid;

    public UnitedWTHost(ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        this.craftingGrid = new SupplierInternalInventory<>(new StackDependentSupplier(this::getItemStack,
                stack -> createInv(player, (ItemStack) stack, CRAFTING_INV,
                        ETMenuType.UNITED_TERMINAL.getGridSize())));
    }

    @Override
    public @Nullable InternalInventory getSubInventory(net.minecraft.resources.ResourceLocation id) {
        if (id.equals(ETMenuType.UNITED_TERMINAL.getCraftingInventory())) {
            return craftingGrid;
        }
        return super.getSubInventory(id);
    }
}
