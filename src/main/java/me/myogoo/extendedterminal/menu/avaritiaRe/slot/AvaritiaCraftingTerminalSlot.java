package me.myogoo.extendedterminal.menu.avaritiaRe.slot;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.helpers.IMenuCraftingPacket;
import appeng.items.storage.ViewCellItem;
import appeng.util.prioritylist.IPartitionList;
import committee.nova.mods.avaritia.common.crafting.recipe.BaseTableCraftingRecipe;
import committee.nova.mods.avaritia.init.registry.ModRecipeTypes;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.AvaritiaTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class AvaritiaCraftingTerminalSlot extends ETCraftingBaseSlot<BaseTableCraftingRecipe, CraftingContainer> {
    public AvaritiaCraftingTerminalSlot(Player player, IActionSource mySrc, IEnergySource energySrc, MEStorage storage, InternalInventory cMatrix, InternalInventory secondMatrix, IMenuCraftingPacket ccp, ETMenuType menuType) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, ccp, menuType);
    }

    protected BaseTableCraftingRecipe findRecipe(CraftingContainer ic, Level level) {
        if (this.menu instanceof AvaritiaTerminalBaseMenu terminalMenu) {
            var recipe = terminalMenu.getCurrentRecipe();

            if (recipe != null && recipe.matches(ic, level)) {
                return terminalMenu.getCurrentRecipe();
            }
        }

        return level.getRecipeManager().getRecipeFor(ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), ic, level).orElse(null);
    }

    @Override
    protected NonNullList<ItemStack> getRemainingItems(CraftingContainer ic, Level level) {
        if (this.menu instanceof AvaritiaTerminalBaseMenu terminalMenu) {
            var recipe = terminalMenu.getCurrentRecipe();

            if (recipe != null && recipe.matches(ic, level)) {
                return terminalMenu.getCurrentRecipe().getRemainingItems(ic);
            }
        }

        return super.getRemainingItems(ic, level);
    }

    @Override
    protected ItemStack craftItem(Player p, MEStorage inv, KeyCounter all) {
        // update crafting matrix...
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
            final var ic = NonNullList.withSize(menuType.getGridSize(), ItemStack.EMPTY);
            for (var x = 0; x < menuType.getGridSize(); x++) {
                ic.set(x, this.getPattern().getStackInSlot(x));
            }
            var recipeContainer = new TransientCraftingContainer(this.getMenu(), menuType.getGridSideLength(), menuType.getGridSideLength(), ic);

            final var r = this.findRecipe(recipeContainer, level);

            if (r == null) {
                return ItemStack.EMPTY;
            }

            is = r.assemble(recipeContainer, level.registryAccess());

            if (inv != null) {
                var filter = ViewCellItem.createItemFilter(this.menu.getViewCells());
                for (var x = 0; x < this.getPattern().size(); x++) {
                    if (!this.getPattern().getStackInSlot(x).isEmpty()) {
                        set[x] = extractItemsByRecipe(this.energySrc, this.mySrc, inv, level, r, is,
                                recipeContainer.getWidth(), recipeContainer.getHeight(),
                                (AbstractContainerMenu) this.menu,
                                ic,
                                this.getPattern().getStackInSlot(x), x, all,
                                filter,
                                menuType);
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
                                                  BaseTableCraftingRecipe r,
                                                  ItemStack output,
                                                  int gridWidth, int gridHeight,
                                                  AbstractContainerMenu menu,
                                                  List<ItemStack> craftingItems,
                                                  ItemStack providedTemplate,
                                                  int slot,
                                                  KeyCounter items,
                                                  IPartitionList filter,
                                                  ETMenuType menuType) {
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

            var checkFuzzy = providedTemplate.getTags().findAny().isEmpty() || providedTemplate.isDamageableItem();

            if (items != null && checkFuzzy) {
                var craftingInputItems = NonNullList.withSize(craftingItems.size(), ItemStack.EMPTY);
                for (int i = 0; i < craftingItems.size(); i++) {
                    craftingInputItems.set(i, craftingItems.get(i).copy());
                }

                for (var x : items) {
                    if (x.getKey() instanceof AEItemKey itemKey) {
                        if (providedTemplate.getItem() == itemKey.getItem() && !itemKey.matches(output)) {

                            craftingInputItems.set(slot, itemKey.toStack());
                            var adjustedCraftingInput = new TransientCraftingContainer(menu, gridWidth, gridHeight, craftingInputItems);
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
