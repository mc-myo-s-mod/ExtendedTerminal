package me.myogoo.extendedterminal.menu;

import appeng.menu.SlotSemantic;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.ETModLoad;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.asm.enumextension.ExtensionInfo;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;


public enum ETMenuType implements IExtensibleEnum {
    ET_TERMINAL(3,-1, ETSlotSemantics.BASIC_CRAFTING_GRID, ETSlotSemantics.BASIC_CRAFTING_RESULT),

    @ETModLoad.ExtendedCrafting
    BASIC_TERMINAL(3,1, ETSlotSemantics.BASIC_CRAFTING_GRID, ETSlotSemantics.BASIC_CRAFTING_RESULT),

    @ETModLoad.ExtendedCrafting
    ADVANCED_TERMINAL(5,2, ETSlotSemantics.ADVANCED_CRAFTING_GRID, ETSlotSemantics.ADVANCED_CRAFTING_RESULT),

    @ETModLoad.ExtendedCrafting
    ELITE_TERMINAL(7,3, ETSlotSemantics.ELITE_CRAFTING_GRID, ETSlotSemantics.ELITE_CRAFTING_RESULT),

    @ETModLoad.ExtendedCrafting
    ULTIMATE_TERMINAL(9,4, ETSlotSemantics.ULTIMATE_CRAFTING_GRID, ETSlotSemantics.ULTIMATE_CRAFTING_RESULT),

    @ETModLoad.ExtendedCrafting
    EXTENDED_CRAFTING_UNIVERSAL_TERMINAL(9,0, ETSlotSemantics.EXTENDED_CRAFTING_UNIVERSAL_GRID, ETSlotSemantics.EXTENDED_CRAFTING_UNIVERSAL_RESULT),

    @ETModLoad.Avaritia
    AVARITIA_TERMINAL(9, -1, ETSlotSemantics.AVARITIA_CRAFTING_GRID, ETSlotSemantics.AVARITIA_CRAFTING_RESULT),;

    private final SlotSemantic slotSemantic_GRID;
    private final SlotSemantic slotSemantic_RESULT;
    private final int sideLength;
    private final int tier;
    ETMenuType(int sideLength, int tier, SlotSemantic slotSemantic_GRID, SlotSemantic slotSemantic_RESULT) {
        this.slotSemantic_GRID = slotSemantic_GRID;
        this.slotSemantic_RESULT =  slotSemantic_RESULT;
        this.sideLength = sideLength;
        this.tier = tier;
    }

    public int getGridSize() {
        return sideLength * sideLength;
    }

    public int getGridSideLength() {
        return sideLength;
    }

    public ResourceLocation getCraftingInventory() {
        return ExtendedTerminal.makeId(this.getIdAsString() + "_inventory");
    }

    public SlotSemantic getSlotSemanticGrid() {
        return slotSemantic_GRID;
    }

    public SlotSemantic getSlotSemanticResult() {
        return slotSemantic_RESULT;
    }

    public int getTier() {
        return tier;
    }

    public ResourceLocation getId() {
        return ExtendedTerminal.makeId(this.getIdAsString());
    }

    public String getIdAsString() {
        return this.name().toLowerCase();
    }

    public String getEnglishName() {
        return this.name().toLowerCase().replace("_", " ");
    }

    public boolean canLoad() {
        try {
            Field field = ETMenuType.class.getField(this.name());
            if(field.getDeclaredAnnotations().length == 0) {
                return true; // No annotations means it can be loaded by default
            }
            return Arrays.stream(field.getDeclaredAnnotations())
                    .map(Annotation::annotationType)
                    .allMatch(ModLoadHelper::get);
        } catch (NoSuchFieldException e) {
            ExtendedTerminal.LOGGER.error("Menu type {} is not loaded due to missing field in ETMenuType", this.name());
        }
        return false;
    }

    public static ExtensionInfo getExtensionInfo() {
        return ExtensionInfo.nonExtended(ETMenuType.class);
    }
}
