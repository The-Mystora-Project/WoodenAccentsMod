package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.woodenAccentsMod.block.textureId
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.asBlockStateVariant
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.withYRotationOf
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.customGroup
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.StairsShape
import net.minecraft.data.models.*
import net.minecraft.data.models.blockstates.*
import net.minecraft.data.models.model.*
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.Direction
import net.minecraft.data.recipes.RecipeOutput


class KitchenCounterBlock(baseBlock: Block, topBlock: Block) : AbstractKitchenCounterBlock(baseBlock, topBlock),
    CustomItemGroupProvider, CustomTagProvider<Block>, CustomRecipeProvider, CustomBlockStateProvider {
    override val tag: TagKey<Block> = ModBlockTags.kitchenCounters
    override val itemGroup: ModItemGroup = ModItemGroup.FURNITURE

    override fun codec(): MapCodec<out KitchenCounterBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock },
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("top_block").forGetter { it.topBlock }
        ).apply(instance, ::KitchenCounterBlock)
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val block = this
        TextureMapping().apply {
            put(TextureSlot.TOP, block.topBlock.textureId)
            put(TextureSlot.SIDE, block.baseBlock.textureId)
        }.let { map ->
            val normalModel = ModModels.kitchenCounter.create(block, map, generator.modelOutput)

            generator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(block)
                    .with(
                        variantMap(
                            blockModel = normalModel,
                            innerLeftModel = ModModels.kitchenCounterInnerLeftCorner.create(
                                block,
                                map,
                                generator.modelOutput
                            ),
                            outerLeftModel = ModModels.kitchenCounterOuterLeftCorner.create(
                                block,
                                map,
                                generator.modelOutput
                            )
                        )
                    )
            )
            generator.delegateItemModel(block, normalModel)
        }
    }

    private fun variantMap(
        blockModel: ResourceLocation,
        innerLeftModel: ResourceLocation,
        outerLeftModel: ResourceLocation
    ) = PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.STAIRS_SHAPE).apply {
        val northBlock = blockModel.asBlockStateVariant()
        val northInnerLeft = innerLeftModel.asBlockStateVariant()
        val northOuterLeft = outerLeftModel.asBlockStateVariant()

        mapOf(
            Direction.NORTH to mapOf(
                StairsShape.STRAIGHT to northBlock,
                StairsShape.INNER_LEFT to northInnerLeft,
                StairsShape.OUTER_LEFT to northOuterLeft,
                StairsShape.INNER_RIGHT to northInnerLeft.withYRotationOf(VariantProperties.Rotation.R90),
                StairsShape.OUTER_RIGHT to northOuterLeft.withYRotationOf(VariantProperties.Rotation.R90),
            ),
            Direction.EAST to mapOf(
                StairsShape.STRAIGHT to northBlock.withYRotationOf(VariantProperties.Rotation.R90),
                StairsShape.INNER_LEFT to northInnerLeft.withYRotationOf(VariantProperties.Rotation.R90),
                StairsShape.OUTER_LEFT to northOuterLeft.withYRotationOf(VariantProperties.Rotation.R90),
                StairsShape.INNER_RIGHT to northInnerLeft.withYRotationOf(VariantProperties.Rotation.R180),
                StairsShape.OUTER_RIGHT to northOuterLeft.withYRotationOf(VariantProperties.Rotation.R180),
            ),
            Direction.SOUTH to mapOf(
                StairsShape.STRAIGHT to northBlock.withYRotationOf(VariantProperties.Rotation.R180),
                StairsShape.INNER_LEFT to northInnerLeft.withYRotationOf(VariantProperties.Rotation.R180),
                StairsShape.OUTER_LEFT to northOuterLeft.withYRotationOf(VariantProperties.Rotation.R180),
                StairsShape.INNER_RIGHT to northInnerLeft.withYRotationOf(VariantProperties.Rotation.R270),
                StairsShape.OUTER_RIGHT to northOuterLeft.withYRotationOf(VariantProperties.Rotation.R270),
            ),
            Direction.WEST to mapOf(
                StairsShape.STRAIGHT to northBlock.withYRotationOf(VariantProperties.Rotation.R270),
                StairsShape.INNER_LEFT to northInnerLeft.withYRotationOf(VariantProperties.Rotation.R270),
                StairsShape.OUTER_LEFT to northOuterLeft.withYRotationOf(VariantProperties.Rotation.R270),
                StairsShape.INNER_RIGHT to northInnerLeft,
                StairsShape.OUTER_RIGHT to northOuterLeft,
            )
        ).forEach { i -> i.value.forEach { j -> select(i.key, j.key, j.value) } }
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 4).apply {
            define('#', baseBlock)
            define('_', topBlock)
            pattern("___")
            pattern("###")
            pattern("###")
            customGroup(this@KitchenCounterBlock, "kitchen_counters")
            requires(baseBlock)
            save(recipeExporter)
        }
        offerWoodcuttingRecipe(recipeExporter, topBlock, this)
    }

}
