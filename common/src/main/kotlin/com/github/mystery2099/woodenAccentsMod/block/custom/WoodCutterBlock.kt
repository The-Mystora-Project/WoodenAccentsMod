package com.github.mystery2099.woodenAccentsMod.block.custom

import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.asBlockStateVariant
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import com.github.mystery2099.woodenAccentsMod.screen.WoodCutterMenu
import com.github.mystery2099.woodenAccentsMod.stat.ModStats
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.model.ModelLocationUtils
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.network.chat.Component
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

/** A wooden take on the [net.minecraft.world.level.block.StonecutterBlock] that uses woodcutting recipes. */
class WoodCutterBlock(settings: Properties) : AbstractWaterloggableBlock(settings),
    CustomBlockStateProvider, CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block> {

    override val itemGroup = ModItemGroup.FURNITURE
    override val tag: TagKey<Block> = ModBlockTags.woodCutters

    override fun codec(): MapCodec<WoodCutterBlock> = commonCodec

    init {
        registerDefaultState(defaultBlockState().setValue(facing, Direction.NORTH))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(facing)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState =
        super.getStateForPlacement(ctx).setValue(facing, ctx.horizontalDirection.opposite)

    @Deprecated("Deprecated in Java")
    override fun getShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = shape

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (world.isClientSide) return InteractionResult.SUCCESS

        player.openMenu(state.getMenuProvider(world, pos))
        player.awardStat(ModStats.interactWithWoodCutter)
        return InteractionResult.CONSUME
    }

    override fun getMenuProvider(state: BlockState, world: Level, pos: BlockPos): MenuProvider =
        SimpleMenuProvider({ containerId, playerInventory, _ ->
            WoodCutterMenu(containerId, playerInventory, ContainerLevelAccess.create(world, pos))
        }, containerTitle)

    override fun useShapeForLightOcclusion(state: BlockState): Boolean = true

    override fun isPathfindable(state: BlockState, type: PathComputationType): Boolean = false

    override fun rotate(state: BlockState, rotation: Rotation): BlockState =
        state.setValue(facing, rotation.rotate(state.getValue(facing)))

    override fun mirror(state: BlockState, mirror: Mirror): BlockState =
        state.rotate(mirror.getRotation(state.getValue(facing)))

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this).apply {
            define('I', Items.IRON_INGOT)
            define('#', ItemTags.PLANKS)
            pattern(" I ")
            pattern("###")
            requires(ItemTags.PLANKS)
            save(recipeExporter)
        }
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val model = ModelLocationUtils.getModelLocation(this)
        generator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(this, model.asBlockStateVariant())
                .with(BlockModelGenerators.createHorizontalFacingDispatch())
        )
        generator.delegateItemModel(this, model)
    }

    companion object {
        val facing: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        val commonCodec: MapCodec<WoodCutterBlock> = simpleCodec(::WoodCutterBlock)
        private val containerTitle: Component = Component.translatable("container.wooden_accents_mod.wood_cutter")

        // Matches the vanilla StonecutterBlock outline.
        private val shape: VoxelShape = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)
    }
}
