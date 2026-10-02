# Wooden Accents roadmap

These are ideas, not promises. Plans tend to change once I actually start implementing them. See the [changelog](CHANGELOG.md) for what has already shipped.

## Minecraft versions

I'd rather keep one current version that feels finished than several half-maintained ones. Older lines stay available for existing worlds and only get important fixes. Block and item IDs stay the same across ports so existing builds keep working.

## Outdoor building ideas

### Bridges

I'd like players to build bridges from sections that connect automatically, with railings along exposed edges. They should work with existing fences where possible and support waterlogging for docks and low walkways.

The old `BridgeBlock` class is an unfinished experiment. I'll start with an oak prototype before adding the other woods. Rope physics, generated bridges, and a separate railing family aren't planned.

### Benches

Benches would use the chair seating behavior and connect into longer seats with left, middle, and right sections. I'll start with one design before considering more styles.
