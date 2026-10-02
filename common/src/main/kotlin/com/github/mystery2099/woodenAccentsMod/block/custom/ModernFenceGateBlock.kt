package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.woodenAccentsMod.block.woodType
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.FenceGateBlock
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

class ModernFenceGateBlock(val baseGate: FenceGateBlock, val baseBlock: Block
) : FenceGateBlock(baseGate.woodType, Properties.ofFullCopy(baseGate)),
    CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider {
    override val tag: TagKey<Block> = ModBlockTags.modernFenceGates
    override val itemGroup = ModItemGroup.BUILDING

    // FenceGateBlock fixes the codec type to FenceGateBlock, so getters receive that parent type.
    override fun codec(): MapCodec<FenceGateBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().comapFlatMap(
                { if (it is FenceGateBlock) DataResult.success(it) else DataResult.error { "Expected a fence gate block" } },
                { it }
            ).fieldOf("base_gate").forGetter { (it as ModernFenceGateBlock).baseGate },
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { (it as ModernFenceGateBlock).baseBlock }
        ).apply(instance, ::ModernFenceGateBlock)
    }

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState,
        world: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?
    ): VoxelShape = when (state.getValue(FACING)) {
        Direction.NORTH, Direction.SOUTH -> if (state.getValue(IN_WALL)) wallShape1 else shape1
        Direction.EAST, Direction.WEST -> if (state.getValue(IN_WALL)) wallShape2 else shape2
        else -> super.getShape(state, world, pos, context)
    }


    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, this).apply {
            define('#', baseBlock)
            define('|', Items.STICK)
            pattern("|#|")
            pattern("|#|")
            group("modern_fence_gates")
            requires(baseBlock)
            save(recipeExporter)
        }
        offerWoodcuttingRecipe(recipeExporter, baseBlock, this)
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        TextureMapping.cube(baseBlock).let { map ->
            val model = ModModels.modernFenceGate.create(this, map, generator.modelOutput)
            val openModel = ModModels.modernFenceGateOpen.create(this, map, generator.modelOutput)
            val wallModel = ModModels.modernFenceGateWall.create(this, map, generator.modelOutput)
            val openWallModel = ModModels.modernFenceGateWallOpen.create(this, map, generator.modelOutput)
            generator.blockStateOutput.accept(BlockModelGenerators.createFenceGate(
                this,
                openModel,
                model,
                openWallModel,
                wallModel,
                false
            ))
        }
    }

    companion object {
        private val shape1 = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(3, 15, 7, 5, 16, 9),
            VoxelAssembly.createCuboidShape(11, 15, 7, 13, 16, 9),
            VoxelAssembly.createCuboidShape(0, 0, 7, 1, 15, 9),
            VoxelAssembly.createCuboidShape(15, 0, 7, 16, 15, 9),
            VoxelAssembly.createCuboidShape(1, 1, 7, 15, 15, 9)
        )
        private val shape2 = shape1.rotateLeft()
        private val wallShape1 = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(15, 0, 7, 16, 1, 9),
            VoxelAssembly.createCuboidShape(0, 0, 7, 1, 1, 9),
            VoxelAssembly.createCuboidShape(3, 15, 7, 5, 16, 9),
            VoxelAssembly.createCuboidShape(11, 15, 7, 13, 16, 9),
            VoxelAssembly.createCuboidShape(3, 14, 7, 13, 15, 9),
            VoxelAssembly.createCuboidShape(0, 1, 7, 16, 14, 9)
        )
        private val wallShape2 = wallShape1.rotateLeft()
    }
}
