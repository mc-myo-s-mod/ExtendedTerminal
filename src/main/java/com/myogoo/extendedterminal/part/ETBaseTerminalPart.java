package com.myogoo.extendedterminal.part;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.reporting.AbstractTerminalPart;
import appeng.parts.reporting.CraftingTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import com.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class ETBaseTerminalPart extends AbstractTerminalPart {
    public static final ResourceLocation MODEL_OFF = CraftingTerminalPart.MODEL_OFF;
    public static final ResourceLocation MODEL_ON = CraftingTerminalPart.MODEL_ON;

    private final AppEngInternalInventory craftingGrid;
    private final ETMenuType menuType;

    public ETBaseTerminalPart(IPartItem<?> partItem, ETMenuType menuType) {
        super(partItem);
        this.menuType = menuType;
        this.craftingGrid = new AppEngInternalInventory(this, menuType.getGridSize());
    }

    @Override
    public void clearContent() {
        super.clearContent();
        craftingGrid.clear();
    }


    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if(id.equals(menuType.getCraftingInventory())){
            return craftingGrid;
        }
        return super.getSubInventory(id);
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        this.craftingGrid.readFromNBT(data, "craftingGrid", registries);
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        this.craftingGrid.writeToNBT(data, "craftingGrid", registries);
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
         super.addAdditionalDrops(drops, wrenched);
        for(var is : this.craftingGrid) {
            if(!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

}
