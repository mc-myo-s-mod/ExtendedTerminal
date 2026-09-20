package me.myogoo.extendedterminal.mixin;

import appeng.menu.AEBaseMenu;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AEBaseMenu.class, remap = false)
public class AEBaseMenuMixin {
    @Inject(method = "isPlayerSideSlot(Lnet/minecraft/world/inventory/Slot;)Z", at = @At("RETURN"), cancellable = true, remap = false, require = 1)
    private void extendedterminal$customPlayerSideSlot(Slot slot, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() || !((Object) this instanceof ETTerminalBaseMenu<?> menu)) {
            return;
        }

        var slotSemantic = menu.getSlotSemantic(slot);
        if (slotSemantic != null && slotSemantic.playerSide()) {
            cir.setReturnValue(true);
        }
    }
}
