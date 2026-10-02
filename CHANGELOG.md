# Changelog

This file tracks player-visible changes and anything maintainers need to know before publishing a release. Release headings must match `mod_version` in `gradle.properties` so the publishing workflow can use the section as its release notes.

## [1.21.1-1.3.0.0] - 2026-10-02

### Added

- Added the Woodcutter, a wooden take on the stonecutter. Craft it with an iron ingot over three planks of any wood.
- Added the `wooden_accents_mod:woodcutting` recipe type so datapacks and other mods can add their own woodcutter recipes. It uses the same JSON format as `minecraft:stonecutting`.
- Woodcutting recipes unlock like stonecutting ones do, complete with a recipe toast showing the woodcutter.
- Woodcutting recipes show up in JEI and REI on both loaders, with the woodcutter listed as their workstation. JEI can also move ingredients into the woodcutter.
- Vanilla planks can be cut into their matching stairs and slabs, and bamboo planks into bamboo mosaic.
- Added woodcutter recipes for vanilla fences, gates, doors, trapdoors, buttons, pressure plates, signs, and bamboo mosaic stairs and slabs.
- Logs, wood, stems, hyphae, and bamboo blocks can be stripped or cut into planks in the woodcutter.
- Added woodcutter recipes for chairs, tables, coffee tables, desks, counters, shelves, narrow bookshelves, ladders, walls, pillars, support beams, and picket fences and gates. Thin pillars yield four per plank.
- Every plank-based woodcutting result can also be cut directly from matching logs and wood, including stripped variants. Direct recipes yield four times the plank recipe output, or twice for bamboo blocks.
- Wooden Accents blocks burn in furnaces like vanilla wooden blocks. The woodcutter burns for 300 ticks like other wooden workstations. Thin pillars and plank flooring burn for less since a plank cuts into several of them. Crimson and warped variants don't burn.
- Added the `tall_coffee_table_connectable`, `support_beams_connectable`, and `support_beams_cannot_connect` block tags so data packs can change what tall coffee tables and support beams connect to. Tables now connect to anything in the `tables` tag.
- Picket fences and gates are now in the vanilla `wooden_fences` and `fence_gates` tags and in `c:fence_gates/wooden`. Wooden walls are in the `walls` item tag.

### Changed

- Plank flooring now comes from woodcutting instead of a crafting recipe with paper. One plank yields eight flooring pieces. Matching logs and wood yield thirty-two, and bamboo blocks yield sixteen.
- Picket fences now connect to vanilla wooden fences, and vanilla wooden fences connect back.
- Plank flooring plays the step sound of the block below it, like wool carpets.

### Fixed

- Kitchen cabinets can no longer be crafted with ender chests. Crates and desk drawers now accept wooden chests from other mods.

## [1.21.1-1.2.0.0] - 2026-09-25

This is the first stable Wooden Accents release for Minecraft 1.21.1 on Fabric and NeoForge.

### Changed

- Ported Fabric and NeoForge builds to Minecraft 1.21.1.
- Updated Fabric API to 0.116.17+1.21.1, NeoForge to 21.1.251, VoxLib to 1.8.0+1.21.1, and Shulker Box Tooltip to 5.1.9+1.21.1.

### Fixed

- Moved generated datapack folders to the singular 1.21 paths so block tags, recipes, loot tables, and advancements load again. This restores ladder climbing and picket fence connections.
- Made picket fences connect to thick and thin pillars.
- Allowed thin bookshelves to use the vanilla chiseled bookshelf block entity so inserting and removing books no longer crashes.

## [1.20.6-1.1.4.4] - 2026-09-24

This is the first published 1.20.6 build for Fabric and NeoForge.

### Added

- Added a NeoForge build for Minecraft 1.20.6 with the same blocks, items, creative tabs, renderers, and optional storage previews as Fabric.

### Changed

- Ported the Fabric build to Minecraft 1.20.6 and Java 21, including data component storage, block codecs, entity save data, advancement criteria, and generated resources.

### Fixed

- Kept crates from accepting vanilla shulker boxes on either loader, even when the Fabric convention tag is absent.

