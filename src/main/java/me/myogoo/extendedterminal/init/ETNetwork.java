package me.myogoo.extendedterminal.init;

import appeng.core.network.ServerboundPacket;
import com.blakebr0.extendedcrafting.ExtendedCrafting;
import me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class ETNetwork {
    public static void init(RegisterPayloadHandlersEvent event) {
        var register = event.registrar(ExtendedCrafting.MOD_ID);

        register.playToServer(FillTableCraftingGridFromRecipePacket.TYPE, FillTableCraftingGridFromRecipePacket.STREAM_CODEC, ServerboundPacket::handleOnServer);
    }
}
