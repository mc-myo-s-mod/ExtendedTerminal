# Changelog

## Unreleased

### Minecraft 1.20.1 (Forge, 15.1.1)

- Add wireless Epic and Legendary terminals.
- Optimize United Terminal recipe lookup and shift-crafting to reduce crafting delays.
- Fix offset and shapeless recipes, NBT-sensitive outputs, and crafting remainder placement in United Terminal.
- Improve custom shaped recipe transfers and reject extra ingredients outside the selected crafting tier.
- Fix JEI/EMI transfers treating ordinary crafting terminals as United Terminal.
- Correct anvil XP costs at low player levels and preserve fractional XP accounting.
- Align Store/Take button order with AE2 and update the Epic/Legendary terminal guide pages.

### Minecraft 26.1.2 (NeoForge)

- Add the initial Minecraft 26.1.2 port.
- Fix crafting remainder placement in United Terminal.
- Improve JEI transfers for custom shaped recipes and validate recipe layout and grid size.
- Correct anvil XP costs at low player levels and preserve fractional XP accounting.
- Preserve United crafting slots when editing the shared Wireless Universal Terminal grid from the Extended panel.
- Validate stonecutting inputs, restore the remembered result on reopening, and block inactive-panel crafting.
- Apply the active-network crafting restriction to all Extended Terminal panels.
- Reject malformed smithing packets and invalid smithing/stonecutting recipe transfers safely.
- Recognize Wireless United Terminal in magnet-mode terminal lookup.
- Support Minecraft 26.1.2 release tags and publishing with Java 25.

## 19.1.0 / 15.1.0

### Features
- Add `United Terminal`, which supports all NxN crafting recipes including Re:Avaritia, AvaritiaNeo, and Extended Crafting.
- Add `Extended Terminal Anvil` support for consuming `Applied Experience` or `Fluid XP` instead of player XP.
- Let the AE2 wireless terminal hotkey open Extended Wireless Terminal and Wireless United Terminal.

### Bug Fixes
- Fix the 1.21.1 wireless terminal settings screen with AE2WTLib 19.3.0 and newer.
- Recognize Sophisticated Core XP fluid as an anvil experience source.
