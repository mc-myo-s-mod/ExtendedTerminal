package me.myogoo.extendedterminal.gametest;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.upgrades.IUpgradeableItem;
import appeng.core.definitions.AEItems;
import appeng.helpers.InventoryAction;
import appeng.menu.ISubMenu;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import com.mojang.authlib.GameProfile;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.me.host.ExtendedCraftingWTHost;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.me.host.UnitedWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.ExtendedCraftingWTMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.experience.ExperienceMath.ExperienceSource;
import me.myogoo.myotus.api.wt.AddTerminalEvent;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@GameTestHolder(ExtendedTerminal.MODID)
public final class WirelessCraftingGameTests {
    private WirelessCraftingGameTests() {
    }

    @GameTestGenerator
    public static List<TestFunction> generateTests() {
        var tests = new ArrayList<TestFunction>();
        if (ModList.get().isLoaded("ae2wtlib")) {
            tests.add(new TestFunction("extendedterminal", "wirelesscraftinggametests.unitedsettingsroundtrip",
                    "extendedterminal:empty", 20, 0, true, WirelessCraftingGameTests::unitedSettingsRoundTrip));
        }
        if (!ModList.get().isLoaded("extendedcrafting")) {
            return tests;
        }
        for (var type : ETMenuType.values()) {
            tests.add(new TestFunction("extendedterminal", "wirelesscraftinggametests.gridstoretake_" + type.getIdAsString(),
                    "extendedterminal:empty", 20, 0, true, helper -> gridStoreTake(helper, type)));
        }
        if (ModList.get().isLoaded("ae2wtlib")
                && ETMenuType.EPIC_TERMINAL.canLoad() && ETMenuType.LEGENDARY_TERMINAL.canLoad()) {
            tests.add(new TestFunction("extendedterminal", "wirelesscraftinggametests.inventoryroundtrip",
                    "extendedterminal:empty", 20, 0, true, WirelessCraftingGameTests::inventoryRoundTrip));
        }
        if (ModList.get().isLoaded("ae2wtlib") && !ModList.get().isLoaded("apotheosis")
                && !ModList.get().isLoaded("apothic_enchanting")) {
            tests.add(new TestFunction("extendedterminal", "wirelesscraftinggametests.anvilexperiencecost",
                    "extendedterminal:empty", 20, 0, true, WirelessCraftingGameTests::anvilExperienceCost));
        }
        return tests;
    }

