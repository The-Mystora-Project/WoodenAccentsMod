package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.block.BlockStateConfigurer.Companion.with
import com.github.mystery2099.woodenAccentsMod.block.BlockStateUtil.isIn
import com.github.mystery2099.woodenAccentsMod.block.custom.enums.DeskShape
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
import com.github.mystery2099.woodenAccentsMod.state.property.ModProperties
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
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockBehaviour

class DeskBlock(val baseBlock: Block, private val topBlock: Block) :
    AbstractWaterloggableBlock(BlockBehaviour.Properties.ofFullCopy(topBlock).mapColor(topBlock.defaultMapColor())),
    CustomItemGroupProvider, CustomRecipeProvider, CustomBlockStateProvider, CustomTagProvider<Block> {


    override val itemGroup: ModItemGroup = ModItemGroup.FURNITURE
    override val tag: TagKey<Block> = ModBlockTags.desks

    override fun codec(): MapCodec<out DeskBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock },
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("top_block").forGetter { it.topBlock }
        ).apply(instance, ::DeskBlock)
    }
    private val BlockState.isDesk: Boolean
        get() = this isIn tag
    private val BlockState.isDeskDrawer: Boolean
        get() = this isIn ModBlockTags.deskDrawers

    init {
        registerDefaultState(defaultBlockState().with {
            facing to Direction.NORTH
            shape to DeskShape.SINGLE
        })
    }

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState,
        world: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?
    ): VoxelShape =
        shapeMap[state.getValue(shape)]?.get(state.getValue(facing)) ?: Shapes.block()

    @Deprecated("Deprecated in Java")
    override fun getCollisionShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = Shapes.block()

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(facing, shape)
    }

    private fun BlockState.canConnectTo(otherState: BlockState): Boolean {
        return (this.isDesk || this.isDeskDrawer) && (otherState.isDesk || otherState.isDeskDrawer)
    }
    private fun checkNeighbors(world: LevelAccessor, pos: BlockPos, state: BlockState = world.getBlockState(pos)): Array<Boolean> {
        val leftState = world.getBlockState(pos.relative(state.getValue(facing).getClockWise()))
        val rightState = world.getBlockState(pos.relative(state.getValue(facing).getCounterClockWise()))
        val left = state.canConnectTo(leftState)
        val right = state.canConnectTo(rightState)
        val forward = world.getBlockState(pos.relative(state.getValue(facing))).let {
            (state.canConnectTo(it)) &&
                    if (left) it.getValue(facing) == state.getValue(facing).getClockWise()
                    else if (right) it.getValue(facing) == state.getValue(facing).getCounterClockWise()
                    else true
        }
        return arrayOf(left, right, forward)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return super.getStateForPlacement(ctx).setValue(facing, ctx.horizontalDirection.opposite).let {
            val checkedNeighbors = checkNeighbors(ctx.level, ctx.clickedPos, it)
            it.withShape(
                left = checkedNeighbors[0],
                right = checkedNeighbors[1],
                forward = checkedNeighbors[2]
            )
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun updateShape(
        state: BlockState,
        direction: Direction?,
        neighborState: BlockState?,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        val checkedNeighbors = checkNeighbors(world, pos)
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos)
            .withShape(
                left = checkedNeighbors[0],
                right = checkedNeighbors[1],
                forward = checkedNeighbors[2]
            )
    }

    private fun BlockState.withShape(left: Boolean, right: Boolean, forward: Boolean = false): BlockState {
        return this.with {
            shape to when {
                left && right -> DeskShape.CENTER
                left && forward -> DeskShape.RIGHT_CORNER
                right && forward -> DeskShape.LEFT_CORNER
                left -> DeskShape.RIGHT
                right -> DeskShape.LEFT
                else -> DeskShape.SINGLE
            }
        }
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val block = this
        TextureMapping().apply {
            put(TextureSlot.TOP, block.topBlock.textureId)
            put(TextureSlot.SIDE, block.baseBlock.textureId)
        }.let { map ->
            generator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(block)
                    .with(
                        variantMap(
                            singleModel = ModModels.desk.create(block, map, generator.modelOutput),
                            leftModel = ModModels.deskLeft.create(block, map, generator.modelOutput),
                            centerModel = ModModels.deskCenter.create(block, map, generator.modelOutput),
                            rightModel = ModModels.deskRight.create(block, map, generator.modelOutput),
                            leftCornerModel = ModModels.deskLeftCorner.create(block, map, generator.modelOutput)
                        )
                    )
            )
        }
    }

    private fun variantMap(
        singleModel: ResourceLocation,
        leftModel: ResourceLocation,
        centerModel: ResourceLocation,
        rightModel: ResourceLocation,
        leftCornerModel: ResourceLocation
    ) = PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, ModProperties.deskShape).apply {
        val northSingle = singleModel.asBlockStateVariant()
        val northLeft = leftModel.asBlockStateVariant()
        val northCenter = centerModel.asBlockStateVariant()
        val northRight = rightModel.asBlockStateVariant()
        val northLeftCorner = leftCornerModel.asBlockStateVariant()

        mapOf(
            Direction.NORTH to mapOf(
                DeskShape.SINGLE to northSingle,
                DeskShape.LEFT to northLeft,
                DeskShape.CENTER to northCenter,
                DeskShape.RIGHT to northRight,
                DeskShape.LEFT_CORNER to northLeftCorner,
                DeskShape.RIGHT_CORNER to northLeftCorner.withYRotationOf(VariantProperties.Rotation.R90)
            ),
            Direction.EAST to mapOf(
                DeskShape.SINGLE to northSingle.withYRotationOf(VariantProperties.Rotation.R90),
                DeskShape.LEFT to northLeft.withYRotationOf(VariantProperties.Rotation.R90),
                DeskShape.CENTER to northCenter.withYRotationOf(VariantProperties.Rotation.R90),
                DeskShape.RIGHT to northRight.withYRotationOf(VariantProperties.Rotation.R90),
                DeskShape.LEFT_CORNER to northLeftCorner.withYRotationOf(VariantProperties.Rotation.R90),
                DeskShape.RIGHT_CORNER to northLeftCorner.withYRotationOf(VariantProperties.Rotation.R180)
            ),
            Direction.SOUTH to mapOf(
                DeskShape.SINGLE to northSingle.withYRotationOf(VariantProperties.Rotation.R180),
                DeskShape.LEFT to northLeft.withYRotationOf(VariantProperties.Rotation.R180),
                DeskShape.CENTER to northCenter.withYRotationOf(VariantProperties.Rotation.R180),
                DeskShape.RIGHT to northRight.withYRotationOf(VariantProperties.Rotation.R180),
                DeskShape.LEFT_CORNER to northLeftCorner.withYRotationOf(VariantProperties.Rotation.R180),
                DeskShape.RIGHT_CORNER to northLeftCorner.withYRotationOf(VariantProperties.Rotation.R270)
            ),
            Direction.WEST to mapOf(
                DeskShape.SINGLE to northSingle.withYRotationOf(VariantProperties.Rotation.R270),
                DeskShape.LEFT to northLeft.withYRotationOf(VariantProperties.Rotation.R270),
                DeskShape.CENTER to northCenter.withYRotationOf(VariantProperties.Rotation.R270),
                DeskShape.RIGHT to northRight.withYRotationOf(VariantProperties.Rotation.R270),
                DeskShape.LEFT_CORNER to northLeftCorner.withYRotationOf(VariantProperties.Rotation.R270),
                DeskShape.RIGHT_CORNER to northLeftCorner
            )
        ).forEach { i -> i.value.forEach { j -> select(i.key, j.key, j.value) } }
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 4).apply {
            define('|', baseBlock)
            define('_', topBlock)
            pattern("___")
            pattern("| |")
            pattern("| |")
            customGroup(this@DeskBlock, "desks")
            requires(baseBlock)
            save(recipeExporter)
        }
        offerWoodcuttingRecipe(recipeExporter, topBlock, this)
    }

    companion object {
        val shape = ModProperties.deskShape
        val facing: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING

        // Shapes are authored facing north, then rotated for the other directions.
        private val northSingleShape = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(1, 15, 0, 15, 16, 16),
            VoxelAssembly.createCuboidShape(1, 8, 15, 15, 15, 16),
            VoxelAssembly.createCuboidShape(15, 0, 0, 16, 16, 2),
            VoxelAssembly.createCuboidShape(15, 14, 2, 16, 16, 14),
            VoxelAssembly.createCuboidShape(15, 0, 14, 16, 16, 16),
            VoxelAssembly.createCuboidShape(0, 0, 0, 1, 16, 2),
            VoxelAssembly.createCuboidShape(0, 14, 2, 1, 16, 14),
            VoxelAssembly.createCuboidShape(0, 0, 14, 1, 16, 16)
        )

        val northLeftShape = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(0, 15, 0, 15, 16, 16),
            VoxelAssembly.createCuboidShape(0, 8, 15, 15, 15, 16),
            VoxelAssembly.createCuboidShape(15, 0, 0, 16, 16, 2),
            VoxelAssembly.createCuboidShape(15, 14, 2, 16, 16, 14),
            VoxelAssembly.createCuboidShape(15, 0, 14, 16, 16, 16)
        )

        val northCenterShape = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(0, 15, 0, 16, 16, 16),
            VoxelAssembly.createCuboidShape(0, 8, 15, 16, 15, 16)
        )

        val northRightShape = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(1, 15, 0, 16, 16, 16),
            VoxelAssembly.createCuboidShape(1, 8, 15, 16, 15, 16),
            VoxelAssembly.createCuboidShape(0, 0, 0, 1, 16, 2),
            VoxelAssembly.createCuboidShape(0, 14, 2, 1, 16, 14),
            VoxelAssembly.createCuboidShape(0, 0, 14, 1, 16, 16)
        )

        private val northLeftCornerShape = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(15, 0, 14, 16, 15, 16),
            VoxelAssembly.createCuboidShape(14, 0, 15, 15, 15, 16),
            VoxelAssembly.createCuboidShape(0, 8, 15, 14, 15, 16),
            VoxelAssembly.createCuboidShape(15, 8, 0, 16, 15, 14),
            VoxelAssembly.createCuboidShape(0, 15, 0, 16, 16, 16)
        )
        private val northRightCornerShape = northLeftCornerShape.rotateLeft()

        private val singleShapeMap = mapOf(
            Direction.NORTH to northSingleShape,
            Direction.EAST to northSingleShape.rotateLeft(),
            Direction.SOUTH to northSingleShape.flip(),
            Direction.WEST to northSingleShape.rotateRight()
        )
        private val centerShapeMap =
            mapOf(
                Direction.NORTH to northCenterShape,
                Direction.EAST to northCenterShape.rotateLeft(),
                Direction.SOUTH to northCenterShape.flip(),
                Direction.WEST to northCenterShape.rotateRight()
            )
        private val leftShapeMap = mapOf(
            Direction.NORTH to northLeftShape,
            Direction.EAST to northLeftShape.rotateLeft(),
            Direction.SOUTH to northLeftShape.flip(),
            Direction.WEST to northLeftShape.rotateRight()
        )
        private val rightShapeMap = mapOf(
            Direction.NORTH to northRightShape,
            Direction.EAST to northRightShape.rotateLeft(),
            Direction.SOUTH to northRightShape.flip(),
            Direction.WEST to northRightShape.rotateRight()
        )
        private val leftCornerShapeMap = mapOf(
            Direction.NORTH to northLeftCornerShape,
            Direction.EAST to northLeftCornerShape.rotateLeft(),
            Direction.SOUTH to northLeftCornerShape.flip(),
            Direction.WEST to northLeftCornerShape.rotateRight()
        )
        private val rightCornerShapeMap = mapOf(
            Direction.NORTH to northRightCornerShape,
            Direction.EAST to northRightCornerShape.rotateLeft(),
            Direction.SOUTH to northRightCornerShape.flip(),
            Direction.WEST to northRightCornerShape.rotateRight()
        )
        private val shapeMap = mapOf(
            DeskShape.SINGLE to singleShapeMap,
            DeskShape.CENTER to centerShapeMap,
            DeskShape.LEFT to leftShapeMap,
            DeskShape.RIGHT to rightShapeMap,
            DeskShape.LEFT_CORNER to leftCornerShapeMap,
            DeskShape.RIGHT_CORNER to rightCornerShapeMap
        )
    }
}
