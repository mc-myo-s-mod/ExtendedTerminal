package me.myogoo.extendedterminal.me.host;

import appeng.api.ids.AEComponents;
import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.items.contents.StackDependentSupplier;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.SupplierInternalInventory;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static me.myogoo.extendedterminal.init.ETDataComponent.*;
import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.SmithingInventory;
import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.StoneCutterInventory;

public class ETWTHost extends WTMenuHost implements IViewCellStorage, ITerminalHost {
    private final SupplierInternalInventory<InternalInventory> craftingGrid;
    private final SupplierInternalInventory<InternalInventory> smithingGrid;
    private final SupplierInternalInventory<InternalInventory> stoneCutterGrid;

    public ETWTHost(ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        this.craftingGrid = new SupplierInternalInventory(new StackDependentSupplier(this::getItemStack, (stack) -> createInv(player,(ItemStack) stack, CRAFTING_INV, 9)));
        this.smithingGrid = new SupplierInternalInventory(new StackDependentSupplier(this::getItemStack, (stack) -> createInv(player,(ItemStack) stack, SMITHING_INV, 3)));
        this.stoneCutterGrid = new SupplierInternalInventory(new StackDependentSupplier(this::getItemStack, (stack) -> createInv(player,(ItemStack) stack, STONECUTTER_INV, 1)));
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ETMenuType.ET_TERMINAL.getCraftingInventory())) {
            return craftingGrid;
        } else if (id.equals(SmithingInventory)) {
            return smithingGrid;
        } else if (id.equals(StoneCutterInventory)) {
            return stoneCutterGrid;
        }
        return super.getSubInventory(id);
    }
}
