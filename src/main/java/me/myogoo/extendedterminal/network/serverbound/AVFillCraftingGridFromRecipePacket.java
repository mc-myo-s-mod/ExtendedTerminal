package me.myogoo.extendedterminal.network.serverbound;

import appeng.core.network.ServerboundPacket;
import me.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.blakebr0.extendedcrafting.ExtendedCrafting.LOGGER;

public class AVFillCraftingGridFromRecipePacket implements ServerboundPacket {
    private static final int NOT_SET_RECIPE_SIZE = -1; //name refactor
    public static final StreamCodec<RegistryFriendlyByteBuf, AVFillCraftingGridFromRecipePacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    AVFillCraftingGridFromRecipePacket::write,
                    AVFillCraftingGridFromRecipePacket::decode);

    public static final Type<AVFillCraftingGridFromRecipePacket> TYPE = new CustomPacketPayload
            .Type<>(ExtendedTerminal.makeId("av_fill_crafting_grid_from_recipe"));

    private final @Nullable ResourceLocation recipeId;
    private final List<ItemStack> ingredientTemplates;
    private final boolean craftMissing;
    private final int recipeWidth;
    private final int recipeHeight;

    public CustomPacketPayload.Type<AVFillCraftingGridFromRecipePacket> type() {
        return TYPE;
    }

    public AVFillCraftingGridFromRecipePacket(
            @Nullable ResourceLocation recipeId,
            List<ItemStack> ingredientTemplates,
            boolean craftMissing
    ) {
        this.recipeId = recipeId;
        this.ingredientTemplates = NonNullList.copyOf(ingredientTemplates.stream().map(ItemStack::copy).toList());
        this.craftMissing = craftMissing;
        this.recipeHeight = NOT_SET_RECIPE_SIZE;
        this.recipeWidth = NOT_SET_RECIPE_SIZE;
    }

    public AVFillCraftingGridFromRecipePacket(
            List<ItemStack> ingredientTemplates,
            boolean craftMissing,
            int recipeWidth,
            int recipeHeight
    ) {
        this.recipeId = null;
        this.ingredientTemplates = NonNullList.copyOf(ingredientTemplates.stream().map(ItemStack::copy).toList());
        this.craftMissing = craftMissing;
        this.recipeWidth = recipeWidth;
        this.recipeHeight = recipeHeight;
    }

    public void write(RegistryFriendlyByteBuf stream) {
        stream.writeBoolean(recipeId != null);
        if (recipeId != null) {
            stream.writeResourceLocation(recipeId);
        }

        stream.writeInt(ingredientTemplates.size());
        for (var ingredientTemplate : ingredientTemplates) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(stream, ingredientTemplate);
        }
        stream.writeBoolean(craftMissing);
        stream.writeInt(recipeWidth);
        stream.writeInt(recipeHeight);
    }

    public static AVFillCraftingGridFromRecipePacket decode(RegistryFriendlyByteBuf stream) {
        ResourceLocation recipeId = null;
        if (stream.readBoolean()) {
            recipeId = stream.readResourceLocation();
        }

        var ingredientTemplates = NonNullList.withSize(stream.readInt(), ItemStack.EMPTY);
        for (int i = 0; i < ingredientTemplates.size(); i++) {
            ingredientTemplates.set(i, ItemStack.OPTIONAL_STREAM_CODEC.decode(stream));
        }
        var craftMissing = stream.readBoolean();
        int recipeWidth = stream.readInt();
        int recipeHeight = stream.readInt();
        if (recipeId != null) {
            return new AVFillCraftingGridFromRecipePacket(recipeId, ingredientTemplates, craftMissing);
        } else {
            if (recipeWidth <= 0 || recipeHeight <= 0) { //hmm.. ai generated code
                LOGGER.warn("Received ETFillCraftingGridFromRecipePacket with invalid recipe size: {}x{}",
                        recipeWidth, recipeHeight);
                return new AVFillCraftingGridFromRecipePacket(recipeId, ingredientTemplates, craftMissing);
            }
            return new AVFillCraftingGridFromRecipePacket(ingredientTemplates, craftMissing, recipeWidth, recipeHeight);
        }
    }


    @Override
    public void handleOnServer(ServerPlayer player) {

    }
}
