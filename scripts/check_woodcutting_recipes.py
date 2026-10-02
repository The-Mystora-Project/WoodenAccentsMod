#!/usr/bin/env python3
"""Check generated woodcutting recipes without launching Minecraft.

Run from the repository root: python3 scripts/check_woodcutting_recipes.py
"""

import json
import sys
from pathlib import Path


WOOD_TYPES = (
    "dark_oak", "mangrove", "crimson", "warped", "spruce", "acacia",
    "cherry", "jungle", "birch", "bamboo", "oak",
)
DATA_DIR = Path(__file__).resolve().parents[1] / "common/src/main/generated/data/wooden_accents_mod"


def require(condition, message):
    if not condition:
        raise ValueError(message)


def wood_type(item):
    path = item.split(":", 1)[1]
    for prefix in ("modern_", "thick_", "thin_", "stripped_"):
        path = path.removeprefix(prefix)
    return next((wood for wood in WOOD_TYPES if path.startswith(wood + "_")), None)


def log_inputs(wood):
    if wood == "bamboo":
        return ("minecraft:bamboo_block", "minecraft:stripped_bamboo_block"), 2
    axis, bark = ("stem", "hyphae") if wood in ("crimson", "warped") else ("log", "wood")
    return tuple(f"minecraft:{prefix}{wood}_{part}" for prefix in ("", "stripped_") for part in (axis, bark)), 4


def check_recipes():
    paths = sorted((DATA_DIR / "recipe").glob("*_woodcutting.json"))
    require(bool(paths), "No generated woodcutting recipes found. Run :fabric:runDatagen first.")
    conversions = {}
    graph = {}
    for path in paths:
        recipe = json.loads(path.read_text())
        require(recipe.get("type") == "wooden_accents_mod:woodcutting", f"{path.name}: wrong recipe type")
        source = recipe["ingredient"]["item"]
        result = recipe["result"]["id"]
        count = recipe["result"]["count"]
        require(source.startswith("minecraft:"), f"{path.name}: unsupported input namespace")
        require(wood_type(source) is not None, f"{path.name}: input is not a supported wood type")
        require(wood_type(source) == wood_type(result), f"{path.name}: source and result wood types differ")
        require(type(count) is int and count in (1, 2, 4, 8), f"{path.name}: unexpected output count {count}")
        pair = (source, result)
        require(pair not in conversions, f"{path.name}: duplicate conversion {pair}")
        conversions[pair] = count
        graph.setdefault(source, set()).add(result)

        advancements = list((DATA_DIR / "advancement/recipes").glob(f"*/{path.name}"))
        require(len(advancements) == 1, f"{path.name}: expected one unlock advancement")
        advancement = json.loads(advancements[0].read_text())
        recipe_id = f"wooden_accents_mod:{path.stem}"
        require(advancement["rewards"]["recipes"] == [recipe_id], f"{path.name}: wrong advancement reward")
        criteria = advancement["criteria"]
        require(criteria["has_the_recipe"]["conditions"]["recipe"] == recipe_id, f"{path.name}: wrong recipe criterion")
        input_criteria = [key for key, value in criteria.items()
                          if value.get("trigger") == "minecraft:inventory_changed"
                          and any(item.get("items") == source for item in value["conditions"]["items"])]
        require(bool(input_criteria), f"{path.name}: no unlock criterion for {source}")
        require(any("has_the_recipe" in group and any(key in group for key in input_criteria)
                    for group in advancement["requirements"]), f"{path.name}: missing unlock requirements")

    for (source, result), count in conversions.items():
        if source.endswith("_planks") or source == "minecraft:bamboo_mosaic":
            inputs, multiplier = log_inputs(wood_type(source))
            for log in inputs:
                require(conversions.get((log, result)) == count * multiplier,
                        f"{source} -> {result}: missing or incorrect direct recipe from {log}")

    for wood in WOOD_TYPES:
        planks = f"minecraft:{wood}_planks"
        for variant in ("stairs", "slab", "fence", "fence_gate", "door", "trapdoor", "button", "pressure_plate", "sign"):
            result = f"minecraft:{wood}_{variant}"
            require(conversions.get((planks, result)) == (2 if variant == "slab" else 1),
                    f"Missing or incorrect vanilla plank recipe: {planks} -> {result}")
        inputs, multiplier = log_inputs(wood)
        for log in inputs:
            require(conversions.get((log, planks)) == multiplier, f"Incorrect plank yield from {log}")
            if not log.startswith("minecraft:stripped_"):
                stripped = log.replace("minecraft:", "minecraft:stripped_", 1)
                require(conversions.get((log, stripped)) == 1, f"Missing stripping recipe for {log}")

    visiting, visited = set(), set()

    def visit(item):
        require(item not in visiting, f"Woodcutting conversion cycle at {item}")
        if item in visited:
            return
        visiting.add(item)
        for result in graph.get(item, ()):
            visit(result)
        visiting.remove(item)
        visited.add(item)

    for source in graph:
        visit(source)
    print(f"Checked {len(paths)} woodcutting recipes: species, yields, direct log inputs, unlocks, and conversion cycles.")


if __name__ == "__main__":
    try:
        check_recipes()
    except (ValueError, KeyError, TypeError) as error:
        print(f"Woodcutting recipe check failed: {error}", file=sys.stderr)
        sys.exit(1)
