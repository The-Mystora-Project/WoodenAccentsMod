package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.HolderLookup
import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.voxlib.combination.VoxelAssembly.appendShapes
import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.withBlockModelPath
import com.github.mystery2099.woodenAccentsMod.block.BlockStateConfigurer.Companion.with
import com.github.mystery2099.woodenAccentsMod.block.BlockStateUtil.isIn
import com.github.mystery2099.woodenAccentsMod.block.BlockStateUtil.isOf
import com.github.mystery2099.woodenAccentsMod.block.custom.enums.CoffeeTableTypes
import com.github.mystery2099.woodenAccentsMod.block.defaultItemStack
import com.github.mystery2099.woodenAccentsMod.block.itemModelId
import com.github.mystery2099.woodenAccentsMod.block.textureId
import com.github.mystery2099.woodenAccentsMod.block.woodType
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.asBlockStateVariant
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.putModel
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.withYRotationOf
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.customGroup
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.*
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.component.ModDataComponents
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import com.github.mystery2099.woodenAccentsMod.state.property.ModProperties
import com.github.mystery2099.woodenAccentsMod.util.WhenUtil
import com.github.mystery2099.woodenAccentsMod.util.WhenUtil.allOf
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.data.models.*
import net.minecraft.data.models.blockstates.*
import net.minecraft.data.models.model.*
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.LevelAccessor
import java.util.*
import java.util.function.Supplier
import net.minecraft.world.level.block.state.BlockBehaviour
import com.github.mystery2099.woodenAccentsMod.util.LootTableUtil
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.ItemStack
class CoffeeTableBlock(val baseBlock: Block, private val topBlock: Block) :
    AbstractWaterloggableBlock(BlockBehaviour.Properties.ofFullCopy(baseBlock)),
    CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider,
    CustomBlockLootTableProvider {


    override val tag: TagKey<Block> = ModBlockTags.coffeeTables
    override val itemGroup = ModItemGroup.FURNITURE

    override fun codec(): MapCodec<out CoffeeTableBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock },
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("top_block").forGetter { it.topBlock }
        ).apply(instance, ::CoffeeTableBlock)
    }

    private val BlockState.isTall: Boolean
        get() = getOptionalValue(type) == Optional.of(CoffeeTableTypes.TALL)

    override val variantItemGroupStack: ItemStack
        get() = this.defaultItemStack.apply {
            set(ModDataComponents.coffeeTableType, CoffeeTableTypes.TALL)
        }

    init {
        registerDefaultState(defaultBlockState().setShort().asSingle().setValue(waterlogged, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(
            type,
            north,
            east,
            south,
            west
        )
    }

    // Placing another short table on a short table upgrades it to the tall variant.
    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val heldType = ctx.itemInHand.get(ModDataComponents.coffeeTableType)
        val state = ctx.level.getBlockState(ctx.clickedPos)
        return if (state isOf this && heldType != CoffeeTableTypes.TALL) state.setTall()
        else defaultBlockState().setDirections(ctx.level, ctx.clickedPos).run {
            heldType?.let {
                if (it == CoffeeTableTypes.TALL) setValue(type, CoffeeTableTypes.TALL) else this
            } ?: this
        }
    }

    private fun BlockState.asSingle(): BlockState {
        return this.with {
            north to false
            east to false
            south to false
            west to false
        }
    }

    private fun BlockState.setShort(): BlockState = this.setValue(type, CoffeeTableTypes.SHORT)

    private fun BlockState.setTall(): BlockState = this.setValue(type, CoffeeTableTypes.TALL)

    private fun BlockState.setDirections(world: LevelAccessor, pos: BlockPos): BlockState {
        return this.with {
            north to world.checkNorthOf(pos)
            east to world.checkEastOf(pos)
            south to world.checkSouthOf(pos)
            west to world.checkWestOf(pos)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun canBeReplaced(state: BlockState, context: BlockPlaceContext): Boolean {
        return !context.isSecondaryUseActive() && !state.isTall && context.itemInHand.item == asItem()
    }

    private fun LevelAccessor.checkDirection(pos: BlockPos, direction: Direction): Boolean {
        return getBlockState(pos.relative(direction))?.let { otherState: BlockState ->
            getBlockState(pos)?.let { thisState: BlockState ->
                val states = arrayOf(thisState, otherState)
                if (states.all { it.block is CoffeeTableBlock }) thisState.getValue(type) == otherState.getValue(type)
                else if (otherState isIn ModBlockTags.tallCoffeeTableConnectable) thisState.isTall
                else states.all { it isIn tag }
            } ?: false
        } ?: false
    }

    private fun LevelAccessor.checkNorthOf(pos: BlockPos): Boolean = checkDirection(pos, Direction.NORTH)
    private fun LevelAccessor.checkEastOf(pos: BlockPos): Boolean = checkDirection(pos, Direction.EAST)
    private fun LevelAccessor.checkSouthOf(pos: BlockPos): Boolean = checkDirection(pos, Direction.SOUTH)
    private fun LevelAccessor.checkWestOf(pos: BlockPos): Boolean = checkDirection(pos, Direction.WEST)

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun updateShape(
        state: BlockState,
        direction: Direction?,
        neighborState: BlockState?,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState = super.updateShape(state, direction, neighborState, world, pos, neighborPos)
        .with {
            north to world.checkNorthOf(pos)
            east to world.checkEastOf(pos)
            south to world.checkSouthOf(pos)
            west to world.checkWestOf(pos)
        }

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState,
        world: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?
    ): VoxelShape = outlineShapes[shapeIndex(state)]

    private fun shapeIndex(state: BlockState): Int {
        var index = 0
        if (state.getValue(north)) index = index or NORTH_MASK
        if (state.getValue(east)) index = index or EAST_MASK
        if (state.getValue(south)) index = index or SOUTH_MASK
        if (state.getValue(west)) index = index or WEST_MASK
        if (state.getValue(type) == CoffeeTableTypes.TALL) index = index or TALL_MASK
        return index
    }

    override fun getCloneItemStack(world: LevelReader, pos: BlockPos, state: BlockState): ItemStack {
        return super.getCloneItemStack(world, pos, state).apply {
            set(ModDataComponents.coffeeTableType, state.getValue(type))
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        world: Item.TooltipContext?,
        tooltip: MutableList<Component>,
        options: TooltipFlag
    ) {
        super.appendHoverText(stack, world, tooltip, options)
        if (stack.has(ModDataComponents.coffeeTableType)) {
            tooltip.add(
                Component.literal(stack.get(ModDataComponents.coffeeTableType)?.getSerializedName())
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            )
        }
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 6).apply {
            define('_', topBlock)
            define('|', baseBlock)
            pattern("___")
            pattern("| |")
            customGroup(this@CoffeeTableBlock, "coffee_tables")
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
                    shortTopModel = ModModels.coffeeTableTopShort.create(this, map, generator.modelOutput),
                    shortLegModel = "${this.woodType.name.lowercase()}_coffee_table_leg_short".toIdentifier()
                        .withBlockModelPath(),
                    tallTopModel = ModModels.coffeeTableTopTall.create(this, map, generator.modelOutput),
                    tallLegModel = "${this.woodType.name.lowercase()}_coffee_table_leg_tall".toIdentifier()
                        .withBlockModelPath(),
                )
            )
            generateItemModel(this, generator)
        }
    }

    private fun blockStateModelSupplier(
        shortTopModel: ResourceLocation,
        shortLegModel: ResourceLocation,
        tallTopModel: ResourceLocation,
        tallLegModel: ResourceLocation
    ): MultiPartGenerator = MultiPartGenerator.multiPart(this).apply {
        val shortNorthEastVariant = shortLegModel.asBlockStateVariant()
        val tallNorthEastVariant = tallLegModel.asBlockStateVariant()

        val isTall = Condition.condition().term(ModProperties.coffeeTableType, CoffeeTableTypes.TALL)
        val isShort = Condition.condition().term(ModProperties.coffeeTableType, CoffeeTableTypes.SHORT)

        mapOf(
            isShort to Variant().putModel(shortTopModel),
            WhenUtil.notNorthEast to shortNorthEastVariant,
            WhenUtil.notNorthWest to shortNorthEastVariant.withYRotationOf(VariantProperties.Rotation.R270),
            WhenUtil.notSouthEast to shortNorthEastVariant.withYRotationOf(VariantProperties.Rotation.R90),
            WhenUtil.notSouthWest to shortNorthEastVariant.withYRotationOf(VariantProperties.Rotation.R180),
            isTall to tallTopModel.asBlockStateVariant(),
            allOf(WhenUtil.notNorthEast, isTall) to tallNorthEastVariant,
            allOf(WhenUtil.notNorthWest, isTall) to tallNorthEastVariant.withYRotationOf(VariantProperties.Rotation.R270),
            allOf(WhenUtil.notSouthEast, isTall) to tallNorthEastVariant.withYRotationOf(VariantProperties.Rotation.R90),
            allOf(WhenUtil.notSouthWest, isTall) to tallNorthEastVariant.withYRotationOf(VariantProperties.Rotation.R180)
        ).forEach(::with)
    }


    private fun generateItemModel(coffeeTableBlock: CoffeeTableBlock, generator: BlockModelGenerators) {
        val textureMap = mapOf(
            TextureSlot.TOP to coffeeTableBlock.topBlock.textureId,
            ModModels.legs to coffeeTableBlock.baseBlock.textureId
        )
        val tallModel = ModModels.coffeeTableTallInventory.create(
            coffeeTableBlock.itemModelId.withSuffix("_tall"),
            TextureMapping().apply {
                textureMap.forEach {
                    put(it.key, it.value)
                }
            },
            generator.modelOutput
        )
        val jsonObject = ModModels.coffeeTableInventory.createBaseTemplate(coffeeTableBlock.itemModelId, textureMap)
        val jsonArray = JsonArray()
        val jsonObject2 = JsonObject()
        val jsonObject3 = JsonObject()
        jsonObject3.addProperty("height", 1.0f)
        jsonObject2.add("predicate", jsonObject3)
        jsonObject2.addProperty("model", tallModel.toString())
        jsonArray.add(jsonObject2)
        jsonObject.add("overrides", jsonArray)
        generator.modelOutput.accept(coffeeTableBlock.itemModelId, Supplier {
            jsonObject
        })
    }
    @Suppress("DEPRECATION")
    override fun getLootTableBuilder(registries: HolderLookup.Provider): LootTable.Builder {
        val tallStatePredicate = StatePropertiesPredicate.Builder.properties().hasProperty(type, CoffeeTableTypes.TALL)
        val whenBlockIsTall = LootItemBlockStatePropertyCondition.hasBlockStateProperties(this).setProperties(tallStatePredicate)
        return LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add(
                LootTableUtil.applyExplosionDecay(
                    this, LootItem.lootTableItem(this).apply(
                        SetItemCountFunction.setCount(ConstantValue.exactly(2.0f))
                            .let { LootTableUtil.conditionally(it, LootTableUtil.hasNoSilkTouch(registries), whenBlockIsTall) }
                    ).apply(
                        SetComponentsFunction.setComponent(ModDataComponents.coffeeTableType, CoffeeTableTypes.TALL)
                            .let { LootTableUtil.conditionally(it, LootTableUtil.hasSilkTouch(registries), whenBlockIsTall) }
                    )
                )
            )
        )
    }

    companion object {
        val type = ModProperties.coffeeTableType
        val north: BooleanProperty = BlockStateProperties.NORTH
        val east: BooleanProperty = BlockStateProperties.EAST
        val south: BooleanProperty = BlockStateProperties.SOUTH
        val west: BooleanProperty = BlockStateProperties.WEST

        private const val SHAPE_VERTICAL_OFFSET = 7.0 / 16

        // Tops
        private val shortTopShape = VoxelAssembly.createCuboidShape(0, 7, 0, 16, 9, 16)

        private val tallTopShape = shortTopShape.move(0.0, SHAPE_VERTICAL_OFFSET, 0.0)

        // Short legs, authored facing north
        private val shortNorthEastLeg = VoxelAssembly.createCuboidShape(13.75, 0, 0.25, 15.75, 7, 2.25)

        private val shortNorthWestLeg = shortNorthEastLeg.rotateRight()

        // Short legs, authored facing south
        private val shortSouthEastLeg = shortNorthWestLeg.flip()

        private val shortSouthWestLeg = shortNorthEastLeg.flip()

        // Tall legs, authored facing north
        private val tallNorthEastLeg = shortNorthEastLeg.move(0.0, SHAPE_VERTICAL_OFFSET, 0.0)

        private val tallNorthWestLeg = tallNorthEastLeg.rotateRight()

        // Tall legs, authored facing south
        private val tallSouthEastLeg = tallNorthWestLeg.flip()

        private val tallSouthWestLeg = tallNorthEastLeg.flip()

        private val outlineShapes = Array(1 shl 5) { index -> shapeForIndex(index) }

        private fun shapeForIndex(index: Int): VoxelShape {
            val isTall = index and TALL_MASK != 0
            val north = index and NORTH_MASK != 0
            val east = index and EAST_MASK != 0
            val south = index and SOUTH_MASK != 0
            val west = index and WEST_MASK != 0

            val shouldAppendNorthEast = !north && !east
            val shouldAppendNorthWest = !north && !west
            val shouldAppendSouthEast = !south && !east
            val shouldAppendSouthWest = !south && !west

            return (if (isTall) tallTopShape else shortTopShape).appendShapes {
                // Short legs
                shortNorthEastLeg case shouldAppendNorthEast
                shortNorthWestLeg case shouldAppendNorthWest
                shortSouthEastLeg case shouldAppendSouthEast
                shortSouthWestLeg case shouldAppendSouthWest
                // Tall legs
                tallNorthEastLeg case (shouldAppendNorthEast && isTall)
                tallNorthWestLeg case (shouldAppendNorthWest && isTall)
                tallSouthEastLeg case (shouldAppendSouthEast && isTall)
                tallSouthWestLeg case (shouldAppendSouthWest && isTall)
            }
        }

        private const val NORTH_MASK = 1
        private const val EAST_MASK = 1 shl 1
        private const val SOUTH_MASK = 1 shl 2
        private const val WEST_MASK = 1 shl 3
        private const val TALL_MASK = 1 shl 4
    }
}
