package com.github.mystery2099.woodenAccentsMod.data.generation

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.block.isPlank
import com.github.mystery2099.woodenAccentsMod.block.isStripped
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
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

        woodFamilies().forEach { (log, wood, strippedLog, strippedWood, planks) ->
            woodcuttingResultFromBase(recipeExporter, strippedLog, log)
            woodcuttingResultFromBase(recipeExporter, strippedWood, wood)
            woodcuttingResultFromBase(recipeExporter, planks, log, 4)
            woodcuttingResultFromBase(recipeExporter, planks, wood, 4)
            woodcuttingResultFromBase(recipeExporter, planks, strippedLog, 4)
            woodcuttingResultFromBase(recipeExporter, planks, strippedWood, 4)
        }

        woodcuttingResultFromBase(recipeExporter, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_BLOCK)
        woodcuttingResultFromBase(recipeExporter, Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK, 2)
        woodcuttingResultFromBase(recipeExporter, Blocks.BAMBOO_PLANKS, Blocks.STRIPPED_BAMBOO_BLOCK, 2)
    }

    private fun woodFamilies() = listOf(
        WoodFamily(Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD, Blocks.OAK_PLANKS),
        WoodFamily(Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.SPRUCE_PLANKS),
        WoodFamily(Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD, Blocks.BIRCH_PLANKS),
        WoodFamily(Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.JUNGLE_PLANKS),
        WoodFamily(Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD, Blocks.ACACIA_PLANKS),
        WoodFamily(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.DARK_OAK_PLANKS),
        WoodFamily(Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_WOOD, Blocks.MANGROVE_PLANKS),
        WoodFamily(Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG, Blocks.STRIPPED_CHERRY_WOOD, Blocks.CHERRY_PLANKS),
        WoodFamily(Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.CRIMSON_PLANKS),
        WoodFamily(Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.WARPED_PLANKS)
    )

    private data class WoodFamily(
        val log: Block,
        val wood: Block,
        val strippedLog: Block,
        val strippedWood: Block,
        val planks: Block
    )

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
