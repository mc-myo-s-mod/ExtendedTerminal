package me.myogoo.extendedterminal.blockentity;

import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkBlockEntity;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETBlockEntities;
import me.myogoo.extendedterminal.init.ETItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public class MaterialConverterBlockEntity extends AENetworkBlockEntity {
    private static final String[] MATERIALS = {
            "compat_processor",
            "printed_compat_processor",
            "compat_press",
            "charged_ender_pearl"
    };

    public MaterialConverterBlockEntity(BlockPos pos, BlockState state) {
        super(ETBlockEntities.MATERIAL_CONVERTER.get(), pos, state);
        this.getMainNode()
                .setVisualRepresentation(ETItems.MATERIAL_CONVERTER)
                .setIdlePowerUsage(1.0)
                .setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.GLASS;
    }

    public Component convertMaterials(Player player) {
        if (!this.getMainNode().isOnline()) {
            return Component.translatable(ETTranslationKey.MESSAGE.MESSAGE_MATERIAL_CONVERTER_OFFLINE.key());
        }

        var grid = this.getMainNode().getGrid();
        if (grid == null) {
            return Component.translatable(ETTranslationKey.MESSAGE.MESSAGE_MATERIAL_CONVERTER_OFFLINE.key());
        }

        var storage = grid.getStorageService().getInventory();
        var source = IActionSource.ofPlayer(player);
        long converted = 0;
        boolean missingTarget = false;

        for (var material : MATERIALS) {
            var target = getItem(new ResourceLocation("myotus", material));
            if (target == Items.AIR) {
                missingTarget = true;
                continue;
            }

            var oldItem = getItem(ExtendedTerminal.makeId(material));
            if (oldItem == Items.AIR) {
                continue;
            }

            converted += convert(storage, source,
                    AEItemKey.of(oldItem),
                    AEItemKey.of(target));
        }

        if (converted > 0) {
            return Component.translatable(ETTranslationKey.MESSAGE.MESSAGE_MATERIAL_CONVERTER_CONVERTED.key(), converted);
        }

        return missingTarget
                ? Component.translatable(ETTranslationKey.MESSAGE.MESSAGE_MATERIAL_CONVERTER_MISSING_TARGETS.key())
                : Component.translatable(ETTranslationKey.MESSAGE.MESSAGE_MATERIAL_CONVERTER_EMPTY.key());
    }

    private static long convert(MEStorage storage, IActionSource source, AEItemKey oldKey, AEItemKey newKey) {
        var available = storage.extract(oldKey, Long.MAX_VALUE, Actionable.SIMULATE, source);
        if (available <= 0) {
            return 0;
        }

        var insertable = storage.insert(newKey, available, Actionable.SIMULATE, source);
        var amount = Math.min(available, insertable);
        if (amount <= 0) {
            return 0;
        }

        var extracted = storage.extract(oldKey, amount, Actionable.MODULATE, source);
        var inserted = storage.insert(newKey, extracted, Actionable.MODULATE, source);
        if (inserted < extracted) {
            storage.insert(oldKey, extracted - inserted, Actionable.MODULATE, source);
        }
        return inserted;
    }

    private static Item getItem(ResourceLocation id) {
        return BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
    }
}
