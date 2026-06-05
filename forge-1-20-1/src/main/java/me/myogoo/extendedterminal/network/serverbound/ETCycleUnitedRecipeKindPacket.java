package me.myogoo.extendedterminal.network.serverbound;

import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.myotus.api.network.IMyotusPacket;
import me.myogoo.myotus.api.network.MyoPacketContext;
import net.minecraft.network.FriendlyByteBuf;

public class ETCycleUnitedRecipeKindPacket implements IMyotusPacket {
    public ETCycleUnitedRecipeKindPacket() {
    }

    public ETCycleUnitedRecipeKindPacket(FriendlyByteBuf stream) {
    }

    @Override
    public void write(FriendlyByteBuf stream) {
    }

    public static void handle(ETCycleUnitedRecipeKindPacket packet, MyoPacketContext context) {
        var player = context.sender();
        if (player == null) {
            return;
        }
        if (player.containerMenu instanceof UnitedTerminalMenu menu) {
            menu.selectNextRecipeKind();
        }
    }
}
