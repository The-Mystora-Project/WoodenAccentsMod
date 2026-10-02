package com.github.mystery2099.woodenAccentsMod.rei

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod
import com.github.mystery2099.woodenAccentsMod.recipe.WoodcuttingRecipe
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryIngredients
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeHolder
import java.util.Optional

/** Mirrors REI's `DefaultStoneCuttingDisplay` for woodcutting recipes. */
class WoodcuttingDisplay(
    inputs: List<EntryIngredient>,
    outputs: List<EntryIngredient>,
    location: Optional<ResourceLocation>
) : BasicDisplay(inputs, outputs, location) {

    constructor(recipe: RecipeHolder<WoodcuttingRecipe>) : this(
        EntryIngredients.ofIngredients(recipe.value().ingredients),
        listOf(EntryIngredients.of(recipe.value().getResultItem(registryAccess()))),
        Optional.of(recipe.id())
    )

    override fun getCategoryIdentifier(): CategoryIdentifier<*> = categoryId

    companion object {
        val categoryId: CategoryIdentifier<WoodcuttingDisplay> =
            CategoryIdentifier.of(WoodenAccentsMod.MOD_ID, "plugins/woodcutting")

        val serializer: Serializer<WoodcuttingDisplay> = Serializer.ofSimple { inputs, outputs, location ->
            WoodcuttingDisplay(inputs, outputs, location)
        }
    }
}
