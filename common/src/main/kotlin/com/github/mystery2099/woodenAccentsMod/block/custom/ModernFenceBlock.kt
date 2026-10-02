package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.voxlib.combination.VoxelAssembly.plus
import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.block.BlockStateUtil.isIn
import com.github.mystery2099.woodenAccentsMod.block.itemModelId
import com.github.mystery2099.woodenAccentsMod.block.textureId
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
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockBehaviour

class ModernFenceBlock(val settingsBlock: Block, private val sideBlock: Block, private val postBlock: Block) :
    FenceBlock(Properties.ofFullCopy(settingsBlock)),
    CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider {
    override val tag: TagKey<Block> = ModBlockTags.modernFences
    override val itemGroup = ModItemGroup.BUILDING

    // FenceBlock fixes the codec type to FenceBlock, so getters receive that parent type.
    override fun codec(): MapCodec<FenceBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("settings_block").forGetter { (it as ModernFenceBlock).settingsBlock },
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("side_block").forGetter { (it as ModernFenceBlock).sideBlock },
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("post_block").forGetter { (it as ModernFenceBlock).postBlock }
        ).apply(instance, ::ModernFenceBlock)
    }

    override fun connectsTo(state: BlockState, neighborIsFullSquare: Boolean, dir: Direction): Boolean {
        return !isExceptionForConnection(state) && neighborIsFullSquare || state isIn ModBlockTags.modernFenceConnectable
    }

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState,
        world: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?
    ): VoxelShape = outlineShapes[getConnectionIndex(state)]


    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, this, 3).apply {
            define('#', postBlock)
            define('|', sideBlock)
            pattern("#|#")
            pattern("#|#")
            group("modern_fences")
            requires(postBlock)
            save(recipeExporter)
        }
        offerWoodcuttingRecipe(recipeExporter, postBlock, this)
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        TextureMapping().apply {
            put(TextureSlot.SIDE, sideBlock.textureId)
            put(TextureSlot.END, postBlock.textureId)
            put(TextureSlot.UP, TextureMapping.getBlockTexture(postBlock, "_top"))
        }.let { map ->
            ModModels.modernFenceInventory.create(itemModelId, map, generator.modelOutput)
            generator.blockStateOutput.accept(
                BlockModelGenerators.createFence(
                    this,
                    ModModels.modernFencePost.create(this, map, generator.modelOutput),
                    ModModels.modernFenceSide.create(this, map, generator.modelOutput)
                )
            )
        }
    }

    companion object {
        private val postShape: VoxelShape = VoxelAssembly.createCuboidShape(6, 0, 6, 10, 16, 10)
        private val northShape: VoxelShape = VoxelAssembly.createCuboidShape(7, 0, 0, 9, 15, 6)
        val directionToShapeMap = mapOf(
            Direction.NORTH to northShape,
            Direction.EAST to northShape.rotateLeft(),
            Direction.SOUTH to northShape.flip(),
            Direction.WEST to northShape.rotateRight()
        )

        private val outlineShapes = Array(16) { connections ->
            var shape = postShape
            if (connections and NORTH_CONNECTION != 0) shape += directionToShapeMap.getValue(Direction.NORTH)
            if (connections and EAST_CONNECTION != 0) shape += directionToShapeMap.getValue(Direction.EAST)
            if (connections and SOUTH_CONNECTION != 0) shape += directionToShapeMap.getValue(Direction.SOUTH)
            if (connections and WEST_CONNECTION != 0) shape += directionToShapeMap.getValue(Direction.WEST)
            shape
        }

        private fun getConnectionIndex(state: BlockState): Int {
            var connections = 0
            if (state.getValue(NORTH)) connections = connections or NORTH_CONNECTION
            if (state.getValue(EAST)) connections = connections or EAST_CONNECTION
            if (state.getValue(SOUTH)) connections = connections or SOUTH_CONNECTION
            if (state.getValue(WEST)) connections = connections or WEST_CONNECTION
            return connections
        }

        private const val NORTH_CONNECTION = 1
        private const val EAST_CONNECTION = 2
        private const val SOUTH_CONNECTION = 4
        private const val WEST_CONNECTION = 8
    }
}
