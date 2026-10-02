package com.github.mystery2099.woodenAccentsMod.block.custom

import com.github.mystery2099.woodenAccentsMod.block.itemModelId
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.asBlockStateVariant
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.uvLock
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.withXRotationOf
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
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags.contains
import com.github.mystery2099.woodenAccentsMod.util.WhenUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.SupportType
import net.minecraft.data.models.*
import net.minecraft.data.models.blockstates.*
import net.minecraft.data.models.model.*
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.Items
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockBehaviour

class SupportBeamBlock(val baseBlock: Block) : OmnidirectionalConnectingBlock(run {
    if (baseBlock !is RotatedPillarBlock) BlockBehaviour.Properties.ofFullCopy(baseBlock)
    else BlockBehaviour.Properties.of().apply {
        mapColor(baseBlock.defaultMapColor())
        destroyTime(baseBlock.defaultDestroyTime())
        explosionResistance(baseBlock.explosionResistance)
        sound(baseBlock.defaultBlockState().soundType)
        instrument(baseBlock.defaultBlockState().instrument())
        if (baseBlock.defaultBlockState().ignitedByLava()) ignitedByLava()
    }
}), CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider {
    override val tag: TagKey<Block> = ModBlockTags.supportBeams
    override val itemGroup: ModItemGroup = ModItemGroup.BUILDING

    private fun canConnect(pos: BlockPos, direction: Direction, world: LevelAccessor): Boolean {
        val otherState = world.getBlockState(pos.relative(direction))
        // Some blocks, like fence gates, expose a solid center face but look wrong with a beam attached.
        return (otherState.isFaceSturdy(
            world,
            pos.relative(direction),
            direction.opposite,
            SupportType.CENTER
        ) || otherState in tag) && otherState !in ModBlockTags.supportBeamsCannotConnect
    }

    override fun canConnectNorthOf(pos: BlockPos, world: LevelAccessor) = canConnect(pos, Direction.NORTH, world)

    override fun canConnectEastOf(pos: BlockPos, world: LevelAccessor) = canConnect(pos, Direction.EAST, world)

    override fun canConnectSouthOf(pos: BlockPos, world: LevelAccessor) = canConnect(pos, Direction.SOUTH, world)

    override fun canConnectWestOf(pos: BlockPos, world: LevelAccessor) = canConnect(pos, Direction.WEST, world)

    override fun canConnectAbove(pos: BlockPos, world: LevelAccessor) = canConnect(pos, Direction.UP, world)

    override fun canConnectBelow(pos: BlockPos, world: LevelAccessor) = canConnect(pos, Direction.DOWN, world)

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val map = TextureMapping.cube(baseBlock)
        ModModels.supportBeamItem.create(this.itemModelId, map, generator.modelOutput)
        val centerVariant =
            ModModels.supportBeamCenter.create(this, map, generator.modelOutput).asBlockStateVariant()
        val downVariant =
            ModModels.supportBeamDown.create(this, map, generator.modelOutput).asBlockStateVariant().uvLock()
        generator.blockStateOutput.accept(
            MultiPartGenerator.multiPart(this).apply {
                val northVariant = downVariant.withXRotationOf(VariantProperties.Rotation.R270)
                mapOf(
                    Condition.or(
                        WhenUtil.notNorth,
                        WhenUtil.notEast,
                        WhenUtil.notSouth,
                        WhenUtil.notWest,
                        WhenUtil.notUp,
                        WhenUtil.notDown
                    ) to centerVariant,
                    WhenUtil.north to northVariant,
                    WhenUtil.east to northVariant.withYRotationOf(VariantProperties.Rotation.R90),
                    WhenUtil.south to northVariant.withYRotationOf(VariantProperties.Rotation.R180),
                    WhenUtil.west to northVariant.withYRotationOf(VariantProperties.Rotation.R270),
                    WhenUtil.up to downVariant.withXRotationOf(VariantProperties.Rotation.R180),
                    WhenUtil.down to downVariant
                ).forEach{
                    with(it.key, it.value)
                }
            }
        )
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, this, 6).apply {
            define('0', Items.STICK)
            define('#', baseBlock)
            pattern("000")
            pattern("0#0")
            pattern("000")
            customGroup(this@SupportBeamBlock, "support_beams")
            requires(baseBlock)
            save(recipeExporter)
        }
        offerWoodcuttingRecipe(recipeExporter, baseBlock, this)
    }

    override fun codec(): MapCodec<out SupportBeamBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base").forGetter { it.baseBlock }
        ).apply(instance, ::SupportBeamBlock)
    }
}
