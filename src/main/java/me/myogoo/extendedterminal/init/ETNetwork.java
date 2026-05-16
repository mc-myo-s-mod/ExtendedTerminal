package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.network.serverbound.ETFillCraftingGridFromRecipePacket;
import me.myogoo.extendedterminal.network.serverbound.ETFillSmithingGridFromRecipePacket;
import me.myogoo.extendedterminal.network.serverbound.ETFillStonecutterGridFromRecipePacket;
import me.myogoo.extendedterminal.network.serverbound.ETPickBlockPacket;
import me.myogoo.myotus.api.MyotusAPI;

public final class ETNetwork {
    public static final int TABLE_FILL_CRAFTING_GRID_PACKET_ID = 10000;
    public static final int SMITHING_FILL_CRAFTING_GRID_PACKET_ID = 10001;
    public static final int STONECUTTER_FILL_CRAFTING_GRID_PACKET_ID = 10002;
    public static final int PICK_BLOCK_PACKET_ID = 10003;

    private static boolean registered;
    private static boolean registeredAE2WTLibPackets;

    private ETNetwork() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }

        registered = true;
        var network = MyotusAPI.network();
        network.registerServerbound(TABLE_FILL_CRAFTING_GRID_PACKET_ID,
                ETFillCraftingGridFromRecipePacket.class,
                ETFillCraftingGridFromRecipePacket::new,
                ETFillCraftingGridFromRecipePacket::handle);
        network.registerServerbound(SMITHING_FILL_CRAFTING_GRID_PACKET_ID,
                ETFillSmithingGridFromRecipePacket.class,
                ETFillSmithingGridFromRecipePacket::new,
                ETFillSmithingGridFromRecipePacket::handle);
        network.registerServerbound(STONECUTTER_FILL_CRAFTING_GRID_PACKET_ID,
                ETFillStonecutterGridFromRecipePacket.class,
                ETFillStonecutterGridFromRecipePacket::new,
                ETFillStonecutterGridFromRecipePacket::handle);
    }

    public static synchronized void registerAE2WTLibPackets() {
        if (registeredAE2WTLibPackets) {
            return;
        }

        registeredAE2WTLibPackets = true;
        var network = MyotusAPI.network();
        network.registerServerbound(PICK_BLOCK_PACKET_ID,
                ETPickBlockPacket.class,
                ETPickBlockPacket::new,
                ETPickBlockPacket::handle);
    }
}
