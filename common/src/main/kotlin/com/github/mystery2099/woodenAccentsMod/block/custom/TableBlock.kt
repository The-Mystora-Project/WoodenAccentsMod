package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.voxlib.combination.VoxelAssembly.appendShapes
import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.withBlockModelPath
import com.github.mystery2099.woodenAccentsMod.block.BlockStateConfigurer.Companion.with
import com.github.mystery2099.woodenAccentsMod.block.itemModelId
import com.github.mystery2099.woodenAccentsMod.block.textureId
import com.github.mystery2099.woodenAccentsMod.block.woodType
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
import com.github.mystery2099.woodenAccentsMod.block.BlockStateUtil.isIn
import com.github.mystery2099.woodenAccentsMod.util.WhenUtil
import com.github.mystery2099.woodenAccentsMod.util.WhenUtil.allOf
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.data.models.*
import net.minecraft.data.models.blockstates.*
import net.minecraft.data.models.model.*
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockBehaviour


class TableBlock(val baseBlock: Block, private val topBlock: Block) :
    AbstractWaterloggableBlock(BlockBehaviour.Properties.ofFullCopy(baseBlock)), CustomItemGroupProvider, CustomRecipeProvider,
    CustomTagProvider<Block>, CustomBlockStateProvider {
    override val itemGroup = ModItemGroup.FURNITURE

    override val tag: TagKey<Block> = ModBlockTags.tables

    override fun codec(): MapCodec<out TableBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock },
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("top_block").forGetter { it.topBlock }
        ).apply(instance, ::TableBlock)
    }

    init {
        registerDefaultState(stateDefinition.any().with {
            north to false
            east to false
            south to false
            west to false
        })
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(north, east, south, west)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return super.getStateForPlacement(ctx).withDirections(
            ctx.level.checkNorthOf(ctx.clickedPos),
            ctx.level.checkEastOf(ctx.clickedPos),
            ctx.level.checkSouthOf(ctx.clickedPos),
            ctx.level.checkWestOf(ctx.clickedPos)
        )
    }

    private fun BlockState.withDirections(north: Boolean, east: Boolean, south: Boolean, west: Boolean): BlockState {
        return this.with {
            TableBlock.north to north
            TableBlock.east to east
            TableBlock.south to south
            TableBlock.west to west
        }
    }

    private fun LevelAccessor.checkDirection(pos: BlockPos, direction: Direction): Boolean {
        return getBlockState(pos.relative(direction)) isIn tag
    }

    private infix fun LevelAccessor.checkNorthOf(pos: BlockPos): Boolean = checkDirection(pos, Direction.NORTH)

    private infix fun LevelAccessor.checkEastOf(pos: BlockPos): Boolean = checkDirection(pos, Direction.EAST)

    private infix fun LevelAccessor.checkSouthOf(pos: BlockPos): Boolean = checkDirection(pos, Direction.SOUTH)

    private infix fun LevelAccessor.checkWestOf(pos: BlockPos): Boolean = checkDirection(pos, Direction.WEST)

    @Deprecated("Deprecated in Java")
    override fun updateShape(
        state: BlockState,
        direction: Direction?,
        neighborState: BlockState?,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState = super.updateShape(state, pos, world).withDirections(
		world.checkNorthOf(pos),
		world.checkEastOf(pos),
		world.checkSouthOf(pos),
		world.checkWestOf(pos)
	)

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext
    ) = outlineShapes[connectionMask(state)]

    private fun connectionMask(state: BlockState): Int {
        var mask = 0
        if (state.getValue(north)) mask = mask or NORTH_MASK
        if (state.getValue(east)) mask = mask or EAST_MASK
        if (state.getValue(south)) mask = mask or SOUTH_MASK
        if (state.getValue(west)) mask = mask or WEST_MASK
        return mask
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 4).apply {
            define('#', topBlock)
            define('|', baseBlock)
            pattern("###")
            pattern(" | ")
            pattern(" | ")
            customGroup(this@TableBlock, "tables")
            requires(topBlock)
            save(recipeExporter)
        }
        offerWoodcuttingRecipe(recipeExporter, topBlock, this)
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        TextureMapping().apply {
            put(TextureSlot.TOP, topBlock.textureId)
            put(ModModels.legs, baseBlock.textureId)
        }.also { map ->
            generator.blockStateOutput.accept(
                blockStateModelSupplier(
                    ModModels.tableTop.create(this, map, generator.modelOutput),
                    "${woodType.name.lowercase()}_table_single_leg".toIdentifier().withBlockModelPath(),
                    "${woodType.name.lowercase()}_table_end_leg".toIdentifier().withBlockModelPath(),
                    "${woodType.name.lowercase()}_table_corner_leg".toIdentifier().withBlockModelPath(),
                )
            )
            ModModels.tableItem.create(this.itemModelId, map, generator.modelOutput)
        }
    }

    private fun blockStateModelSupplier(
        topModel: ResourceLocation,
        singleLegModel: ResourceLocation,
        endLegModel: ResourceLocation,
        cornerLegModel: ResourceLocation,
    ): MultiPartGenerator = MultiPartGenerator.multiPart(this).apply {
        val northEndLegVariant = endLegModel.asBlockStateVariant()
        val northEastCornerVariant = cornerLegModel.asBlockStateVariant()

        with(topModel.asBlockStateVariant())
        with(
            allOf(WhenUtil.notNorth, WhenUtil.notEast, WhenUtil.notSouth, WhenUtil.notWest),
            singleLegModel.asBlockStateVariant()
        )
        // EndEffects legs
        with(
            allOf(WhenUtil.notNorth, WhenUtil.notEast, WhenUtil.south, WhenUtil.notWest),
            northEndLegVariant
        )
        with(
            allOf(WhenUtil.notNorth, WhenUtil.notEast, WhenUtil.notSouth, WhenUtil.west),
            northEndLegVariant.withYRotationOf(VariantProperties.Rotation.R90)
        )
        with(
            allOf(WhenUtil.north, WhenUtil.notEast, WhenUtil.notSouth, WhenUtil.notWest),
            northEndLegVariant.withYRotationOf(VariantProperties.Rotation.R180)
        )
        with(
            allOf(WhenUtil.notNorth, WhenUtil.east, WhenUtil.notSouth, WhenUtil.notWest),
            northEndLegVariant.withYRotationOf(VariantProperties.Rotation.R270)
        )
        // VertexInfo legs
        with(
            allOf(WhenUtil.notNorth, WhenUtil.notEast, WhenUtil.south, WhenUtil.west),
            northEastCornerVariant
        )
        with(
            allOf(WhenUtil.notNorth, WhenUtil.east, WhenUtil.south, WhenUtil.notWest),
            northEastCornerVariant.withYRotationOf(VariantProperties.Rotation.R270)
        )
        with(
            allOf(WhenUtil.north, WhenUtil.notEast, WhenUtil.notSouth, WhenUtil.west),
            northEastCornerVariant.withYRotationOf(VariantProperties.Rotation.R90)
        )
        with(
            allOf(WhenUtil.north, WhenUtil.east, WhenUtil.notSouth, WhenUtil.notWest),
            northEastCornerVariant.withYRotationOf(VariantProperties.Rotation.R180)
        )
    }

    companion object {
        val north: BooleanProperty = BlockStateProperties.NORTH
        val east: BooleanProperty = BlockStateProperties.EAST
        val south: BooleanProperty = BlockStateProperties.SOUTH
        val west: BooleanProperty = BlockStateProperties.WEST

        val topShape = VoxelAssembly.createCuboidShape(0, 13, 0, 16, 16, 16)
        val singleLegShape = VoxelAssembly.createCuboidShape(6, 0, 6, 10, 13, 10)
        val northEndLegShape = VoxelAssembly.createCuboidShape(6, 0, 1, 10, 13, 5)
        val eastEndLegShape = northEndLegShape.rotateLeft()
        val southEndLegShape = northEndLegShape.flip()
        val westEndLegShape = northEndLegShape.rotateRight()
        val northEastLegShape = VoxelAssembly.createCuboidShape(11, 0, 1, 15, 13, 5)
        val northWestLegShape = northEastLegShape.rotateRight()
        val southEastLegShape = northWestLegShape.flip()
        val southWestLegShape = northEastLegShape.flip()

        private val outlineShapes = Array(1 shl 4) { mask -> shapeForMask(mask) }

        private fun shapeForMask(mask: Int) = topShape.appendShapes {
            val north = mask and NORTH_MASK != 0
            val east = mask and EAST_MASK != 0
            val south = mask and SOUTH_MASK != 0
            val west = mask and WEST_MASK != 0

            singleLegShape case (!north && !east && !south && !west)
            // EndEffects legs
            northEndLegShape case (!north && !east && south && !west)
            eastEndLegShape case (!north && !east && !south && west)
            southEndLegShape case (north && !east && !south && !west)
            westEndLegShape case (!north && east && !south && !west)

            // VertexInfo legs
            northEastLegShape case (!north && !east && south && west)
            northWestLegShape case (!north && east && south && !west)
            southEastLegShape case (north && !east && !south && west)
            southWestLegShape case (north && east && !south && !west)
        }

        private const val NORTH_MASK = 1
        private const val EAST_MASK = 1 shl 1
        private const val SOUTH_MASK = 1 shl 2
        private const val WEST_MASK = 1 shl 3
    }
}
