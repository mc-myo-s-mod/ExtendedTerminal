package me.myogoo.extendedterminal.part;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.parts.reporting.AbstractTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public abstract class ETTerminalBasePart extends AbstractTerminalPart {
    private final AppEngInternalInventory craftingGrid;
    private final ETMenuType menuType;

    public ETTerminalBasePart(IPartItem<?> partItem, ETMenuType menuType) {
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
    public InternalInventory getSubInventory(Identifier id) {
        if(id.equals(menuType.getCraftingInventory())){
            return craftingGrid;
        }
        return super.getSubInventory(id);
    }

    @Override
    public void readFromNBT(ValueInput input) {
        super.readFromNBT(input);
        this.craftingGrid.readFromNBT(input, "craftingGrid");
    }

    @Override
    public void writeToNBT(ValueOutput output) {
        super.writeToNBT(output);
        this.craftingGrid.writeToNBT(output, "craftingGrid");
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
