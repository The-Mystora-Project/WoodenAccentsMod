package com.github.mystery2099.woodenAccentsMod.data.generation

import com.github.mystery2099.woodenAccentsMod.block.isPlank
import com.github.mystery2099.woodenAccentsMod.block.isStripped
import com.github.mystery2099.woodenAccentsMod.recipe.WoodcuttingRecipe
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.SingleItemRecipeBuilder
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.SingleItemRecipe
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block

/** Recipe-builder conveniences used by blocks that own their recipe definitions. */
object RecipeUtil {
    /** Adds the recipe-book unlock criterion for [requiredItem]. */
    fun ShapedRecipeBuilder.requires(requiredItem: ItemLike): ShapedRecipeBuilder {
        return unlockedBy(RecipeProvider.getHasName(requiredItem), RecipeProvider.has(requiredItem))
    }

    /** Adds the recipe-book unlock criterion for [requiredTag]. */
    fun ShapedRecipeBuilder.requires(requiredTag: TagKey<Item>): ShapedRecipeBuilder {
        return unlockedBy("has_${requiredTag.location()}", RecipeProvider.has(requiredTag))
    }

    /** The woodcutting counterpart to [SingleItemRecipeBuilder.stonecutting]. */
    fun woodcutting(ingredient: Ingredient, category: RecipeCategory, result: ItemLike, count: Int = 1) =
        SingleItemRecipeBuilder(category, SingleItemRecipe.Factory(::WoodcuttingRecipe), ingredient, result, count)

    fun ShapedRecipeBuilder.customGroup(block: Block, name: String): ShapedRecipeBuilder {
        return group(
            when {
                block.isStripped -> "stripped_$name"
                block.isPlank -> "plank_$name"
                else -> name
            }
        )
    }
}
