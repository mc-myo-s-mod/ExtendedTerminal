package me.myogoo.extendedterminal.init;

import appeng.core.network.ServerboundPacket;
import com.blakebr0.extendedcrafting.ExtendedCrafting;
import me.myogoo.extendedterminal.network.serverbound.AVFillCraftingGridFromRecipePacket;
import me.myogoo.extendedterminal.network.serverbound.ECFillCraftingGridFromRecipePacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class ETNetwork {
    public static void init(RegisterPayloadHandlersEvent event) {
        var register = event.registrar(ExtendedCrafting.MOD_ID);

        register.playToServer(ECFillCraftingGridFromRecipePacket.TYPE, ECFillCraftingGridFromRecipePacket.STREAM_CODEC, ServerboundPacket::handleOnServer);
        register.playToServer(AVFillCraftingGridFromRecipePacket.TYPE, AVFillCraftingGridFromRecipePacket.STREAM_CODEC, ServerboundPacket::handleOnServer);

    }
}
