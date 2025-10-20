package me.myogoo.extendedterminal.menu.extendedterminal.slot;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import appeng.menu.slot.AppEngCraftingSlot;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.FakeAnvilMenu;
import me.myogoo.extendedterminal.util.mod.ModIntegrationManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.CommonHooks;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;


public class ETAnvilSlot extends AppEngCraftingSlot {
    private final FakeAnvilMenu anvilDelegate;
    public ETAnvilSlot(Player player, IActionSource mySrc, IEnergySource energySrc, MEStorage storage,InternalInventory anvilInv, FakeAnvilMenu anvilDelegate, ETTerminalMenu terminalMenu) {
        super(player, anvilInv);
        this.anvilDelegate = anvilDelegate;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            int cost = -this.anvilDelegate.getCost();
            if(ModIntegrationManager.isLoaded(ModAccessor.ApothicEnchant.class)) {
                EnchantmentUtils.chargeExperience(player, EnchantmentUtils.getTotalExperienceForLevel(cost))    ;
            } else {
                player.giveExperienceLevels(cost);
            }
        }

        var leftSlot = this.getMenu().getSlots(ETSlotSemantics.ANVIL_LEFT_INPUT).getFirst();
        var rightSlot = this.getMenu().getSlots(ETSlotSemantics.ANVIL_RIGHT_INPUT).getFirst();

        CommonHooks.onAnvilRepair(player, stack, leftSlot.getItem(), rightSlot.getItem());

        leftSlot.set(ItemStack.EMPTY);
        if (this.anvilDelegate.repairItemCountCost > 0) {
            ItemStack itemstack = rightSlot.getItem();
            if (!itemstack.isEmpty() && itemstack.getCount() > this.anvilDelegate.repairItemCountCost) {
                itemstack.shrink(this.anvilDelegate.repairItemCountCost);
                rightSlot.set(itemstack);
            } else {
                rightSlot.set(ItemStack.EMPTY);
            }
        } else {
            rightSlot.set(ItemStack.EMPTY);
        }

        this.anvilDelegate.cost.set(0);
    }

    @Override
    public boolean mayPickup(Player player) {
        return (player.hasInfiniteMaterials() || player.experienceLevel >= anvilDelegate.getCost()) && anvilDelegate.getCost() > 0;
    }
}
