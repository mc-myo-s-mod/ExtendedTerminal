package me.myogoo.extendedterminal.part.extendedterminal;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.MachineUpgradesChanged;
import appeng.api.upgrades.UpgradeInventories;
import appeng.util.inv.AppEngInternalInventory;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.host.IETTerminalHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import me.myogoo.myotus.api.experience.ExperienceMath;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ETTerminalPart extends ETTerminalBasePart implements IETTerminalHost {
    public static final Identifier SmithingInventory = ExtendedTerminal.makeId("smithing_crafting_inventory");
    public static final Identifier StoneCutterInventory = ExtendedTerminal.makeId("stonecutter_crafting_inventory");
    public static final Identifier AnvilInventory = ExtendedTerminal.makeId("anvil_inventory");
    public static final Identifier UpgradeInventory = ExtendedTerminal.makeId("upgrade_inventory");

    private final AppEngInternalInventory smithingGrid = new AppEngInternalInventory(this, 3);
    private final AppEngInternalInventory stoneCutterGrid = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory anvilInv = new AppEngInternalInventory(this, 2);

    private ETTerminalMode mode;
    @Nullable
    private Identifier stonecuttingRecipeId;
    @Nullable
    private ExperienceMath.ExperienceSource rememberedAnvilExperienceSource;
    private boolean isLoading = false;

    public ETTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ET_TERMINAL);
        this.mode = ETTerminalMode.loadableValues().getFirst();
    }

    @Override
    public void clearContent() {
        super.clearContent();
        smithingGrid.clear();
        stoneCutterGrid.clear();
        anvilInv.clear();
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return ETTerminalMenu.TYPE;
    }

    @Override
    public InternalInventory getSubInventory(Identifier id) {
        if (id.equals(SmithingInventory)) {
            return smithingGrid;
        } else if (id.equals(StoneCutterInventory)) {
            return stoneCutterGrid;
        } else if (id.equals(AnvilInventory)) {
            return anvilInv;
        } else if (id.equals(UpgradeInventory)) {
        }
        return super.getSubInventory(id);
    }

    @Override
    public void writeToNBT(ValueOutput output) {
        super.writeToNBT(output);
        output.putString("mode", this.mode.name());
        if (this.stonecuttingRecipeId != null) {
            output.putString("stonecuttingRecipeId", this.stonecuttingRecipeId.toString());
        }
        if (this.rememberedAnvilExperienceSource != null) {
            output.putString("anvilExperienceSource", this.rememberedAnvilExperienceSource.name());
        }

        smithingGrid.writeToNBT(output, "smithingGrid");
        stoneCutterGrid.writeToNBT(output, "stoneCutterGrid");
        anvilInv.writeToNBT(output, "anvilInv");

    }

    @Override
    public void saveChanges() {
        if(!isLoading) {
            super.saveChanges();
        }
    }

    @Override
    public void readFromNBT(ValueInput input) {
        isLoading = false;
        try {
            super.readFromNBT(input);
            try {
                this.mode = ETTerminalMode.valueOf(input.getStringOr("mode", ""));
            } catch (IllegalArgumentException ignored) {
                this.mode = ETTerminalMode.CRAFTING;
            }

            var stonecuttingRecipeId = input.getStringOr("stonecuttingRecipeId", null);
            if (stonecuttingRecipeId != null) {
                this.stonecuttingRecipeId = Identifier.parse(stonecuttingRecipeId);
            } else {
                this.stonecuttingRecipeId = null;
            }
            this.rememberedAnvilExperienceSource = readAnvilExperienceSource(input);
            smithingGrid.readFromNBT(input, "smithingGrid");
            stoneCutterGrid.readFromNBT(input, "stoneCutterGrid");
            anvilInv.readFromNBT(input, "anvilInv");

        } finally {
            isLoading = false;
        }
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

        for (var is : this.anvilInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    public ETTerminalMode getMode() {
        return mode;
    }

    @Override
    public void setMode(ETTerminalMode mode) {
        this.mode = mode;
        this.saveChanges();
    }

    @Override
    public @Nullable Identifier getStoneCutterRecipeId() {
        return stonecuttingRecipeId;
    }

    @Override
    public void setStoneCutterRecipeId(@Nullable Identifier stonecuttingRecipeId) {
        this.stonecuttingRecipeId = stonecuttingRecipeId;
        this.saveChanges();
    }

    @Override
    public @Nullable ExperienceMath.ExperienceSource getRememberedAnvilExperienceSource() {
        return this.rememberedAnvilExperienceSource;
    }

    @Override
    public void setRememberedAnvilExperienceSource(@Nullable ExperienceMath.ExperienceSource source) {
        this.rememberedAnvilExperienceSource = source;
        this.saveChanges();
    }

    private static @Nullable ExperienceMath.ExperienceSource readAnvilExperienceSource(ValueInput input) {
        var source = input.getStringOr("anvilExperienceSource", null);
        if (source == null) {
            return null;
        }
        try {
            return ExperienceMath.ExperienceSource.valueOf(source);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
