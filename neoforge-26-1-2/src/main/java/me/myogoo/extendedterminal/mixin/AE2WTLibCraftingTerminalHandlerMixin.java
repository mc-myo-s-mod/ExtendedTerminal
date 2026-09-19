package me.myogoo.extendedterminal.mixin;

import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.api.terminal.WUTHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;
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
@Mixin(targets = "de.mari_023.ae2wtlib.wct.CraftingTerminalHandler", remap = false)
public abstract class AE2WTLibCraftingTerminalHandlerMixin {
    @Shadow
    @Final
    public Player player;

    @Shadow
    @Nullable
    private WTMenuHost menuHost;

    @Shadow
    @Nullable
    private ItemMenuHostLocator locator;

    @Shadow
    @Nullable
    private MagnetHost magnetHost;

    @Inject(
            method = "getLocator()Lappeng/menu/locator/ItemMenuHostLocator;",
            at = @At("HEAD"),
            cancellable = true,
            require = 1,
            expect = 1)
    private void extendedterminal$useCachedETLocator(CallbackInfoReturnable<ItemMenuHostLocator> cir) {
        var etDefinition = getETDefinition();
        if (etDefinition == null || locator == null) {
            return;
        }

        if (WUTHandler.hasTerminal(locator.locateItem(player), etDefinition)) {
            cir.setReturnValue(locator);
        }
    }

    @Inject(
            method = "getLocator()Lappeng/menu/locator/ItemMenuHostLocator;",
            at = @At(value = "RETURN", ordinal = 2),
            cancellable = true,
            require = 1,
            expect = 1)
    private void extendedterminal$findETLocator(CallbackInfoReturnable<ItemMenuHostLocator> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }

        var etDefinition = getETDefinition();
        if (etDefinition == null) {
            return;
        }

        var etLocator = WUTHandler.findTerminal(player, etDefinition);
        if (etLocator == null) {
            return;
        }

        locator = etLocator;
        menuHost = null;
        magnetHost = null;
        cir.setReturnValue(etLocator);
    }

    private static @Nullable WTDefinition getETDefinition() {
        var name = ETMenuType.ET_TERMINAL.getWTIdAsString();
        if (!WTDefinition.exists(name)) {
            return null;
        }
        return WTDefinition.of(name);
    }
}
