package com.github.mystery2099.woodenAccentsMod.data.generation

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.block.isPlank
import com.github.mystery2099.woodenAccentsMod.block.isStripped
import com.github.mystery2099.woodenAccentsMod.recipe.WoodcuttingRecipe
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.SingleItemRecipeBuilder
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.SingleItemRecipe
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

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

    /** Offers a one-input woodcutting conversion and gives it a stable ID based on both blocks. */
    fun offerWoodcuttingRecipe(
        recipeOutput: RecipeOutput,
        input: ItemLike,
        result: ItemLike,
        count: Int = 1,
        expandLogInputs: Boolean = true
    ) {
        offerSingleWoodcuttingRecipe(recipeOutput, input, result, count)

        if (!expandLogInputs) return
        plankWoodInputs[input.asItem()]?.let { (woodInputs, plankYield) ->
            woodInputs.forEach { woodInput ->
                offerSingleWoodcuttingRecipe(recipeOutput, woodInput, result, count * plankYield)
            }
        }
    }

    private fun offerSingleWoodcuttingRecipe(recipeOutput: RecipeOutput, input: ItemLike, result: ItemLike, count: Int) {
        val inputId = BuiltInRegistries.ITEM.getKey(input.asItem())
        val resultId = BuiltInRegistries.ITEM.getKey(result.asItem())
        woodcutting(Ingredient.of(input), RecipeCategory.BUILDING_BLOCKS, result, count)
            .unlockedBy(RecipeProvider.getHasName(input), RecipeProvider.has(input))
            .save(recipeOutput, "${resultId.path}_from_${inputId.path}_woodcutting".toIdentifier())
    }

    private val plankWoodInputs: Map<Item, Pair<List<ItemLike>, Int>> = mapOf(
        Blocks.OAK_PLANKS.asItem() to (listOf(Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD) to 4),
        Blocks.SPRUCE_PLANKS.asItem() to (listOf(Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD) to 4),
        Blocks.BIRCH_PLANKS.asItem() to (listOf(Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD) to 4),
        Blocks.JUNGLE_PLANKS.asItem() to (listOf(Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD) to 4),
        Blocks.ACACIA_PLANKS.asItem() to (listOf(Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD) to 4),
        Blocks.DARK_OAK_PLANKS.asItem() to (listOf(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD) to 4),
        Blocks.MANGROVE_PLANKS.asItem() to (listOf(Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_WOOD) to 4),
        Blocks.CHERRY_PLANKS.asItem() to (listOf(Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG, Blocks.STRIPPED_CHERRY_WOOD) to 4),
        Blocks.BAMBOO_PLANKS.asItem() to (listOf(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK) to 2),
        Blocks.CRIMSON_PLANKS.asItem() to (listOf(Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_HYPHAE) to 4),
        Blocks.WARPED_PLANKS.asItem() to (listOf(Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM, Blocks.STRIPPED_WARPED_HYPHAE) to 4),
        Blocks.BAMBOO_MOSAIC.asItem() to (listOf(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK) to 2)
    )

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
