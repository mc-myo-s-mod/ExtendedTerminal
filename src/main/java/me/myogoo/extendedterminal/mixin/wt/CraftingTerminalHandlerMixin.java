package me.myogoo.extendedterminal.mixin.wt;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.menu.locator.MenuLocator;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = CraftingTerminalHandler.class, remap = false)
public abstract class CraftingTerminalHandlerMixin {
    @Shadow
    @Final
    private Player player;

    @Unique
    @Nullable
    private WTMenuHost extendedterminal$menuHost;

    @Unique
    @Nullable
    private MenuLocator extendedterminal$locator;

    @Unique
    @Nullable
    private MagnetHost extendedterminal$magnetHost;

    @Inject(method = "invalidateCache", at = @At("TAIL"))
    private void extendedterminal$invalidateETCache(CallbackInfo ci) {
        extendedterminal$menuHost = null;
        extendedterminal$locator = null;
        extendedterminal$magnetHost = null;
    }

    @Inject(method = "getCraftingTerminal", at = @At("RETURN"), cancellable = true)
    private void extendedterminal$getETCraftingTerminal(CallbackInfoReturnable<ItemStack> cir) {
        if (!cir.getReturnValue().isEmpty()) {
            return;
        }

        var host = extendedterminal$getETMenuHost();
        if (host != null) {
            cir.setReturnValue(host.getItemStack());
        }
    }

    @Inject(method = "getLocator", at = @At("RETURN"), cancellable = true)
    private void extendedterminal$getETLocator(CallbackInfoReturnable<MenuLocator> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }

        if (extendedterminal$getETMenuHost() != null) {
            cir.setReturnValue(extendedterminal$locator);
        }
    }

    @Inject(method = "getTargetGrid", at = @At("HEAD"), cancellable = true)
    private void extendedterminal$getETTargetGrid(CallbackInfoReturnable<IGrid> cir) {
        if (extendedterminal$hasCraftingTerminal()) {
            return;
        }

        var host = extendedterminal$getETMenuHost();
        if (host == null) {
            return;
        }

        IGridNode node = host.getActionableNode();
        cir.setReturnValue(node == null ? null : node.getGrid());
    }

    @Inject(method = "inRange", at = @At("RETURN"), cancellable = true)
    private void extendedterminal$inETRange(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }

        var host = extendedterminal$getETMenuHost();
        if (host != null) {
            cir.setReturnValue(host.rangeCheck());
        }
    }

    @Inject(method = "getMagnetHost", at = @At("HEAD"), cancellable = true)
    private void extendedterminal$getETMagnetHost(CallbackInfoReturnable<MagnetHost> cir) {
        if (extendedterminal$hasCraftingTerminal()) {
            return;
        }

        if (extendedterminal$getETMenuHost() == null) {
            extendedterminal$magnetHost = null;
            return;
        }

        if (extendedterminal$magnetHost == null) {
            extendedterminal$magnetHost = new MagnetHost((CraftingTerminalHandler) (Object) this);
        }
        cir.setReturnValue(extendedterminal$magnetHost);
    }

    @Unique
    private boolean extendedterminal$hasCraftingTerminal() {
        return WUTHandler.findTerminal(player, "crafting") != null;
    }

    @Unique
    private @Nullable WTMenuHost extendedterminal$getETMenuHost() {
        if (extendedterminal$menuHost != null
                && extendedterminal$menuHost.rangeCheck()
                && extendedterminal$menuHost.stillValid()) {
            return extendedterminal$menuHost;
        }

        extendedterminal$locator = extendedterminal$findETLocator();
        if (extendedterminal$locator == null) {
            extendedterminal$menuHost = null;
            extendedterminal$magnetHost = null;
            return null;
        }

        extendedterminal$menuHost = extendedterminal$locator.locate(player, WTMenuHost.class);
        if (extendedterminal$menuHost == null) {
            extendedterminal$locator = null;
            extendedterminal$magnetHost = null;
        }
        return extendedterminal$menuHost;
    }

    @Unique
    private @Nullable MenuLocator extendedterminal$findETLocator() {
        var terminalName = ETMenuType.ET_TERMINAL.getWTIdAsString();
        if (!WUTHandler.terminalNames.contains(terminalName)) {
            return null;
        }
        return WUTHandler.findTerminal(player, terminalName);
    }
}
