package me.myogoo.extendedterminal.mixin;

import appeng.core.sync.BasePacket;
import appeng.core.sync.BasePacketHandler;
import appeng.core.sync.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import me.myogoo.extendedterminal.network.NetworkPacketType;
import net.minecraft.network.FriendlyByteBuf;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetworkHandler.class)
public class NetworkHandlerMixin {
    private static Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "deserializePacket", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private static void deserializePacket(FriendlyByteBuf payload, CallbackInfoReturnable<BasePacket> cir)  {
        cir.cancel();
        var packetId = payload.readInt();
        if (packetId < BasePacketHandler.PacketTypes.values().length) {
            cir.setReturnValue(BasePacketHandler.PacketTypes.getPacket(packetId).parsePacket(payload));
        }
        if(packetId >= NetworkPacketType.ET_PACKET_START_ID) {
            if(NetworkPacketType.get(packetId).isPresent()) {
                cir.setReturnValue(NetworkPacketType.get(packetId).get().parsePacket(payload));
            } else {
                LOGGER.error("Unknown packet id: " + packetId + " from " + NetworkHandler.class.getName());
            }
        }
    }
}