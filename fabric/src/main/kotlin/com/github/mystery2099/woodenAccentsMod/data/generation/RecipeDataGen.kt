package com.github.mystery2099.woodenAccentsMod.data.generation

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.block.isPlank
import com.github.mystery2099.woodenAccentsMod.block.isStripped
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.woodcutting
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.BlockFamilies
import net.minecraft.data.BlockFamily
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture

class RecipeDataGen(output: FabricDataOutput,
                    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricRecipeProvider(output, registriesFuture) {

    override fun buildRecipes(recipeExporter: RecipeOutput) {
        ModBlocks.blocks.filterIsInstance<CustomRecipeProvider>().forEach {
            it.offerRecipeTo(recipeExporter)
        }
        offerVanillaWoodcuttingRecipes(recipeExporter)
    }

    /** Gives every vanilla wood family the same plank conversions the stonecutter gives stone. */
    private fun offerVanillaWoodcuttingRecipes(recipeExporter: RecipeOutput) {
        BlockFamilies.getAllFamilies()
            .filter { it.recipeGroupPrefix.orElse("") == "wooden" }
            .forEach { family ->
                val planks = family.baseBlock
                family.get(BlockFamily.Variant.STAIRS)?.let { woodcuttingResultFromBase(recipeExporter, it, planks) }
                family.get(BlockFamily.Variant.SLAB)?.let { woodcuttingResultFromBase(recipeExporter, it, planks, 2) }
                family.get(BlockFamily.Variant.MOSAIC)?.let { woodcuttingResultFromBase(recipeExporter, it, planks) }
            }
    }

    private fun woodcuttingResultFromBase(recipeExporter: RecipeOutput, result: ItemLike, base: ItemLike, count: Int = 1) {
        woodcutting(Ingredient.of(base), RecipeCategory.BUILDING_BLOCKS, result, count)
            .unlockedBy(getHasName(base), has(base))
            .save(recipeExporter, "${getConversionRecipeName(result, base)}_woodcutting".toIdentifier())
    }

    companion object {
        /** Adds the recipe-book unlock criterion for [requiredItem]. */
        fun ShapedRecipeBuilder.requires(requiredItem: ItemLike): ShapedRecipeBuilder {
            return unlockedBy(getHasName(requiredItem), has(requiredItem))
        }

        /** Adds the recipe-book unlock criterion for [requiredTag]. */
        fun ShapedRecipeBuilder.requires(requiredTag: TagKey<Item>): ShapedRecipeBuilder {
            return unlockedBy("has_${requiredTag.location()}", has(requiredTag))
        }

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

}
