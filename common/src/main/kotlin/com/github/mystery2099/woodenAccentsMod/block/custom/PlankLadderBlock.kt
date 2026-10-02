package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LadderBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.Items
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockBehaviour

class PlankLadderBlock(val baseBlock: Block) :
    AbstractCustomLadderBlock(BlockBehaviour.Properties.of().apply {
        mapColor(baseBlock.defaultMapColor())
        destroyTime(Blocks.LADDER.defaultDestroyTime())
        explosionResistance(Blocks.LADDER.explosionResistance)
        sound(baseBlock.defaultBlockState().soundType)
        instrument(baseBlock.defaultBlockState().instrument())
        if (baseBlock.defaultBlockState().ignitedByLava()) ignitedByLava()
    }), CustomItemGroupProvider {
    override val tag: TagKey<Block> = ModBlockTags.plankLadders

    // vanilla LadderBlock declares its codec() as an invariant MapCodec<LadderBlock>, so the codec is typed against
    // LadderBlock while decoding into PlankLadderBlock via its constructor reference.
    override fun codec(): MapCodec<LadderBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { (it as PlankLadderBlock).baseBlock }
        ).apply(instance, ::PlankLadderBlock)
    }

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape = when (state.getValue(FACING)) {
        Direction.NORTH -> northShape
        Direction.EAST -> eastShape
        Direction.SOUTH -> southShape
        Direction.WEST -> westShape
        else -> super.getShape(state, world, pos, context)
    }


    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 4).apply {
            define('S', Items.STICK)
            define('P', baseBlock)
            pattern("SSS")
            pattern("P P")
            pattern("SSS")
            group("plank_ladders")
            requires(baseBlock)
            save(recipeExporter)
        }
        offerWoodcuttingRecipe(recipeExporter, baseBlock, this)
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        ModModels.plankLadder.create(this, TextureMapping.cube(baseBlock), generator.modelOutput)
        generator.createNonTemplateHorizontalBlock(this)
    }

    companion object {
        private val northShape = VoxelAssembly.createCuboidShape(2, 1, 15, 14, 15, 16)
        private val eastShape = northShape.rotateLeft()
        private val southShape = northShape.flip()
        private val westShape = northShape.rotateRight()
    }
}
