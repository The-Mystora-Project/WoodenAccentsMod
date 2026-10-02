package com.github.mystery2099.woodenAccentsMod.data.generation

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.block.isPlank
import com.github.mystery2099.woodenAccentsMod.block.isStripped
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.woodFamilies
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.BlockFamilies
import net.minecraft.data.BlockFamily
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
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
                family.get(BlockFamily.Variant.FENCE)?.let { woodcuttingResultFromBase(recipeExporter, it, planks) }
                family.get(BlockFamily.Variant.FENCE_GATE)?.let { woodcuttingResultFromBase(recipeExporter, it, planks) }
                family.get(BlockFamily.Variant.DOOR)?.let { woodcuttingResultFromBase(recipeExporter, it, planks) }
                family.get(BlockFamily.Variant.TRAPDOOR)?.let { woodcuttingResultFromBase(recipeExporter, it, planks) }
                family.get(BlockFamily.Variant.BUTTON)?.let { woodcuttingResultFromBase(recipeExporter, it, planks) }
                family.get(BlockFamily.Variant.PRESSURE_PLATE)?.let { woodcuttingResultFromBase(recipeExporter, it, planks) }
                family.get(BlockFamily.Variant.SIGN)?.let { woodcuttingResultFromBase(recipeExporter, it, planks) }
            }

        // Vanilla registers bamboo fences separately from its plank block family.
        woodcuttingResultFromBase(recipeExporter, Blocks.BAMBOO_FENCE, Blocks.BAMBOO_PLANKS)
        woodcuttingResultFromBase(recipeExporter, Blocks.BAMBOO_FENCE_GATE, Blocks.BAMBOO_PLANKS)

        val bambooMosaic = Blocks.BAMBOO_MOSAIC
        woodcuttingResultFromBase(recipeExporter, Blocks.BAMBOO_MOSAIC_STAIRS, bambooMosaic)
        woodcuttingResultFromBase(recipeExporter, Blocks.BAMBOO_MOSAIC_SLAB, bambooMosaic, 2)
        woodcuttingResultFromBase(recipeExporter, Blocks.BAMBOO_MOSAIC_STAIRS, Blocks.BAMBOO_PLANKS)
        woodcuttingResultFromBase(recipeExporter, Blocks.BAMBOO_MOSAIC_SLAB, Blocks.BAMBOO_PLANKS, 2)

        woodFamilies.forEach { family ->
            woodcuttingResultFromBase(recipeExporter, family.strippedLog, family.log)
            family.wood?.let { wood ->
                family.strippedWood?.let { strippedWood ->
                    woodcuttingResultFromBase(recipeExporter, strippedWood, wood)
                }
            }
            woodcuttingResultFromBase(recipeExporter, family.planks, family.log, family.plankYield)
            family.wood?.let { wood ->
                woodcuttingResultFromBase(recipeExporter, family.planks, wood, family.plankYield)
            }
            woodcuttingResultFromBase(recipeExporter, family.planks, family.strippedLog, family.plankYield)
            family.strippedWood?.let { strippedWood ->
                woodcuttingResultFromBase(recipeExporter, family.planks, strippedWood, family.plankYield)
            }
        }
    }

    private fun woodcuttingResultFromBase(recipeExporter: RecipeOutput, result: ItemLike, base: ItemLike, count: Int = 1) {
        // Mosaic shapes already get direct log inputs through their bamboo plank recipes.
        offerWoodcuttingRecipe(recipeExporter, base, result, count, expandLogInputs = base != Blocks.BAMBOO_MOSAIC)
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