    private static void unitedSettingsRoundTrip(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-united-config"));
        var item = WTItems.WIRELESS_UNITED_TERMINAL.asItem();
        var stack = new ItemStack(item);
        var definition = WUTHandler.wirelessTerminals.get(ETMenuType.UNITED_TERMINAL.getWTIdAsString());
        helper.assertTrue(definition != null && definition.item() == item
                        && definition.menuType() == UnitedWTMenu.TYPE && item.getMenuType(stack) == UnitedWTMenu.TYPE,
                "United wireless registration must retain its own item and menu");
        var host = (UnitedWTHost) definition.wTMenuHostFactory().create(player, null, stack, (p, menu) -> { });
        var menu = new UnitedWTMenu(1, player.getInventory(), host);
        helper.assertTrue(menu.getMagnetMode() == MagnetMode.NO_CARD,
                "United terminal without a Magnet card must report no card");
        var upgrades = ((IUpgradeableItem) item).getUpgrades(stack);
        helper.assertTrue(upgrades.addItems(new ItemStack(AE2wtlib.MAGNET_CARD)).isEmpty()
                        && upgrades.isInstalled(AE2wtlib.MAGNET_CARD),
                "United terminal must accept the real AE2WTLib Magnet card through its upgrade inventory");
        var grid = menu.getCraftingMatrix();
        grid.setItemDirect(0, new ItemStack(Items.DIAMOND, 2));
        grid.setItemDirect(grid.size() - 1, new ItemStack(Items.EMERALD, 3));

        var modes = List.of(MagnetMode.OFF, MagnetMode.PICKUP_INVENTORY, MagnetMode.PICKUP_ME, MagnetMode.OFF);
        for (int i = 0; i < modes.size(); i++) {
            boolean enabled = i % 2 == 1;
            menu.setRememberRecipeType(enabled);
            menu.setPickBlock(enabled);
            menu.setCraftIfMissing(enabled);
            menu.setRestock(enabled);
            menu.setMagnetMode(modes.get(i));

            stack = ItemStack.of(stack.save(new CompoundTag()));
            host = (UnitedWTHost) definition.wTMenuHostFactory().create(player, null, stack, (p, subMenu) -> { });
            menu = new UnitedWTMenu(i + 2, player.getInventory(), host);
            helper.assertTrue(host.getMainMenuIcon().is(item),
                    "United settings back button must retain the United terminal icon");
            helper.assertTrue(menu.rememberRecipeType() == enabled && menu.isPickBlock() == enabled
                            && menu.isCraftIfMissing() == enabled && menu.isRestock() == enabled
                            && menu.getMagnetMode() == modes.get(i),
                    "United settings and Magnet mode must survive reopening: " + modes.get(i));
            helper.assertTrue(((IUpgradeableItem) item).getUpgrades(stack).isInstalled(AE2wtlib.MAGNET_CARD),
                    "Magnet card must survive the settings roundtrip");
            var restoredGrid = menu.getCraftingMatrix();
            helper.assertTrue(menu.getType() == UnitedWTMenu.TYPE && menu.getWTHost() == host
                            && restoredGrid == host.getSubInventory(ETMenuType.UNITED_TERMINAL.getCraftingInventory())
                            && ItemStack.matches(restoredGrid.getStackInSlot(0), grid.getStackInSlot(0))
                            && ItemStack.matches(restoredGrid.getStackInSlot(grid.size() - 1),
                                    grid.getStackInSlot(grid.size() - 1)),
                    "reopening United settings must preserve the United menu, host and crafting inventory");
        }
        helper.succeed();
    }

