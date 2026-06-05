package me.myogoo.extendedterminal.network.serverbound;

import appeng.core.network.ServerboundPacket;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class CycleUnitedRecipeKindPacket implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, CycleUnitedRecipeKindPacket> STREAM_CODEC = StreamCodec
            .ofMember(CycleUnitedRecipeKindPacket::write, CycleUnitedRecipeKindPacket::decode);

    public static final CustomPacketPayload.Type<CycleUnitedRecipeKindPacket> TYPE = new CustomPacketPayload
            .Type<>(ExtendedTerminal.makeId("cycle_united_recipe_kind"));

    @Override
    public CustomPacketPayload.@NotNull Type<CycleUnitedRecipeKindPacket> type() {
        return TYPE;
    }

    private void write(RegistryFriendlyByteBuf stream) {
    }

    private static CycleUnitedRecipeKindPacket decode(RegistryFriendlyByteBuf stream) {
        return new CycleUnitedRecipeKindPacket();
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.containerMenu instanceof UnitedTerminalMenu menu) {
            menu.selectNextRecipeKind();
        }
    }
}
