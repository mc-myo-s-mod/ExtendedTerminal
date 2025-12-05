package me.myogoo.extendedterminal.menu.avaritiaNeo;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.core.sync.network.NetworkHandler;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.CraftingMatrixSlot;
import com.google.common.base.Preconditions;
import me.myogoo.extendedterminal.config.avaritiaNeo.AvaritiaNeoConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.avaritiaNeo.slot.AvaritiaNeoCraftingTerminalSlot;
import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

public class NeoExtremeTerminalMenu extends ETTerminalBaseMenu<RecipeExtremeCrafting> {
    public static final MenuType<NeoExtremeTerminalMenu> TYPE = MenuTypeBuilder
            .create(NeoExtremeTerminalMenu::new, ITerminalHost.class)
            .build(ETMenuType.EXTREME_TERMINAL.getIdAsString());

    private final AvaritiaNeoCraftingTerminalSlot outputSlot;
    private final ISegmentedInventory craftingInventoryHost;
    private final CraftingMatrixSlot[] craftingSlots;
    private final CraftingContainer recipeTestContainer;


    public NeoExtremeTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.NEO_EXTREME_TERMINAL, AvaritiaNeoConfig.INSTANCE.getExtremeConfig());
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];
        this.recipeTestContainer = new TransientCraftingContainer(this,this.menuType.getGridSideLength() , this.menuType.getGridSideLength());
        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(this.menuType.getCraftingInventory());
        for(int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this,craftingGridInv,i), this.menuType.getSlotSemanticGrid());
        }

        this.addSlot(this.outputSlot = new AvaritiaNeoCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                        this.powerSource, host.getInventory(), craftingGridInv, craftingGridInv, this),
                this.menuType.getSlotSemanticResult());

        updateCurrentRecipeAndOutput(true);

    }

    @Override
    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        CraftingMatrixSlot slot = craftingSlots[0];
        var p = new appeng.core.sync.packets.InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        NetworkHandler.instance().sendToServer(p);
    }

    @Override
    public SlotSemantic getCraftingGridSlotSemantic() {
        return ETSlotSemantics.EXTREME_CRAFTING_GRID;
    }

    @Override
    public SlotSemantic getOutputSlotSemantic() {
        return ETSlotSemantics.EXTREME_CRAFTING_RESULT;
    }

    @Override
    public int getCraftingGridSize() {
        return 81;
    }

    @Override
    public int getCraftingGridWidth() {
        return 9;
    }

    @Override
    public int getCraftingGridHeight() {
        return 9;
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        if(checkCraftingOnlyActive()) {
            return;
        }

        boolean hasChanged = forceUpdate;
        for (int x = 0; x < this.craftingSlots.length; x++) {
            var stack = this.craftingSlots[x].getItem();
            if (!ItemStack.isSameItemSameTags(stack, recipeTestContainer.getItem(x))) {
                hasChanged = true;
                recipeTestContainer.setItem(x, stack.copy());
            }
        }

        if (!hasChanged) {
            return;
        }

        var level = this.getPlayerInventory().player.level();
        this.currentRecipe = level.getRecipeManager().getRecipeFor(AvaritiaRecipes.EXTREME_CRAFTING.get(), recipeTestContainer, level)
                .orElse(null);

        if(this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentRecipe.assemble(recipeTestContainer,level.registryAccess()));
        }
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return this.craftingInventoryHost.getSubInventory(menuType.getCraftingInventory());
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        var s = this.getSlot(slot);
        if(s instanceof AvaritiaNeoCraftingTerminalSlot craftingSlot) {
            switch (action) {
                case CRAFT_SHIFT:
                case CRAFT_ALL:
                case CRAFT_ITEM:
                case CRAFT_STACK:
                    craftingSlot.doClick(action, player);
                default:
                    return;
            }
        }
        super.doAction(player, action, slot, id);
    }
}
