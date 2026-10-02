package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.block.BlockStateConfigurer.Companion.with
import com.github.mystery2099.woodenAccentsMod.block.entity.custom.BracketShelfBlockEntity
import com.github.mystery2099.woodenAccentsMod.block.itemModelId
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.asBlockStateVariant
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.withYRotationOf
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.customGroup
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.blockstates.MultiPartGenerator
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.blockstates.Condition
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.Items
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.Containers
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.block.state.BlockBehaviour

class BracketShelfBlock(val baseBlock: Block) : AbstractWaterloggableBlock(
    Properties.ofFullCopy(baseBlock).noOcclusion()), EntityBlock, CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider {

    override val itemGroup = ModItemGroup.STORAGE
    override val tag: TagKey<Block> = ModBlockTags.bracketShelves

    init {
        registerDefaultState(defaultBlockState().with {
            facing to Direction.NORTH
            left to false
            right to false
        })
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(facing, left, right)
    }

    override fun codec(): MapCodec<out BracketShelfBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock }
        ).apply(instance, ::BracketShelfBlock)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val placementFacing = if (ctx.clickedFace.axis.isHorizontal) ctx.clickedFace else ctx.horizontalDirection.opposite
        return super.getStateForPlacement(ctx).setValue(facing, placementFacing).withBracketVisibility(ctx.level, ctx.clickedPos)
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
        val updated = super.updateShape(state, direction, neighborState, world, pos, neighborPos)
        // Drop the shelf when its support disappears. onRemove scatters its contents.
        if (direction == updated.getValue(facing).opposite && !updated.canSurvive(world, pos)) {
            return Blocks.AIR.defaultBlockState()
        }
        if (direction?.axis?.isHorizontal == true) {
            world.scheduleTick(pos, this, 1)
        }
        return updated
    }

    private fun BlockState.withBracketVisibility(world: LevelAccessor, pos: BlockPos): BlockState {
        val leftDirection = this.getValue(facing).getCounterClockWise()
        var distanceFromLeft = 0
        var cursor = pos.relative(leftDirection)
        while (canConnect(world.getBlockState(cursor))) {
            distanceFromLeft++
            cursor = cursor.relative(leftDirection)
        }
        return withBracketVisibility(world, pos, distanceFromLeft)
    }

    private fun BlockState.withBracketVisibility(
        world: LevelAccessor,
        pos: BlockPos,
        distanceFromLeft: Int
    ): BlockState {
        val rightDirection = this.getValue(facing).getClockWise()
        val hasRightNeighbor = canConnect(world.getBlockState(pos.relative(rightDirection)))
        return this.with {
            left to (distanceFromLeft > 0)
            right to (hasRightNeighbor && (distanceFromLeft + 1) % SHELVES_PER_SECTION != 0)
        }
    }

    private fun BlockState.canConnect(other: BlockState): Boolean =
        other.block === this.block && other.getValue(facing) == this.getValue(facing)

    @Deprecated("Deprecated in Java")
    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val leftDirection = state.getValue(facing).getCounterClockWise()
        val rightDirection = state.getValue(facing).getClockWise()
        var start = pos
        while (state.canConnect(world.getBlockState(start.relative(leftDirection)))) {
            start = start.relative(leftDirection)
        }

        var cursor = start
        var distanceFromLeft = 0
        while (state.canConnect(world.getBlockState(cursor))) {
            val currentState = world.getBlockState(cursor)
            val updatedState = currentState.withBracketVisibility(world, cursor, distanceFromLeft)
            if (updatedState != currentState) {
                world.setBlock(cursor, updatedState, Block.UPDATE_CLIENTS)
            }
            cursor = cursor.relative(rightDirection)
            distanceFromLeft++
        }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        BracketShelfBlockEntity(pos, state)

    /**
     * Drains water with a bucket. Otherwise, swaps with the selected slot or, when powered, with the hotbar.
     */
    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): ItemInteractionResult {
        if (world.isClientSide) return ItemInteractionResult.SUCCESS

        // Handle the bucket before swapping items so it can drain the shelf.
        if (state.getValue(waterlogged) && stack.`is`(Items.BUCKET)) {
            player.setItemInHand(
                hand,
                ItemUtils.createFilledResult(stack, player, ItemStack(Items.WATER_BUCKET))
            )
            world.setBlockAndUpdate(pos, state.setValue(waterlogged, false))
            world.playSound(
                null,
                pos,
                SoundEvents.BUCKET_FILL,
                SoundSource.BLOCKS,
                1.0f,
                1.0f
            )
            world.gameEvent(player, GameEvent.FLUID_PICKUP, pos)
            return ItemInteractionResult.CONSUME
        }

        val blockEntity = world.getBlockEntity(pos) as? BracketShelfBlockEntity
            ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

        if (world.hasNeighborSignal(pos)) {
            swapWithHotbar(world, pos, state.getValue(facing), player)
        } else {
            val slot = slotFromHit(hit, pos, state.getValue(facing))
            val shelfStack = blockEntity.getItem(slot)
            blockEntity.setItem(slot, stack.copy())
            player.setItemInHand(hand, shelfStack)
            playInteractionSound(world, pos, shelfStack, stack)
        }
        return ItemInteractionResult.CONSUME
    }

    /** Takes the selected item, or swaps with the hotbar when powered. */
    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if (world.isClientSide) return InteractionResult.SUCCESS

        val blockEntity = world.getBlockEntity(pos) as? BracketShelfBlockEntity
            ?: return InteractionResult.PASS

        if (world.hasNeighborSignal(pos)) {
            swapWithHotbar(world, pos, state.getValue(facing), player)
        } else {
            val slot = slotFromHit(hit, pos, state.getValue(facing))
            val shelfStack = blockEntity.getItem(slot)
            blockEntity.setItem(slot, ItemStack.EMPTY)
            player.setItemInHand(InteractionHand.MAIN_HAND, shelfStack)
            playInteractionSound(world, pos, shelfStack, ItemStack.EMPTY)
        }
        return InteractionResult.CONSUME
    }

    /** Finds the clicked slot, numbered left to right from the front of the shelf. */
    private fun slotFromHit(hit: BlockHitResult, pos: BlockPos, facing: Direction): Int {
        val leftDirection = facing.getClockWise()
        val hitPos = hit.location
        val localX = hitPos.x - pos.x
        val localZ = hitPos.z - pos.z
        val alongLeft = if (leftDirection.axis == Direction.Axis.X) {
            if (leftDirection.axisDirection == Direction.AxisDirection.POSITIVE) localX else 1.0 - localX
        } else {
            if (leftDirection.axisDirection == Direction.AxisDirection.POSITIVE) localZ else 1.0 - localZ
        }
        // alongLeft is 1.0 at the viewer's left edge, where the first slot starts.
        return ((1.0 - alongLeft) * 3.0).toInt().coerceIn(0, BracketShelfBlockEntity.SLOT_COUNT - 1)
    }

    /** Swaps up to three connected, powered shelves facing [facing] with the rightmost hotbar slots. */
    private fun swapWithHotbar(world: Level, clickedPos: BlockPos, facing: Direction, player: Player) {
        val leftDirection = facing.getClockWise()
        val rightDirection = facing.getCounterClockWise()
        val shelfBlock = world.getBlockState(clickedPos).block

        val leftEnd = generateSequence(clickedPos, { it.relative(leftDirection) })
            .takeWhile { isPoweredShelf(it, world, shelfBlock, facing) }
            .take(3)
            .last()
        val group = generateSequence(leftEnd, { it.relative(rightDirection) })
            .takeWhile { isPoweredShelf(it, world, shelfBlock, facing) }
            .take(3)
            .toList()

        val firstHotbarSlot = Inventory.getSelectionSize() - group.size * BracketShelfBlockEntity.SLOT_COUNT
        group.forEachIndexed { groupIndex, shelfPos ->
            val shelf = world.getBlockEntity(shelfPos) as? BracketShelfBlockEntity ?: return@forEachIndexed
            repeat(BracketShelfBlockEntity.SLOT_COUNT) { slot ->
                val hotbarSlot = firstHotbarSlot + groupIndex * BracketShelfBlockEntity.SLOT_COUNT + slot
                val shelfStack = shelf.getItem(slot)
                shelf.setItem(slot, player.inventory.getItem(hotbarSlot))
                player.inventory.setItem(hotbarSlot, shelfStack)
            }
        }
        player.inventory.setChanged()
        world.playSound(
            null,
            clickedPos,
            SoundEvents.DISPENSER_DISPENSE,
            SoundSource.BLOCKS,
            0.8f,
            1.0f
        )
    }

    private fun isPoweredShelf(pos: BlockPos, world: Level, shelfBlock: Block, shelfFacing: Direction): Boolean {
        val state = world.getBlockState(pos)
        return state.block === shelfBlock &&
            state.getValue(facing) == shelfFacing &&
            world.hasNeighborSignal(pos)
    }

    private fun playInteractionSound(world: Level, pos: BlockPos, shelfStack: ItemStack, handStack: ItemStack) {
        val sound = when {
            shelfStack.isEmpty && handStack.isEmpty -> return
            shelfStack.isEmpty -> SoundEvents.ITEM_FRAME_ADD_ITEM
            handStack.isEmpty -> SoundEvents.ITEM_FRAME_REMOVE_ITEM
            else -> SoundEvents.ITEM_FRAME_ADD_ITEM
        }
        world.playSound(null, pos, sound, SoundSource.BLOCKS, 0.5f, 0.9f + world.random.nextFloat() * 0.2f)
    }

    @Deprecated("Deprecated in Java")
    override fun canSurvive(state: BlockState, world: LevelReader, pos: BlockPos): Boolean =
        world.getBlockState(pos.relative(state.getValue(facing).opposite)).isFaceSturdy(
            world,
            pos.relative(state.getValue(facing).opposite),
            state.getValue(facing)
        )

    @Deprecated("Deprecated in Java")
    override fun hasAnalogOutputSignal(state: BlockState): Boolean = true

    /** Left, middle, and right slots contribute 1, 2, and 4 respectively. */
    @Deprecated("Deprecated in Java")
    override fun getAnalogOutputSignal(state: BlockState, world: Level, pos: BlockPos): Int {
        val shelf = world.getBlockEntity(pos) as? BracketShelfBlockEntity ?: return 0
        var signal = 0
        repeat(BracketShelfBlockEntity.SLOT_COUNT) { slot ->
            if (!shelf.getItem(slot).isEmpty) signal = signal or (1 shl slot)
        }
        return signal
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
        if (!state.`is`(newState.block) && !world.isClientSide) {
            // Covers both player breaks and support-loss pop-offs.
            world.getBlockEntity(pos)?.let { if (it is BracketShelfBlockEntity) Containers.dropContents(world, pos, it) }
            world.updateNeighbourForOutputSignal(pos, state.block)
        }
        super.onRemove(state, world, pos, newState, moved)
    }

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        val direction = state.getValue(facing)
        var shape = shelfShapeByFacing.getValue(direction)
        if (!state.getValue(left)) shape = Shapes.or(shape, leftBracketShapeByFacing.getValue(direction))
        if (!state.getValue(right)) shape = Shapes.or(shape, rightBracketShapeByFacing.getValue(direction))
        return shape
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 2).apply {
            define('#', baseBlock)
            define('|', Items.STICK)
            pattern("###")
            pattern("| |")
            customGroup(this@BracketShelfBlock, "bracket_shelves")
            requires(baseBlock)
            save(recipeExporter)
        }
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val textures = TextureMapping.cube(baseBlock)
        val baseModel = ModModels.bracketShelfBase.create(this, textures, generator.modelOutput)
        val leftBracketModel = ModModels.bracketShelfLeft.create(this, textures, generator.modelOutput)
        val rightBracketModel = ModModels.bracketShelfRight.create(this, textures, generator.modelOutput)
        ModModels.bracketShelfItem.create(itemModelId, textures, generator.modelOutput)

        val supplier = MultiPartGenerator.multiPart(this)
        val rotations = mapOf(
            Direction.NORTH to VariantProperties.Rotation.R0,
            Direction.EAST to VariantProperties.Rotation.R90,
            Direction.SOUTH to VariantProperties.Rotation.R180,
            Direction.WEST to VariantProperties.Rotation.R270
        )
        rotations.forEach { (direction, rotation) ->
            val facingCondition = Condition.condition().term(facing, direction)
            supplier.with(facingCondition, baseModel.asBlockStateVariant().withYRotationOf(rotation))
            supplier.with(
                Condition.and(facingCondition, Condition.condition().term(left, false)),
                leftBracketModel.asBlockStateVariant().withYRotationOf(rotation)
            )
            supplier.with(
                Condition.and(facingCondition, Condition.condition().term(right, false)),
                rightBracketModel.asBlockStateVariant().withYRotationOf(rotation)
            )
        }
        generator.blockStateOutput.accept(supplier)
    }

    companion object {
        private const val SHELVES_PER_SECTION = 3

        val facing = BlockStateProperties.HORIZONTAL_FACING
        val left: BooleanProperty = com.github.mystery2099.woodenAccentsMod.state.property.ModProperties.left
        val right: BooleanProperty = com.github.mystery2099.woodenAccentsMod.state.property.ModProperties.right

        private val northShelfShape = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(0, 11, 6, 16, 13, 16),
            VoxelAssembly.createCuboidShape(0, 13, 15, 16, 15, 16)
        )
        private val northLeftBracketShape = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(2, 3, 14, 4, 11, 16),
            VoxelAssembly.createCuboidShape(2, 9, 7, 4, 11, 14),
            VoxelAssembly.createCuboidShape(2, 7, 9, 4, 9, 14),
            VoxelAssembly.createCuboidShape(2, 5, 11, 4, 7, 14)
        )
        private val northRightBracketShape = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(12, 3, 14, 14, 11, 16),
            VoxelAssembly.createCuboidShape(12, 9, 7, 14, 11, 14),
            VoxelAssembly.createCuboidShape(12, 7, 9, 14, 9, 14),
            VoxelAssembly.createCuboidShape(12, 5, 11, 14, 7, 14)
        )

        private val shelfShapeByFacing = shapesByFacing(northShelfShape)
        private val leftBracketShapeByFacing = shapesByFacing(northLeftBracketShape)
        private val rightBracketShapeByFacing = shapesByFacing(northRightBracketShape)

        private fun shapesByFacing(northShape: VoxelShape) = mapOf(
            Direction.NORTH to northShape,
            Direction.EAST to northShape.rotateLeft(),
            Direction.SOUTH to northShape.flip(),
            Direction.WEST to northShape.rotateRight()
        )
    }
}
