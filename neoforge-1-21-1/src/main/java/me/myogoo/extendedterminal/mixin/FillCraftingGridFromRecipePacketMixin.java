package me.myogoo.extendedterminal.mixin;

import appeng.core.network.serverbound.FillCraftingGridFromRecipePacket;
import appeng.helpers.ICraftingGridMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.network.serverbound.IETFillRecipeBasePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FillCraftingGridFromRecipePacket.class, remap = false)
public abstract class FillCraftingGridFromRecipePacketMixin implements IETFillRecipeBasePacket {

    @Shadow
    protected abstract ItemStack takeIngredientFromPlayer(ICraftingGridMenu cct, ServerPlayer player, Ingredient ingredient);

    @Inject(method = "handleOnServer", at = @At("HEAD"), remap = false)
    private void et$onHandleOnServer(ServerPlayer player, CallbackInfo ci) {
        if (player.containerMenu instanceof ETTerminalMenu menu) {
            menu.setMode(ETTerminalMode.CRAFTING);
        }
    }

    @Redirect(method = "handleOnServer", at = @At(value = "INVOKE", target = "Lappeng/core/network/serverbound/FillCraftingGridFromRecipePacket;takeIngredientFromPlayer(Lappeng/helpers/ICraftingGridMenu;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/crafting/Ingredient;)Lnet/minecraft/world/item/ItemStack;"), remap = false)
    ItemStack et$handleOnServer(FillCraftingGridFromRecipePacket instance, ICraftingGridMenu cct, ServerPlayer player, Ingredient ingredient) {
        var invResult = takeIngredientFromPlayer(cct, player, ingredient);
        if (!invResult.isEmpty()) {
            return invResult;
        } else {
            return takeIngredientFromOtherGrid(cct, ingredient);
        }
    }

}
