package com.github.mystery2099.woodenAccentsMod.block.custom

import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.voxlib.combination.VoxelAssembly.and
import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.block.BlockStateConfigurer.Companion.with
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.customGroup
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.entity.ModEntities
import com.github.mystery2099.woodenAccentsMod.entity.custom.SeatEntity
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.model.TexturedModel
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.Items
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.InteractionResult
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.AABB
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockBehaviour
class ChairBlock(settings: Properties, val baseBlock: Block) : HorizontalDirectionalBlock(settings), SimpleWaterloggedBlock,
    CustomBlockStateProvider, CustomItemGroupProvider,
    CustomRecipeProvider, CustomTagProvider<Block> {

    override val itemGroup: ModItemGroup = ModItemGroup.FURNITURE
    override val tag: TagKey<Block> = ModBlockTags.chairs

    init {
        this.registerDefaultState(defaultBlockState().with {
            waterlogged to false
            FACING to Direction.NORTH
        })
    }

    constructor(baseBlock: Block) : this(BlockBehaviour.Properties.ofFullCopy(baseBlock), baseBlock)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(FACING, waterlogged)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return defaultBlockState().with {
            waterlogged to ctx.level.getFluidState(ctx.clickedPos).`is`(Fluids.WATER)
            FACING to ctx.horizontalDirection.opposite
        }
    }

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if (player.isSecondaryUseActive() || player.isPassenger()) return InteractionResult.PASS
        val seats = world.getEntitiesOfClass(SeatEntity::class.java, AABB(pos)) { !it.isRemoved }
        if (seats.any { it.isVehicle() }) {
            return InteractionResult.CONSUME
        }
        if (world.isClientSide) return InteractionResult.SUCCESS

        seats.forEach { it.discard() }
        val seat = SeatEntity(ModEntities.seatEntity, world)
        seat.setPos(pos.x + 0.5, pos.y + 0.3, pos.z + 0.5)
        seat.yRot = state.getValue(FACING).toYRot()
        if (!world.addFreshEntity(seat)) return InteractionResult.PASS
        if (!player.startRiding(seat)) {
            seat.discard()
            return InteractionResult.PASS
        }
        player.yHeadRot = state.getValue(FACING).toYRot()
        return InteractionResult.CONSUME
    }

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState,
        world: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?
    ): VoxelShape = when (state.getValue(FACING)) {
        Direction.NORTH -> northShape
        Direction.EAST -> eastShape
        Direction.SOUTH -> southShape
        Direction.WEST -> westShape
        else -> Shapes.block()
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "state.also { if (it.getValue(waterlogged)) world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world)) }",
        "com.mystery2099.wooden_accents_mod.block.custom.ChairBlock.Companion.waterlogged",
        "net.minecraft.world.level.material.Fluids",
        "net.minecraft.world.level.material.Fluids"
    )
    )
    override fun updateShape(
        state: BlockState,
        direction: Direction?,
        neighborState: BlockState?,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState = state.also {
        if (it.getValue(waterlogged)) world.scheduleTick(
            pos,
            Fluids.WATER,
            Fluids.WATER.getTickDelay(world)
        )
    }


    @Deprecated("Deprecated in Java", ReplaceWith(
        "if (state.getValue(waterlogged)) Fluids.WATER.getSource(false) else Fluids.EMPTY.defaultFluidState()",
        "com.github.mystery2099.woodenAccentsMod.block.custom.ChairBlock.Companion.waterlogged",
        "net.minecraft.world.level.material.Fluids",
        "net.minecraft.world.level.material.Fluids"
    )
    )
    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(waterlogged)) Fluids.WATER.getSource(false)
        else Fluids.EMPTY.defaultFluidState()
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val texturedModel = TexturedModel.createDefault({ TextureMapping.cube(baseBlock) }, ModModels.basicChair)
        generator.createHorizontallyRotatedBlock(this, texturedModel)
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 3).apply {
            define('#', baseBlock)
            define('|', Items.STICK)
            pattern("#  ")
            pattern("###")
            pattern("| |")
            customGroup(this@ChairBlock, "chairs")
            requires(baseBlock)
            save(recipeExporter)
        }
        offerWoodcuttingRecipe(recipeExporter, baseBlock, this)
    }

    override fun codec(): MapCodec<ChairBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base").forGetter { it.baseBlock }
        ).apply(instance, ::ChairBlock)
    }

    companion object {
        val waterlogged: BooleanProperty = BlockStateProperties.WATERLOGGED

        // Shapes are authored facing north, then rotated for the other directions.
        private val northBaseShape = VoxelAssembly.createCuboidShape(2, 10, 12, 14, 20, 14)
        private val bottomShape = VoxelAssembly.union(
            VoxelAssembly.createCuboidShape(2, 8, 2, 14, 10, 14),
            VoxelAssembly.createCuboidShape(2, 0, 2, 4, 8, 4),
            VoxelAssembly.createCuboidShape(12, 0, 2, 14, 8, 4),
            VoxelAssembly.createCuboidShape(12, 0, 12, 14, 8, 14),
            VoxelAssembly.createCuboidShape(2, 0, 12, 4, 8, 14)
        )

        private val northShape = northBaseShape and bottomShape
        private val eastShape = northBaseShape.rotateLeft() and bottomShape
        private val southShape = northBaseShape.flip() and bottomShape
        private val westShape = northBaseShape.rotateRight() and bottomShape
    }
}
