package me.myogoo.extendedterminal.network;

import appeng.core.sync.BasePacket;
import me.myogoo.extendedterminal.network.serverbound.ETFillCraftingGridFromRecipePacket;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class NetworkPacketType {
    public static int ET_PACKET_START_ID = 10000;

    public enum PacketIDs {
        EXTENDED_FILL_CRAFTING_GRID(10000);

        private final int value;

        PacketIDs(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    private static final HashMap<Integer,Packet> packetMap = new HashMap<>() {
        {
            put(PacketIDs.EXTENDED_FILL_CRAFTING_GRID.value,new Packet(ETFillCraftingGridFromRecipePacket.class, ETFillCraftingGridFromRecipePacket::new));
        }
    };

    public static Optional<Packet> get(int packetId) {
        Packet packet = packetMap.get(packetId);
        return Optional.ofNullable(packet);
    }


    public record Packet(Class<? extends BasePacket> clazz, Function<FriendlyByteBuf, BasePacket> factory) {

        public BasePacket parsePacket(FriendlyByteBuf payload) {
            return factory.apply(payload);
        }

    }
}
