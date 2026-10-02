package com.github.mystery2099.woodenAccentsMod.block.custom

import com.github.mystery2099.voxlib.combination.VoxelAssembly.appendShapes
import com.github.mystery2099.woodenAccentsMod.block.BlockStateConfigurer.Companion.with
import com.github.mystery2099.woodenAccentsMod.block.BlockStateUtil.isIn
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.asBlockStateVariant
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.uvLock
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.withXRotationOf
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.util.WhenUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.models.blockstates.MultiPartGenerator
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SupportType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

abstract class AbstractPillarBlock(val baseBlock: Block, private val pillarShape: CanyonShapeConfiguration) :
    AbstractWaterloggableBlock(BlockBehaviour.Properties.ofFullCopy(baseBlock)),
    CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider {
    override val itemGroup = ModItemGroup.BUILDING
    abstract val connectableBlockTag: TagKey<Block>

    private val outlineShapes = Array(4) { connections ->
        pillarShape.centerShape.appendShapes {
            pillarShape.topShape case (connections and UP_CONNECTION == 0)
            pillarShape.baseShape case (connections and DOWN_CONNECTION == 0)
        }
    }

    init {
        registerDefaultState(defaultBlockState().with {
            up to false
            down to false
        })
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(up, down)
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
    ): BlockState = super.updateShape(
        state = state,
        direction = direction,
        neighborState = neighborState,
        world = world,
        pos = pos,
        neighborPos = neighborPos
    ).with {
        up to canConnect(world, pos, Direction.UP)
        down to canConnect(world, pos, Direction.DOWN)
    }

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState,
        world: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?
    ): VoxelShape = outlineShapes[getConnectionIndex(state)]

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState =
        super.getStateForPlacement(ctx).with {
            val world = ctx.level
            val pos = ctx.clickedPos
            up to canConnect(world, pos, Direction.UP)
            down to canConnect(world, pos, Direction.DOWN)
        }

    private fun canConnect(world: LevelAccessor, pos: BlockPos, direction: Direction): Boolean {
        val otherPos = pos.relative(direction)
        val otherState = world.getBlockState(otherPos)
        if (!(otherState isIn connectableBlockTag)) return false

        // An isolated thick pillar has full-cube collision, but remains visually connectable as a pillar.
        if (otherState.block is ThickPillarBlock) return true

        return otherState.isFaceSturdy(
            world,
            otherPos,
            direction.opposite,
            SupportType.CENTER
        ) && !otherState.isFaceSturdy(world, otherPos, direction.opposite)
    }

    fun offerRecipe(
        exporter: RecipeOutput,
        outputNum: Int,
        primaryInput: ItemLike,
        secondaryInput: ItemLike
    ) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, outputNum).apply {
            define('|', secondaryInput)
            define('#', primaryInput)
            pattern("###")
            pattern(" | ")
            pattern("###")
            group(
                when (this@AbstractPillarBlock) {
                    is ThickPillarBlock -> "thick_pillars"
                    is ThinPillarBlock -> "thin_pillars"
                    else -> "pillars"
                }
            )
            requires(primaryInput)
            save(exporter)
        }
    }

    fun offerPillarWoodcuttingRecipe(exporter: RecipeOutput, count: Int = 1) {
        offerWoodcuttingRecipe(exporter, baseBlock, this, count)
    }

    fun genBlockStateModelSupplier(
        centerModel: ResourceLocation,
        bottomModel: ResourceLocation
    ): MultiPartGenerator = MultiPartGenerator.multiPart(this).apply {
        with(centerModel.asBlockStateVariant())
        with(WhenUtil.notUp, bottomModel.asBlockStateVariant().withXRotationOf(VariantProperties.Rotation.R180).uvLock())
        with(WhenUtil.notDown, bottomModel.asBlockStateVariant())
    }

    @JvmRecord
    data class CanyonShapeConfiguration(val topShape: VoxelShape, val centerShape: VoxelShape, val baseShape: VoxelShape)
    companion object {
        val up: BooleanProperty = BlockStateProperties.UP
        val down: BooleanProperty = BlockStateProperties.DOWN

        private fun getConnectionIndex(state: BlockState): Int {
            var connections = 0
            if (state.getValue(up)) connections = connections or UP_CONNECTION
            if (state.getValue(down)) connections = connections or DOWN_CONNECTION
            return connections
        }

        private const val UP_CONNECTION = 1
        private const val DOWN_CONNECTION = 2
    }
}
