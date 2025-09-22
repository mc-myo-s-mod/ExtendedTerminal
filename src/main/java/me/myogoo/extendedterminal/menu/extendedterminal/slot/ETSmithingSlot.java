package me.myogoo.extendedterminal.menu.extendedterminal.slot;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.helpers.ICraftingGridMenu;
import appeng.items.storage.ViewCellItem;
import appeng.util.prioritylist.IPartitionList;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ETSmithingSlot extends ETCraftingBaseSlot<SmithingRecipe, SmithingRecipeInput> {
    private static final int SMITHING_SLOT_SIZE = 3;

    public ETSmithingSlot(Player player, IActionSource mySrc, IEnergySource energySrc, MEStorage storage, InternalInventory cMatrix, InternalInventory secondMatrix, ICraftingGridMenu ccp) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, ccp, ETMenuType.ET_TERMINAL);
    }

    @Override
    protected RecipeHolder<SmithingRecipe> findRecipe(SmithingRecipeInput ic, Level level) {
        if (this.menu instanceof ETTerminalMenu terminalMenu) {
            var recipe = terminalMenu.getSmithingRecipe();

            if (recipe != null && recipe.value().matches(ic, level)) {
                return terminalMenu.getSmithingRecipe();
            }
        }

        return level.getRecipeManager().getRecipeFor(RecipeType.SMITHING, ic, level).orElse(null);
    }

    @Override
    protected NonNullList<ItemStack> getETRemainingItems(SmithingRecipeInput ic, Level level) {
        if (this.menu instanceof ETTerminalMenu terminalMenu) {
            var recipe = terminalMenu.getSmithingRecipe();

            if (recipe != null && recipe.value().matches(ic, level)) {
                return terminalMenu.getSmithingRecipe().value().getRemainingItems(ic);
            }
        }
        return NonNullList.create();
    }

    @Override
    protected void makeItem(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);

        var items = NonNullList.withSize(this.craftInv.size(), ItemStack.EMPTY);

        if(this.craftInv.size() != SMITHING_SLOT_SIZE) return;

        for (int i = 0; i < this.craftInv.size(); i++) {
            items.set(i, this.craftInv.getStackInSlot(i));
        }
        var input = new SmithingRecipeInput(items.get(0), items.get(1), items.get(2));

        CommonHooks.setCraftingPlayer(player);
        var remainingItems = this.getETRemainingItems(input, player.level());
        CommonHooks.setCraftingPlayer(null);

        for(int i = 0; i < SMITHING_SLOT_SIZE; i++) {
            this.craftInv.extractItem(i, 1, false);
            if (i < remainingItems.size()) {
                var remainingInSlot = remainingItems.get(i);
                if (!remainingInSlot.isEmpty()) {
                    if (this.craftInv.getStackInSlot(i).isEmpty()) {
                        this.craftInv.setItemDirect(i, remainingInSlot);
                    } else if (!player.getInventory().add(remainingInSlot)) {
                        player.drop(remainingInSlot, false);
                    }
                }
            }
        }
    }

    @Override
    protected ItemStack craftItem(Player p, MEStorage inv, KeyCounter all) {
        var is = this.getItem().copy();
        if (is.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Make sure the item in the slot is still the same item as before
        final var set = new ItemStack[this.getPattern().size()];
        // Safeguard for empty slots in the inventory for now
        Arrays.fill(set, ItemStack.EMPTY);

        // add one of each item to the items on the board...
        var level = p.level();
        if (!level.isClientSide()) {
            final var ic = NonNullList.withSize(SMITHING_SLOT_SIZE, ItemStack.EMPTY);
            for (var x = 0; x < ic.size(); x++) {
                ic.set(x, this.getPattern().getStackInSlot(x));
            }
            var recipeInput = new SmithingRecipeInput(ic.get(0), ic.get(1), ic.get(2));

            final var r = this.findRecipe(recipeInput, level);
            setRecipeUsed(r);

            if (r == null) {
                return ItemStack.EMPTY;
            }

            is = r.value().assemble(recipeInput, level.registryAccess());

            if (inv != null) {
                var filter = ViewCellItem.createItemFilter(this.menu.getViewCells());
                for (var x = 0; x < this.getPattern().size(); x++) {
                    if (!this.getPattern().getStackInSlot(x).isEmpty()) {
                        set[x] = extractItemsByRecipe(this.energySrc, this.mySrc, inv, level, r.value(), is,
                                ic,
                                this.getPattern().getStackInSlot(x), x, all,
                                filter);
                        ic.set(x, set[x]);
                    }
                }
            }
        }

        this.makeItem(p, is);
        this.postCraft(p, inv, set, is);

        p.containerMenu.slotsChanged(this.craftInv.toContainer());
        return is;
    }

    private static ItemStack extractItemsByRecipe(IEnergySource energySrc,
                                                  IActionSource mySrc,
                                                  MEStorage src,
                                                  Level level,
                                                  SmithingRecipe r,
                                                  ItemStack output,
                                                  List<ItemStack> craftingItems,
                                                  ItemStack providedTemplate,
                                                  int slot,
                                                  KeyCounter items,
                                                  IPartitionList filter)
    {
        if (energySrc.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) > 0.9) {
            if (providedTemplate == null) {
                return ItemStack.EMPTY;
            }

            var ae_req = AEItemKey.of(providedTemplate);

            if (filter == null || filter.isListed(ae_req)) {
                var extracted = src.extract(ae_req, 1, Actionable.MODULATE, mySrc);
                if (extracted > 0) {
                    energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    return ae_req.toStack();
                }
            }

            var checkFuzzy = !providedTemplate.getComponents().isEmpty() || providedTemplate.isDamageableItem();

            if (items != null && checkFuzzy) {
                var craftingInputItems = new ArrayList<>(craftingItems);

                for (var x : items) {
                    if (x.getKey() instanceof AEItemKey itemKey) {
                        if (providedTemplate.getItem() == itemKey.getItem() && !itemKey.matches(output)) {

                            craftingInputItems.set(slot, itemKey.toStack());
                            var adjustedCraftingInput = new SmithingRecipeInput(craftingInputItems.get(0), craftingInputItems.get(1), craftingInputItems.get(2));
                            if (r.matches(adjustedCraftingInput, level)
                                    && ItemStack.matches(r.assemble(adjustedCraftingInput, level.registryAccess()),
                                    output)) {
                                if (filter == null || filter.isListed(itemKey)) {
                                    var ex = src.extract(itemKey, 1, Actionable.MODULATE, mySrc);
                                    if (ex > 0) {
                                        energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                                        return itemKey.toStack();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
