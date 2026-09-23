# Changelog

## Unreleased

### Minecraft 1.20.1 (Forge)

- Add wireless Epic and Legendary terminals.
- Optimize United Terminal recipe lookup and shift-crafting to reduce crafting delays.
- Fix offset and shapeless recipes, NBT-sensitive outputs, and crafting remainder placement in United Terminal.
- Improve custom shaped recipe transfers and reject extra ingredients outside the selected crafting tier.
- Correct anvil XP costs at low player levels and preserve fractional XP accounting.
- Align Store/Take button order with AE2 and update the Epic/Legendary terminal guide pages.

### Minecraft 26.1.2 (NeoForge)

- Add the initial Minecraft 26.1.2 port.
- Fix crafting remainder placement in United Terminal.
- Improve JEI transfers for custom shaped recipes and validate recipe layout and grid size.
- Correct anvil XP costs at low player levels and preserve fractional XP accounting.

## 19.1.0 / 15.1.0

### Features
- Add `United Terminal`, which supports all NxN crafting recipes including Re:Avaritia, AvaritiaNeo, and Extended Crafting.
- Add `Extended Terminal Anvil` support for consuming `Applied Experience` or `Fluid XP` instead of player XP.
- Let the AE2 wireless terminal hotkey open Extended Wireless Terminal and Wireless United Terminal.

### Bug Fixes
- Fix the 1.21.1 wireless terminal settings screen with AE2WTLib 19.3.0 and newer.
- Recognize Sophisticated Core XP fluid as an anvil experience source.
