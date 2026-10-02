package com.github.mystery2099.woodenAccentsMod.block.custom

import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.voxlib.combination.VoxelAssembly.and
import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.block.BlockStateConfigurer.Companion.with
import com.github.mystery2099.woodenAccentsMod.block.BlockStateUtil.isIn
import com.github.mystery2099.woodenAccentsMod.block.BlockStateUtil.isOf
import com.github.mystery2099.woodenAccentsMod.block.custom.enums.SidewaysConnectionShape
import com.github.mystery2099.woodenAccentsMod.block.entity.custom.DeskDrawerBlockEntity
import com.github.mystery2099.woodenAccentsMod.block.textureId
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.asBlockStateVariant
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.withYRotationOf
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.customGroup
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockLootTableProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModItemTags
import com.github.mystery2099.woodenAccentsMod.state.property.ModProperties
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.data.models.*
import net.minecraft.data.models.blockstates.*
import net.minecraft.data.models.model.*
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.piglin.PiglinAi
import net.minecraft.world.entity.player.Player
import net.minecraft.world.Container
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.InteractionResult
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.Containers
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockBehaviour
import com.github.mystery2099.woodenAccentsMod.util.LootTableUtil
import net.minecraft.data.recipes.RecipeOutput
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.HolderLookup