[Full changelog](https://github.com/The-Mystora-Project/WoodenAccentsMod/compare/v1.20.1-1.1.4.2...v1.20.6-1.1.4.4)

## [1.20.6-1.1.4.3] - 2026-09-22

### Changed

- Ported Wooden Accents to Minecraft 1.20.6 with Fabric API 0.100.8+1.20.6 on Java 21.
- Moved item NBT usage to the data component system; coffee table variants now persist as a registered data component.
- Reworked crate item contents to vanilla shulker-box-style container components.
- Replaced raw NBT loot manipulation with component copy functions and item sub-predicate enchantment conditions.
- Updated block codecs, entity save data threading, seat attachment points, advancement criteria, and datagen providers for the 1.20.6 APIs.
- Regenerated data for the 1.20.6 formats and removed leftover unreferenced datapack files.

### Fixed

- Kept seat riders placed on chair seats by relocating the mount height to the 1.20.6 entity attachment system.

[Full changelog](https://github.com/The-Mystora-Project/WoodenAccentsMod/compare/v1.20.1-1.1.4.2...v1.20.6-1.1.4.3)

## [1.20.1-1.1.4.2] - 2026-09-22

### Changed

- Split loader-agnostic content into a common module and a Fabric loader module.
- Standardized shared sources on official Mojang mappings.
- Deferred the second loader port to the planned NeoForge 1.20.6 update; this alpha release ships Fabric only.

[Full changelog](https://github.com/The-Mystora-Project/WoodenAccentsMod/compare/v1.20.1-1.1.4.1...v1.20.1-1.1.4.2)

## [1.20.1-1.1.4.1] - 2026-09-18

### Changed

- Updated Wooden Accents from Minecraft 1.19.4 to 1.20.1.
- Updated Fabric Loader, Fabric API, Fabric Language Kotlin, VoxLib, and optional mod compatibility for their 1.20.1 releases.
- Made bamboo, cherry, and narrow bookshelf content available without experimental feature flags.
- Updated creative tabs, block settings, loot handling, rendering hooks, and advancement criteria for Minecraft 1.20.1 APIs.

### Fixed

- Moved Wooden Accents advancements out of the `minecraft` namespace and into the mod's namespace.

[Full changelog](https://github.com/The-Mystora-Project/WoodenAccentsMod/compare/v1.19.4-1.1.4.0...v1.20.1-1.1.4.1)

## [1.19.4-1.1.4.0] - 2026-09-18

### Added

- Design philosophy and a visual guide to combining structural blocks.
- Advancements for desks, desk drawers, kitchen counters, and kitchen cabinets.
- Advancements covering narrow bookshelves, bracket shelves, plank walls, simple ladders, and a "Home Sweet Home" goal for owning furniture from every category.
- Placement-based advancement challenges that showcase block flexibility: connect pillars into seamless columns, stack support beams, link connecting ladders sideways, fit picket fence gates into wall openings, link bracket shelves together, and build corner-connected desks and kitchen counters.
- Player documentation for seating, named storage, portable crates, tall coffee tables, and bracket shelf loadouts.

### Changed

- Organized creative tabs into Wooden Accents: Furniture, Storage, and Building. Each family stays together, including tall coffee table variants in vanilla wood order.
- Renamed modern fences to picket fences, plank carpets to plank flooring, and thin bookshelves to narrow bookshelves. Existing block and item IDs are unchanged.
- Updated advancement descriptions to match the new names.
- Reworked plank ladder recipes to look like boards nailed to a wall: two planks of the wood between rows of stick supports, yielding four ladders. This also resolves the recipe conflicts with the simple ladders, which keep the original all-planks recipe.

### Fixed

- Chairs lift riders slightly so their legs clear the seat's front edge.
- Chairs reject additional riders and clean up their seat entity when the chair breaks or the rider leaves.
- Kitchen cabinets preserve custom names on dropped items and mirror their facing correctly.
- Picking a crate preserves its custom display name along with its contents.
- Crates and desk drawers emit a container-close event when the last viewer closes them.
- Connecting ladders safely detach when their support is removed and connect immediately on placement.
- Tables initialize their connections on placement.
- Sneak-placing a coffee table against another coffee table places it normally instead of creating a tall coffee table.

[Full changelog](https://github.com/The-Mystora-Project/WoodenAccentsMod/compare/v1.19.4-1.1.3.1...v1.19.4-1.1.4.0)

## [1.19.4-1.1.3.1] - 2026-09-14

### Fixed

- Bracket shelves now map stored items and powered hotbar loadouts from left to right when viewed from the front instead of reversing their order.
- Empty bracket shelves now clear their displayed items immediately after a powered hotbar swap.

[Full changelog](https://github.com/The-Mystora-Project/WoodenAccentsMod/compare/v1.19.4-1.1.3.0...v1.19.4-1.1.3.1)

## [1.19.4-1.1.3.0] - 2026-09-14

### Added

- Bracket shelves for all 12 vanilla plank types. Shelves attach to any full square solid side, connect side-by-side with supports at each end and every three shelves, and can be placed in or out of water like other waterloggable blocks. Crafted with 3 planks over two sticks, yielding two shelves.
- Bracket shelves now store and display up to three full item stacks, one per third of the shelf face. Using a shelf swaps the pointed-at slot with the stack in the interacting hand, like vanilla wall shelves.
- When powered by redstone, interacting with a bracket shelf swaps its three slots (and those of any connected powered shelves facing the same way, up to three shelves in total) with the rightmost three, six, or all nine hotbar slots for instant loadout changes.
- A comparator placed behind a bracket shelf outputs a signal strength based on which slots are filled: 1 for the first slot, 2 for the second, 4 for the third, up to a maximum of 7.
- Hoppers can fill bracket shelves from above and empty them from below. Shelves scatter their stored items when broken or when their support block is removed.

[Full changelog](https://github.com/The-Mystora-Project/WoodenAccentsMod/compare/v1.19.4-1.1.2.0...v1.19.4-1.1.3.0)

## [1.19.4-1.1.2.0] - 2026-08-21

### Changed

- Reworked the project, contribution, and release documentation.
- Rewrote the player README for clearer features, dependencies, and FAQ, and mirrored Modrinth gallery screenshots under `docs/images/`.
- Precomputed reusable outline shapes for connecting and multipart blocks without changing their dimensions.
- Desks, desk drawers, kitchen counters, and kitchen cabinets now use full-block collision while retaining their detailed selection outlines. Isolated thick pillars use full-block collision and return to their detailed shape when connected.
- Raised the minimum supported versions to Fabric Loader 0.19.3, Fabric Language Kotlin 1.13.13+kotlin.2.4.10, and VoxLib 1.6.1+1.19.4.

### Fixed

- Desk drawers now drop their stored contents when broken and preserve custom names on the dropped block item.

[Full changelog](https://github.com/The-Mystora-Project/WoodenAccentsMod/compare/v1.19.4-1.1.1.1...v1.19.4-1.1.2.0)

## [1.19.4-1.1.1.1] - 2024-05-20

Simplified outline shapes for:

- Plank ladders
- Desk drawers
- Modern fences
- Modern fence gates
- Thin pillars

[Full changelog](https://github.com/The-Mystora-Project/WoodenAccentsMod/compare/v1.19.4-1.1.1.0...v1.19.4-1.1.1.1)

## [1.19.4-1.1.1.0] - 2024-05-20

Implemented an advancement provider and generated new advancements.

[Full changelog](https://github.com/The-Mystora-Project/WoodenAccentsMod/compare/v1.1+1.19.4...v1.19.4-1.1.1.0)

## [1.1+1.19.4] - 2024-03-15

- Updated Fabric Loader from 0.14.23 to 0.15.7.
- Updated Fabric Language Kotlin from 1.10.14+kotlin.1.9.20 to 1.10.19+kotlin.1.9.23.
- Updated Kotlin from 1.9.20 to 1.9.23.
- Updated the mod version from 1.0.0+1.19.4 to 1.1+1.19.4.
- Re-added thin pillar blocks.
- Re-added thick pillar blocks.
- Added data files providing compatibility with Easy Shulker Boxes for all storage blocks in this mod.

## [1.0.0+1.19.4] - 2023-12-03

Initial release.

Wooden Accents Mod adds wooden furniture, walls, pillars, crates, and other decorative blocks for builders.

Added wooden variants of:

- Chairs
- Tables
- Coffee tables
- Stripped wood ladders
- Plank ladders
- Crates
- Plank carpets
- Plank walls
- Desks
- Desk drawers
- Kitchen counters
- Kitchen cabinets
- Modern fences
- Modern fence gates
- Supports
- Thin pillars
- Thick pillars
- Thin bookshelves

Each block is available for every wood type in Minecraft 1.19.4, including wood types behind experimental features.
