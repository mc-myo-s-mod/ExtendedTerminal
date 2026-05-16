package me.myogoo.extendedterminal.mixin.wt;

import appeng.api.networking.IGrid;
import appeng.menu.locator.MenuLocator;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = CraftingTerminalHandler.class, remap = false)
public abstract class CraftingTerminalHandlerMixin {
    @Shadow
    @Final
    private Player player;

    @Shadow
    @Nullable
    private WTMenuHost menuHost;

    @Shadow
    @Nullable
    private MenuLocator locator;

    @Shadow
    @Nullable
    private MagnetHost magnetHost;

    @Shadow
    @Nullable
    private WTMenuHost getMenuHost() {
        throw new AssertionError();
    }

    @Inject(method = "getMenuHost", at = @At("RETURN"), cancellable = true)
    private void extendedterminal$getETMenuHost(CallbackInfoReturnable<WTMenuHost> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }

        var terminalName = ETMenuType.ET_TERMINAL.getWTIdAsString();
        if (!WUTHandler.terminalNames.contains(terminalName)) {
            return;
        }

        this.locator = WUTHandler.findTerminal(player, terminalName);
        this.menuHost = this.locator == null ? null : this.locator.locate(player, WTMenuHost.class);
        this.magnetHost = null;

        if (this.menuHost != null) {
            cir.setReturnValue(this.menuHost);
        }
    }

    @Inject(method = "getTargetGrid", at = @At("HEAD"), cancellable = true)
    private void extendedterminal$loadETTargetGridHost(CallbackInfoReturnable<IGrid> cir) {
        if (getMenuHost() != null) {
            return;
        }

        cir.setReturnValue(null);
    }

}