    private static void anvilExperienceCost(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-anvil-test"));
        player.getAbilities().instabuild = false;
        var host = new ETWTHost(player, null, new ItemStack(WTItems.WIRELESS_ET_TERMINAL), (p, menu) -> { });
        host.setMode(ETTerminalMode.ANVIL);
        var input = new ItemStack(Items.DIAMOND);
        input.setRepairCost(4);
        host.getSubInventory(ETTerminalPart.ANVIL_INVENTORY).setItemDirect(0, input);
        var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 1, player.getInventory(), host);
        menu.setAnvilItemName("XP regression");
        helper.assertTrue(menu.getAnvilCost() == 5, "rename fixture must cost five levels");
        helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player),
                "zero-level players without stored XP must not get a free anvil operation");

        player.giveExperiencePoints(7);
        helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                        && player.experienceLevel == 1,
                "insufficient XP must not underpay the displayed anvil cost or debit the player");

        player.giveExperiencePoints(48);
        helper.assertTrue(player.experienceLevel == 5 && menu.canPayAnvilCost(player)
                        && menu.consumeAnvilExperience(player) && player.experienceLevel == 0,
                "exactly five levels must pay the anvil cost");

        player.giveExperiencePoints(173); // Level 10 plus 13 of the next 27 XP.
        helper.assertTrue(menu.consumeAnvilExperience(player) && player.experienceLevel == 5
                        && player.totalExperience == 63,
                "vanilla level removal must account for the progress fraction at the target level");
        helper.succeed();
    }

    @GameTestGenerator
    public static List<TestFunction> generateFluidExperienceTests() {
        if (!ModList.get().isLoaded("ae2wtlib") || ModList.get().isLoaded("apotheosis")
                || ModList.get().isLoaded("apothic_enchanting")) {
            return List.of();
        }
        var tests = new ArrayList<TestFunction>();
        var fluidIds = new ArrayList<>(List.of(new ResourceLocation("mob_grinding_utils", "fluid_xp"),
                new ResourceLocation("sophisticatedcore", "xp_still"), new ResourceLocation("minecraft", "water")));
        if (ModList.get().isLoaded("create_enchantment_industry")) {
            fluidIds.add(new ResourceLocation("create_enchantment_industry", "experience"));
        }
        for (var id : fluidIds) {
            if (ForgeRegistries.FLUIDS.containsKey(id)) {
                tests.add(new TestFunction("extendedterminal", "wirelesscraftinggametests.anvilfluid_" + id.getNamespace(),
                        "extendedterminal:empty", 20, 0, true, helper -> anvilFluidExperience(helper, id)));
            }
        }
        tests.add(new TestFunction("extendedterminal", "wirelesscraftinggametests.anvilavailabilitycadence",
                "extendedterminal:empty", 40, 0, true, WirelessCraftingGameTests::anvilAvailabilityCadence));
        return tests;
    }

    private static void anvilAvailabilityCadence(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-xp-refresh"));
        var storage = new TestStorage(10_000);
        var host = new ETWTHost(player, null, new ItemStack(WTItems.WIRELESS_ET_TERMINAL), (p, menu) -> { }) {
            @Override
            public MEStorage getInventory() {
                return storage;
            }

            @Override
            public double extractAEPower(double amount, Actionable mode, PowerMultiplier multiplier) {
                return amount;
            }
        };
        var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 1, player.getInventory(), host);
        helper.assertTrue(storage.reports == 2, "constructor queries each XP source once");
        storage.reports = 0;
        menu.getSelectedAnvilExperienceSource();
        menu.getSelectedAnvilExperienceSourceLabelKey();
        helper.assertTrue(storage.reports == 0, "display getters reuse the availability snapshot");
        menu.broadcastChanges();
        menu.broadcastChanges();
        helper.assertTrue(storage.reports == 2, "same-tick broadcasts only perform AE2's normal storage reports");
        long started = helper.getLevel().getGameTime();
        helper.runAfterDelay(9, () -> {
            helper.assertTrue(helper.getLevel().getGameTime() == started + 9, "advance nine game ticks");
            storage.reports = 0;
            menu.broadcastChanges();
            helper.assertTrue(storage.reports == 1, "no XP refresh before ten ticks");
        });
        helper.runAfterDelay(10, () -> {
            storage.reports = 0;
            menu.broadcastChanges();
            helper.assertTrue(storage.reports == 3, "ten ticks refresh both XP sources plus AE2's normal report");
            menu.broadcastChanges();
            helper.assertTrue(storage.reports == 4, "a second broadcast does not repeat the XP refresh");
        });
        helper.runAfterDelay(19, () -> {
            storage.reports = 0;
            menu.setMode(ETTerminalMode.ANVIL);
            helper.assertTrue(storage.reports == 2, "entering the anvil refreshes immediately");
            storage.reports = 0;
            menu.cycleAnvilExperienceSourcePriority();
            helper.assertTrue(storage.reports == 2, "cycling refreshes once without querying again to select or remember");
        });
        helper.runAfterDelay(20, () -> {
            storage.reports = 0;
            menu.broadcastChanges();
            helper.assertTrue(storage.reports == 1, "forced refresh postpones the periodic deadline");
        });
        helper.runAfterDelay(29, () -> {
            storage.reports = 0;
            menu.broadcastChanges();
            helper.assertTrue(storage.reports == 3, "periodic refresh resumes ten ticks after the forced refresh");
            helper.succeed();
        });
    }

    private static void anvilFluidExperience(GameTestHelper helper, ResourceLocation fluidId) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-fluid-test"));
        player.getAbilities().instabuild = false;
        var storage = new TestStorage(10_000);
        long unitsPerExperience = fluidId.equals(new ResourceLocation("create_enchantment_industry", "experience"))
                ? 1L : 20L;
        long requiredFluidUnits = 55L * unitsPerExperience;
        long enoughFluidUnits = requiredFluidUnits + unitsPerExperience;
        var fluid = AEFluidKey.of(ForgeRegistries.FLUIDS.getValue(fluidId));
        storage.amounts.put(fluid, enoughFluidUnits);
        var host = new ETWTHost(player, null, new ItemStack(WTItems.WIRELESS_ET_TERMINAL), (p, menu) -> { }) {
            private boolean powered = true;

            @Override
            public MEStorage getInventory() {
                return storage;
            }

            @Override
            public double extractAEPower(double amount, Actionable mode, PowerMultiplier multiplier) {
                return powered ? amount : 0;
            }
        };
        host.setMode(ETTerminalMode.ANVIL);
        var input = new ItemStack(Items.DIAMOND);
        input.setRepairCost(4);
        host.getSubInventory(ETTerminalPart.ANVIL_INVENTORY).setItemDirect(0, input);
        var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 1, player.getInventory(), host);
        menu.setAnvilItemName("Fluid XP regression");
        helper.assertTrue(menu.getAnvilCost() == 5, "fluid rename fixture must cost five levels");
        menu.cycleAnvilExperienceSourcePriority();
        if (fluidId.equals(new ResourceLocation("minecraft", "water"))) {
            helper.assertTrue(menu.getSelectedAnvilExperienceSource() == ExperienceSource.PLAYER
                            && !menu.getAnvilExperienceSourcePriority().contains(ExperienceSource.FLUID_XP)
                            && !menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                            && storage.amounts.get(fluid) == 1120L,
                    "water must not be offered or consumed as XP");
            helper.succeed();
            return;
        }
        helper.assertTrue(menu.getSelectedAnvilExperienceSource() == ExperienceSource.FLUID_XP
                        && menu.getAnvilExperienceSourcePriority().contains(ExperienceSource.FLUID_XP),
                fluidId + " must be offered and selectable as fluid XP");
        storage.reports = 0;
        storage.largestSimulatedExtraction = 0;
        menu.cycleAnvilExperienceSourcePriority();
        helper.assertTrue(storage.reports == 2 && storage.largestSimulatedExtraction == unitsPerExperience,
                "cycling checks only one XP per source, without repeated storage snapshots");
        menu.cycleAnvilExperienceSourcePriority();
        var actionSource = IActionSource.ofPlayer(player, host);
        for (long units : new long[] {0, unitsPerExperience - 1, unitsPerExperience, enoughFluidUnits}) {
            storage.amounts.put(fluid, units);
            helper.assertTrue((MyotusAPI.experience().extractable(host, storage, actionSource, ExperienceSource.FLUID_XP) > 0)
                            == MyotusAPI.experience().canExtract(host, storage, actionSource, 1, ExperienceSource.FLUID_XP)
                            && storage.amounts.get(fluid) == units,
                    "one-XP availability matches the old estimate without consuming fluid at XP boundaries");
        }
        helper.assertTrue(menu.canPayAnvilCost(player) && storage.amounts.get(fluid) == enoughFluidUnits
                        && menu.consumeAnvilExperience(player) && storage.amounts.get(fluid) == unitsPerExperience
                        && player.totalExperience == 0 && player.experienceLevel == 0,
                fluidId + " must pay 55 XP with exactly " + requiredFluidUnits + " mB and no player XP");

        storage.amounts.put(fluid, requiredFluidUnits - 1);
        helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                        && storage.amounts.get(fluid) == requiredFluidUnits - 1,
                "insufficient fluid XP must not be debited");

        storage.amounts.put(fluid, enoughFluidUnits);
        storage.allowExtraction = false;
        helper.assertTrue(menu.getSelectedAnvilExperienceSource() == ExperienceSource.FLUID_XP
                        && !menu.getAnvilExperienceSourcePriority().contains(ExperienceSource.FLUID_XP)
                        && !menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                        && storage.amounts.get(fluid) == enoughFluidUnits,
                "a stale displayed source must not bypass live extraction permissions");
        menu.cycleAnvilExperienceSourcePriority();
        helper.assertTrue(menu.getSelectedAnvilExperienceSource() == ExperienceSource.PLAYER,
                "cycling refreshes denied-source availability immediately");
        storage.allowExtraction = true;
        menu.cycleAnvilExperienceSourcePriority();
        host.powered = false;
        helper.assertTrue(menu.getSelectedAnvilExperienceSource() == ExperienceSource.FLUID_XP
                        && !menu.getAnvilExperienceSourcePriority().contains(ExperienceSource.FLUID_XP)
                        && !menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                        && storage.amounts.get(fluid) == enoughFluidUnits,
                "a stale displayed source must not bypass live AE power checks");
        host.powered = true;

        var otherId = new ResourceLocation("sophisticatedcore", "xp_still");
        if (fluidId.getNamespace().equals("mob_grinding_utils") && ForgeRegistries.FLUIDS.containsKey(otherId)) {
            var otherFluid = AEFluidKey.of(ForgeRegistries.FLUIDS.getValue(otherId));
            storage.amounts.put(fluid, 550L);
            storage.amounts.put(otherFluid, 550L);
            helper.assertTrue(menu.canPayAnvilCost(player) && menu.consumeAnvilExperience(player)
                            && storage.amounts.get(fluid) == 0L && storage.amounts.get(otherFluid) == 0L,
                    "supported XP fluids must combine to pay exactly 1100 mB");
        }
        helper.succeed();
    }

    private static void gridStoreTake(GameTestHelper helper, ETMenuType type) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-store-test"));
        var host = new TestHost(player, type, 100);
        var menu = new ExtendedTerminalBaseMenu(BasicTerminalMenu.TYPE, 1, player.getInventory(), host, type, TEST_CONFIG);
        menu.broadcastChanges();
        var grid = host.grid;

        grid.setItemDirect(0, new ItemStack(Items.DIAMOND, 2));
        grid.setItemDirect(grid.size() - 1, new ItemStack(Items.EMERALD, 3));
        menu.doAction(player, InventoryAction.MOVE_REGION,
                menu.getSlots(type.getSlotSemanticGrid()).get(0).index, 0);
        helper.assertTrue(host.storage.count(Items.DIAMOND) == 2 && host.storage.count(Items.EMERALD) == 3,
                "store items must move the whole custom crafting grid into ME storage");
        helper.assertTrue(countPlayer(player, Items.DIAMOND) == 0 && countPlayer(player, Items.EMERALD) == 0,
                "store items must not move custom grid items into the player inventory");
        helper.assertTrue(grid.getStackInSlot(0).isEmpty() && grid.getStackInSlot(grid.size() - 1).isEmpty(),
                "store items must clear grid slots inserted into ME storage");

        grid.setItemDirect(0, new ItemStack(Items.GOLD_INGOT, 4));
        menu.clearToPlayerInventory(type.getCraftingInventory());
        helper.assertTrue(countPlayer(player, Items.GOLD_INGOT) == 4 && host.storage.count(Items.GOLD_INGOT) == 0,
                "take items must move the grid into the player inventory only");

        var fullHost = new TestHost(player, type, 0);
        var fullMenu = new ExtendedTerminalBaseMenu(BasicTerminalMenu.TYPE, 2, player.getInventory(), fullHost, type, TEST_CONFIG);
        fullMenu.broadcastChanges();
        fullHost.grid.setItemDirect(0, new ItemStack(Items.REDSTONE, 5));
        int redstoneBefore = countPlayer(player, Items.REDSTONE);
        fullMenu.doAction(player, InventoryAction.MOVE_REGION,
                fullMenu.getSlots(type.getSlotSemanticGrid()).get(0).index, 0);
        helper.assertTrue(fullHost.grid.getStackInSlot(0).getCount() == 5
                        && countPlayer(player, Items.REDSTONE) == redstoneBefore,
                "full ME storage must leave store items in the custom grid");
        helper.succeed();
    }

    private static void inventoryRoundTrip(GameTestHelper helper) {
        helper.assertTrue(AddTerminalEvent.didRun(), "Myotus wireless registration hook did not run");
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-wireless-test"));
        helper.assertTrue(CraftingTerminalHandler.getCraftingTerminalHandler(player).getTargetGrid() == null,
                "wireless handler without a terminal must not resolve a grid");
        var types = List.of(ETMenuType.EPIC_TERMINAL, ETMenuType.LEGENDARY_TERMINAL);
        var items = List.of((ItemWT) WTItems.WIRELESS_EPIC_TERMINAL.asItem(),
                (ItemWT) WTItems.WIRELESS_LEGENDARY_TERMINAL.asItem());
        var menus = List.of(ExtendedCraftingWTMenu.EPIC_TYPE, ExtendedCraftingWTMenu.LEGENDARY_TYPE);
        var markers = List.of(Items.DIAMOND, Items.EMERALD);
        var universal = new ItemStack(ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(AE2wtlib.MOD_NAME, "wireless_universal_terminal")));

        for (int i = 0; i < types.size(); i++) {
            var type = types.get(i);
            var definition = WUTHandler.wirelessTerminals.get(type.getWTIdAsString());
            helper.assertTrue(definition != null, type + " missing from Myotus WUT registration");
            helper.assertTrue(definition.item() == items.get(i) && definition.menuType() == menus.get(i),
                    type + " WUT item/menu mapping");
            var standalone = new ItemStack(items.get(i));
            helper.assertTrue(items.get(i).getMenuType(standalone) == menus.get(i), type + " standalone menu mapping");
            universal.getOrCreateTag().putBoolean(type.getWTIdAsString(), true);

            for (var stack : List.of(standalone, universal)) {
                var host = definition.wTMenuHostFactory().create(player, null, stack, (p, menu) -> { });
                helper.assertTrue(host instanceof ExtendedCraftingWTHost craftingHost
                        && craftingHost.getTerminalType() == type, type + " WUT host factory");
                var grid = host.getSubInventory(type.getCraftingInventory());
                int size = i == 0 ? 121 : 169;
                helper.assertTrue(grid != null && grid.size() == size, type + " grid size");
                grid.setItemDirect(0, new ItemStack(markers.get(i), 1));
                grid.setItemDirect(size - 1, new ItemStack(markers.get(i), 3));
                host.getViewCellStorage().setItemDirect(4, new ItemStack(AEItems.VIEW_CELL));
                host.getSubInventory(WTMenuHost.INV_SINGULARITY).setItemDirect(0,
                        new ItemStack(AEItems.QUANTUM_ENTANGLED_SINGULARITY));

                var restoredStack = ItemStack.of(stack.save(new CompoundTag()));
                var restored = definition.wTMenuHostFactory().create(player, null, restoredStack, (p, menu) -> { });
                var restoredGrid = restored.getSubInventory(type.getCraftingInventory());
                helper.assertTrue(ItemStack.matches(grid.getStackInSlot(size - 1), restoredGrid.getStackInSlot(size - 1)),
                        type + " last grid slot must survive item NBT roundtrip");
                helper.assertTrue(restored.getViewCellStorage().getStackInSlot(4).is(AEItems.VIEW_CELL.asItem()),
                        type + " view cells must survive item NBT roundtrip");
                helper.assertTrue(restored.getSubInventory(WTMenuHost.INV_SINGULARITY).getStackInSlot(0)
                        .is(AEItems.QUANTUM_ENTANGLED_SINGULARITY.asItem()),
                        type + " singularity must survive item NBT roundtrip");
                var menu = new ExtendedCraftingWTMenu(menus.get(i), i, player.getInventory(),
                        (ExtendedCraftingWTHost) restored);
                helper.assertTrue(menu.getType() == menus.get(i)
                                && menu.getSlots(type.getSlotSemanticGrid()).size() == size
                                && menu.getCraftingMatrix() == restoredGrid,
                        type + " menu must expose every crafting slot from the wireless host");
            }
        }

        var restoredUniversal = ItemStack.of(universal.save(new CompoundTag()));
        for (int i = 0; i < types.size(); i++) {
            var type = types.get(i);
            var host = WUTHandler.wirelessTerminals.get(type.getWTIdAsString()).wTMenuHostFactory()
                    .create(player, null, restoredUniversal, (p, menu) -> { });
            var grid = host.getSubInventory(type.getCraftingInventory());
            helper.assertTrue(grid.getStackInSlot(0).is(markers.get(i))
                            && grid.getStackInSlot(grid.size() - 1).is(markers.get(i))
                            && grid.getStackInSlot(grid.size() - 1).getCount() == 3,
                    type + " grid must remain independent when both terminals share a WUT");
        }
        helper.succeed();
    }

    private static int countPlayer(Player player, net.minecraft.world.item.Item item) {
        int count = 0;
        for (var stack : player.getInventory().items) {
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static final IETTerminalConfig TEST_CONFIG = new IETTerminalConfig() {
        @Override
        public boolean enableTerminal() {
            return true;
        }

        @Override
        public boolean enableCraftOnlyPowered() {
            return false;
        }

        @Override
        public double passiveDrainAE() {
            return 0;
        }
    };

    private static final class TestHost extends ItemMenuHost implements IPortableTerminal, ISegmentedInventory {
        private final ETMenuType type;
        private final TestStorage storage;
        private final AppEngInternalInventory grid;
        private final ConfigManager config = new ConfigManager(() -> { });

        private TestHost(Player player, ETMenuType type, long capacity) {
            super(player, null, new ItemStack(Items.CRAFTING_TABLE));
            this.type = type;
            this.storage = new TestStorage(capacity);
            this.grid = new AppEngInternalInventory(null, type.getGridSize());
        }

        @Override
        public MEStorage getInventory() {
            return storage;
        }

        @Override
        public InternalInventory getSubInventory(ResourceLocation id) {
            return type.getCraftingInventory().equals(id) ? grid : InternalInventory.empty();
        }

        @Override
        public double extractAEPower(double amt, Actionable mode, PowerMultiplier usePowerMultiplier) {
            return amt;
        }

        @Override
        public ConfigManager getConfigManager() {
            return config;
        }

        @Override
        public void returnToMainMenu(Player player, ISubMenu subMenu) {
        }

        @Override
        public ItemStack getMainMenuIcon() {
            return new ItemStack(Items.CRAFTING_TABLE);
        }
    }

    private static final class TestStorage implements MEStorage {
        private final long capacity;
        private final Map<AEKey, Long> amounts = new HashMap<>();
        private boolean allowExtraction = true;
        private int reports;
        private long largestSimulatedExtraction;

        private TestStorage(long capacity) {
            this.capacity = capacity;
        }

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            long accepted = Math.min(amount, capacity - amounts.values().stream().mapToLong(Long::longValue).sum());
            if (accepted > 0 && mode == Actionable.MODULATE) {
                amounts.merge(what, accepted, Long::sum);
            }
            return Math.max(accepted, 0);
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (mode == Actionable.SIMULATE) {
                largestSimulatedExtraction = Math.max(largestSimulatedExtraction, amount);
            }
            long extracted = allowExtraction ? Math.min(amount, amounts.getOrDefault(what, 0L)) : 0;
            if (extracted > 0 && mode == Actionable.MODULATE) {
                amounts.merge(what, -extracted, Long::sum);
            }
            return extracted;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            reports++;
            amounts.forEach(out::add);
        }

        private long count(net.minecraft.world.item.Item item) {
            return amounts.getOrDefault(AEItemKey.of(item), 0L);
        }

        @Override
        public Component getDescription() {
            return Component.literal("test");
        }
    }
}