class DeskDrawerBlock(private val edgeBlock: Block, val baseBlock: Block) :
    WaterloggableBlockWithEntity(BlockBehaviour.Properties.ofFullCopy(baseBlock).mapColor(baseBlock.defaultMapColor())),
    CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider,
    CustomBlockLootTableProvider {

    override val itemGroup: ModItemGroup = ModItemGroup.STORAGE
    override val tag: TagKey<Block> = ModBlockTags.deskDrawers
    private inline val BlockState.isDeskDrawer: Boolean
        get() = this isIn tag
    private inline val BlockState.isDesk: Boolean
        get() = this isIn ModBlockTags.desks

    init {
        this.registerDefaultState(this.stateDefinition.any().with { facing to Direction.NORTH }
                .withShape(left = false, right = false))
    }

    private fun BlockState.withShape(left: Boolean, right: Boolean): BlockState = this.with {
        shape to when {
            left && right -> SidewaysConnectionShape.CENTER
            left -> SidewaysConnectionShape.RIGHT
            right -> SidewaysConnectionShape.LEFT
            else -> SidewaysConnectionShape.SINGLE
        }
    }

    private fun BlockState.canConnectTo(otherState: BlockState): Boolean {
        return ((this.isDeskDrawer || this.isDesk) && (otherState.isDeskDrawer || otherState.isDesk)) && (this.getValue(facing) == otherState.getValue(facing))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(facing, shape)
    }

    private fun getAdjacentStates(world: LevelAccessor, pos: BlockPos, state: BlockState = world.getBlockState(pos)): Array<BlockState> {
        return arrayOf(
            world.getBlockState(pos.relative(state.getValue(facing).getClockWise())),
            world.getBlockState(pos.relative(state.getValue(facing).getCounterClockWise()))
        )
    }
    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val world = ctx.level
        val pos = ctx.clickedPos

        val state = super.getStateForPlacement(ctx).setValue(facing, ctx.horizontalDirection.opposite)
        val adjacentStates = getAdjacentStates(world, pos, state)
        return state.withShape(
            left = state.canConnectTo(adjacentStates[0]),
            right = state.canConnectTo(adjacentStates[1])
        )
    }

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if (world.isClientSide) return InteractionResult.SUCCESS
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is DeskDrawerBlockEntity) {
            player.openMenu(blockEntity)
            PiglinAi.angerNearbyPiglins(player, true)
        }
        return InteractionResult.CONSUME
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onRemove(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        if (state isOf newState.block) return

        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is Container) {
            Containers.dropContents(world, pos, blockEntity)
            world.updateNeighbourForOutputSignal(pos, this)
        }

        super.onRemove(state, world, pos, newState, moved)
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
        val adjacentStates = getAdjacentStates(world, pos)
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos)
            .withShape(
                left = state.canConnectTo(adjacentStates[0]),
                right = state.canConnectTo(adjacentStates[1])
            )
    }

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState,
        world: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?
    ): VoxelShape = shapeMap[state.getValue(shape)]?.get(state.getValue(facing)) ?: Shapes.block()

    @Deprecated("Deprecated in Java")
    override fun getCollisionShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = Shapes.block()

    @Deprecated("Deprecated in Java", ReplaceWith("RenderShape.MODEL", "net.minecraft.world.level.block.RenderShape"))
    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    @Deprecated("Deprecated in Java", ReplaceWith("true"))
    override fun hasAnalogOutputSignal(state: BlockState) = true

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos))",
            "net.minecraft.world.inventory.AbstractContainerMenu"
        )
    )
    override fun getAnalogOutputSignal(state: BlockState, world: Level, pos: BlockPos): Int {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos))
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "state.setValue(facing, rotation.rotate(state.getValue(facing)))",
            "com.mystery2099.wooden_accents_mod.block.custom.DeskDrawerBlock.Companion.facing",
            "com.mystery2099.wooden_accents_mod.block.custom.DeskDrawerBlock.Companion.facing"
        )
    )
    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(facing, rotation.rotate(state.getValue(facing)))
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "state.rotate(mirror.getRotation(state.getValue(facing)))",
            "com.mystery2099.wooden_accents_mod.block.custom.DeskDrawerBlock.Companion.facing"
        )
    )
    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.rotate(mirror.getRotation(state.getValue(facing)))
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = DeskDrawerBlockEntity(pos, state)

    override fun codec(): MapCodec<DeskDrawerBlock> = CODEC

    override fun getLootTableBuilder(registries: HolderLookup.Provider): LootTable.Builder {
        return LootTable.lootTable().withPool(
            LootTableUtil.applyExplosionCondition(
                this,
                LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add(
                    LootItem.lootTableItem(this)
                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                )
            )
        )
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val textureMap = TextureMapping().apply {
            put(TextureSlot.SIDE, baseBlock.textureId)
            put(TextureSlot.EDGE, edgeBlock.textureId)
        }

        val singleModel = ModModels.deskDrawer.create(this, textureMap, generator.modelOutput)
        val leftModel = ModModels.deskDrawerLeft.create(this, textureMap, generator.modelOutput)
        val centerModel = ModModels.deskDrawerCenter.create(this, textureMap, generator.modelOutput)
        val rightModel = ModModels.deskDrawerRight.create(this, textureMap, generator.modelOutput)

        generator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(this).with(
                PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, ModProperties.sidewaysConnectionShape).apply {
                    val northSingleVariant = singleModel.asBlockStateVariant()
                    val northLeftVariant = leftModel.asBlockStateVariant()
                    val northCenterVariant = centerModel.asBlockStateVariant()
                    val northRightVariant = rightModel.asBlockStateVariant()

                    select(Direction.NORTH, SidewaysConnectionShape.SINGLE, northSingleVariant)
                    select(Direction.NORTH, SidewaysConnectionShape.LEFT, northLeftVariant)
                    select(Direction.NORTH, SidewaysConnectionShape.CENTER, northCenterVariant)
                    select(Direction.NORTH, SidewaysConnectionShape.RIGHT, northRightVariant)

                    select(
                        Direction.EAST, SidewaysConnectionShape.SINGLE, northSingleVariant.withYRotationOf(
                            VariantProperties.Rotation.R90
                        )
                    )
                    select(
                        Direction.EAST, SidewaysConnectionShape.LEFT, northLeftVariant.withYRotationOf(
                            VariantProperties.Rotation.R90
                        )
                    )
                    select(
                        Direction.EAST, SidewaysConnectionShape.CENTER, northCenterVariant.withYRotationOf(
                            VariantProperties.Rotation.R90
                        )
                    )
                    select(
                        Direction.EAST, SidewaysConnectionShape.RIGHT, northRightVariant.withYRotationOf(
                            VariantProperties.Rotation.R90
                        )
                    )

                    select(
                        Direction.SOUTH, SidewaysConnectionShape.SINGLE, northSingleVariant.withYRotationOf(
                            VariantProperties.Rotation.R180
                        )
                    )
                    select(
                        Direction.SOUTH, SidewaysConnectionShape.LEFT, northLeftVariant.withYRotationOf(
                            VariantProperties.Rotation.R180
                        )
                    )
                    select(
                        Direction.SOUTH, SidewaysConnectionShape.CENTER, northCenterVariant.withYRotationOf(
                            VariantProperties.Rotation.R180
                        )
                    )
                    select(
                        Direction.SOUTH, SidewaysConnectionShape.RIGHT, northRightVariant.withYRotationOf(
                            VariantProperties.Rotation.R180
                        )
                    )

                    select(
                        Direction.WEST, SidewaysConnectionShape.SINGLE, northSingleVariant.withYRotationOf(
                            VariantProperties.Rotation.R270
                        )
                    )
                    select(
                        Direction.WEST, SidewaysConnectionShape.LEFT, northLeftVariant.withYRotationOf(
                            VariantProperties.Rotation.R270
                        )
                    )
                    select(
                        Direction.WEST, SidewaysConnectionShape.CENTER, northCenterVariant.withYRotationOf(
                            VariantProperties.Rotation.R270
                        )
                    )
                    select(
                        Direction.WEST, SidewaysConnectionShape.RIGHT, northRightVariant.withYRotationOf(
                            VariantProperties.Rotation.R270
                        )
                    )

                }
            )
        )
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 4).apply {
            define('|', edgeBlock)
            define('_', baseBlock)
            define('#', ModItemTags.woodenChests)
            pattern("___")
            pattern("|#|")
            pattern("| |")
            customGroup(this@DeskDrawerBlock, "desk_drawers")
            requires(ModBlockTags.getItemTagFrom(ModBlockTags.desks))
            save(recipeExporter)
        }
    }

    companion object {
        val facing: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        val shape: EnumProperty<SidewaysConnectionShape> = ModProperties.sidewaysConnectionShape

        val CODEC: MapCodec<DeskDrawerBlock> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("edge_block").forGetter { it.edgeBlock },
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock }
            ).apply(instance, ::DeskDrawerBlock)
        }
        private val northShape = VoxelAssembly.createCuboidShape(1, 0, 1, 15, 15, 16)
        private val northSingleShape = VoxelAssembly.createCuboidShape(
            1, 0, 1, 15, 15, 15
        ) and VoxelAssembly.createCuboidShape(
            0, 15, 0, 16, 16, 16
        )

        private val northCenterShape = northShape and DeskBlock.northCenterShape
        private val northLeftShape = northShape and DeskBlock.northLeftShape
        private val northRightShape = northShape and DeskBlock.northRightShape

        private val singleShapeMap = mapOf(
            Direction.NORTH to northSingleShape,
            Direction.EAST to northSingleShape.rotateLeft(),
            Direction.SOUTH to northSingleShape.flip(),
            Direction.WEST to northSingleShape.rotateRight()
        )
        private val centerShapeMap = mapOf(
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
        private val shapeMap = mapOf(
            SidewaysConnectionShape.SINGLE to singleShapeMap,
            SidewaysConnectionShape.CENTER to centerShapeMap,
            SidewaysConnectionShape.LEFT to leftShapeMap,
            SidewaysConnectionShape.RIGHT to rightShapeMap,
        )
    }
}
