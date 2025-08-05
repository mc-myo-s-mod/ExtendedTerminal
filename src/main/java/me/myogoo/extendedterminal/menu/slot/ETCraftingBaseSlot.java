package me.myogoo.extendedterminal.menu.slot;

import appeng.api.config.Actionable;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.helpers.ICraftingGridMenu;
import appeng.helpers.InventoryAction;
import appeng.menu.slot.AppEngCraftingSlot;
import appeng.util.Platform;
import appeng.util.inv.CarriedItemInventory;
import appeng.util.inv.PlayerInternalInventory;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public abstract class ETCraftingBaseSlot<R extends Recipe<?>, I extends CraftingInput> extends AppEngCraftingSlot {
    protected final InternalInventory craftInv;
    private final InternalInventory pattern;

    protected final IActionSource mySrc;
    protected final IEnergySource energySrc;
    protected final ICraftingGridMenu menu;
    private final MEStorage storage;

    protected final ETMenuType menuType;

    public ETCraftingBaseSlot(Player player, IActionSource mySrc, IEnergySource energySrc,
                              MEStorage storage, InternalInventory cMatrix, InternalInventory secondMatrix,
                              ICraftingGridMenu ccp, ETMenuType menuType) {
        super(player, cMatrix);
        this.energySrc = energySrc;
        this.storage = storage;
        this.mySrc = mySrc;
        this.pattern = cMatrix;
        this.craftInv = secondMatrix;
        this.menu = ccp;
        this.menuType = menuType;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public void onTake(Player player, ItemStack is) { }

    public void doClick(InventoryAction action, Player who) {
        if (this.getItem().isEmpty()) {
            return;
        }
        if (isRemote()) {
            return;
        }

        final var howManyPerCraft = this.getItem().getCount();

        int maxTimesToCraft;
        InternalInventory target;
        if (action == InventoryAction.CRAFT_SHIFT || action == InventoryAction.CRAFT_ALL) // craft into player
        // inventory...
        {
            target = new PlayerInternalInventory(who.getInventory());
            if (action == InventoryAction.CRAFT_SHIFT) {
                maxTimesToCraft = (int) Math
                        .floor((double) this.getItem().getMaxStackSize() / (double) howManyPerCraft);
            } else {
                maxTimesToCraft = (int) Math.floor((double) this.getItem().getMaxStackSize() / (double) howManyPerCraft
                        * Inventory.INVENTORY_SIZE);
            }
        } else if (action == InventoryAction.CRAFT_STACK) // craft into hand, full stack
        {
            target = new CarriedItemInventory(getMenu());
            maxTimesToCraft = (int) Math.floor((double) this.getItem().getMaxStackSize() / (double) howManyPerCraft);
        } else
        // pick up what was crafted...
        {
            // This is a shortcut to ensure that for mods that create recipes with result counts larger than
            // the max stack size, it remains possible to pick up those items at least _once_.
            if (getMenu().getCarried().isEmpty()) {
                getMenu().setCarried(craftItem(who, storage, storage.getAvailableStacks()));
                return;
            }

            target = new CarriedItemInventory(getMenu());
            maxTimesToCraft = 1;
        }

        // Since we may be crafting multiple times, we have to ensure that we keep crafting the same item.
        // This may not be the case if not all crafting grid slots have the same number of items in them,
        // and some ingredients run-out after a few crafts.
        var itemAtStart = this.getItem().copy();
        if (itemAtStart.isEmpty()) {
            return;
        }

        for (var x = 0; x < maxTimesToCraft; x++) {
            // Stop if the recipe output has changed (i.e. due to fully consumed input slots)
            if (!ItemStack.isSameItemSameComponents(itemAtStart, getItem())) {
                return;
            }

            // Stop if the target inventory is full
            if (!target.simulateAdd(itemAtStart).isEmpty()) {
                return;
            }

            var all = storage.getAvailableStacks();
            var extra = target.addItems(craftItem(who, storage, all));

            // If we couldn't actually add what we crafted, we drop it and stop
            if (!extra.isEmpty()) {
                Platform.spawnDrops(who.level(), who.blockPosition(), List.of(extra));
                return;
            }
        }
    }

    protected abstract RecipeHolder<R> findRecipe(I ic, Level level);

    protected abstract NonNullList<ItemStack> getETRemainingItems(I ic, Level level);

    protected abstract void makeItem(Player player, ItemStack stack);

    protected abstract ItemStack craftItem(Player p, MEStorage inv, KeyCounter all);


    protected void postCraft(Player p, MEStorage inv, ItemStack[] set,
                           ItemStack result) {
        final List<ItemStack> drops = new ArrayList<>();

        // add one of each item to the items on the board...
        if (!p.getCommandSenderWorld().isClientSide()) {
            // set new items onto the crafting table...
            for (var x = 0; x < this.craftInv.size(); x++) {
                if (this.craftInv.getStackInSlot(x).isEmpty()) {
                    this.craftInv.setItemDirect(x, set[x]);
                } else if (!set[x].isEmpty()) {
                    var what = AEItemKey.of(set[x]);
                    var amount = set[x].getCount();
                    var inserted = inv.insert(what, amount, Actionable.MODULATE,
                            this.mySrc);
                    // eek! put it back!
                    if (inserted < amount) {
                        drops.add(what.toStack((int) (amount - inserted)));
                    }
                }
            }
        }

        if (!drops.isEmpty()) {
            Platform.spawnDrops(p.level(), new BlockPos((int) p.getX(), (int) p.getY(), (int) p.getZ()), drops);
        }
    }

    protected InternalInventory getPattern() {
        return this.pattern;
    }


}

