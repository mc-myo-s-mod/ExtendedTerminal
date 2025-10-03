package me.myogoo.extendedterminal.part.extendedterminal;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.MachineUpgradesChanged;
import appeng.api.upgrades.UpgradeInventories;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.util.inv.AppEngInternalInventory;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static appeng.parts.reporting.CraftingTerminalPart.*;

public class ETTerminalPart extends ETTerminalBasePart {
    @PartModels
    public static final ResourceLocation MODEL_OFF = ExtendedTerminal.makeId("part/extendedterminal/extended_terminal_off");
    @PartModels
    public static final ResourceLocation MODEL_ON = ExtendedTerminal.makeId("part/extendedterminal/extended_terminal_on");


    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);


    public static final ResourceLocation SmithingInventory = ExtendedTerminal.makeId("smithing_crafting_inventory");
    public static final ResourceLocation StoneCutterInventory = ExtendedTerminal.makeId("stonecutter_crafting_inventory");
    public static final ResourceLocation UpgradeInventory = ExtendedTerminal.makeId("upgrade_inventory");

    private final AppEngInternalInventory smithingGrid = new AppEngInternalInventory(this, 3);
    private final AppEngInternalInventory stoneCutterGrid = new AppEngInternalInventory(this, 1);

    public ETTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ET_TERMINAL);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        smithingGrid.clear();
        stoneCutterGrid.clear();
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return ETTerminalMenu.TYPE;
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_ON,MODELS_OFF,MODELS_HAS_CHANNEL);
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if(id.equals(SmithingInventory)){
            return smithingGrid;
        } else if(id.equals(StoneCutterInventory)){
            return stoneCutterGrid;
        } else if(id.equals(UpgradeInventory)){
        }
        return super.getSubInventory(id);
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        smithingGrid.writeToNBT(data, "smithingGrid", registries);
        stoneCutterGrid.writeToNBT(data, "stoneCutterGrid", registries);
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        smithingGrid.readFromNBT(data, "smithingGrid", registries);
        stoneCutterGrid.readFromNBT(data, "stoneCutterGrid", registries);
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        for (var is : this.smithingGrid) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
        for (var is : this.stoneCutterGrid) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Override
    protected IPartModel selectModel(IPartModel offModels, IPartModel onModels, IPartModel hasChannelModels) {
        return super.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }
}
