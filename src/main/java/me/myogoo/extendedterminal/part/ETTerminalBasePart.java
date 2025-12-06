package me.myogoo.extendedterminal.part;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.parts.reporting.AbstractTerminalPart;
import appeng.parts.reporting.CraftingTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class ETTerminalBasePart extends AbstractTerminalPart {
    public static final ResourceLocation MODEL_OFF = CraftingTerminalPart.MODEL_OFF;
    public static final ResourceLocation MODEL_ON = CraftingTerminalPart.MODEL_ON;

    private final AppEngInternalInventory craftingGrid;
    private final ETMenuType menuType;

    public ETTerminalBasePart(IPartItem<?> partItem, ETMenuType menuType, IETTerminalConfig config) {
        super(partItem);
        this.getMainNode().setIdlePowerUsage(config.passiveDrainAE());
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
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.craftingGrid.readFromNBT(data, "craftingGrid");
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        this.craftingGrid.writeToNBT(data, "craftingGrid");
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
