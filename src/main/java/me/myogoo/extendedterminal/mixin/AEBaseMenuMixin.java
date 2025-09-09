package me.myogoo.extendedterminal.mixin;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = AEBaseMenu.class, remap = false)
public class AEBaseMenuMixin {
    @Shadow @Final private Inventory playerInventory;

    @Shadow @Final private Map<Slot, SlotSemantic> semanticBySlot;

    @Inject(method = "isPlayerSideSlot", at = @At("HEAD"), cancellable = true, remap = false)
    public void isPlayerSideSlot(Slot slot, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        if(slot.container == playerInventory) {
            cir.setReturnValue(true);
        }

        SlotSemantic slotSemantic = semanticBySlot.get(slot);
        cir.setReturnValue(slotSemantic.playerSide());
    }
}
