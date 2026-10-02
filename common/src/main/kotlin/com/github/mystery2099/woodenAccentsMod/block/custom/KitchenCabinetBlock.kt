package com.github.mystery2099.woodenAccentsMod.block.custom

import com.github.mystery2099.voxlib.combination.VoxelAssembly.and
import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.block.BlockStateConfigurer.Companion.with
import com.github.mystery2099.woodenAccentsMod.block.BlockStateUtil.isOf
import com.github.mystery2099.woodenAccentsMod.block.entity.custom.KitchenCabinetBlockEntity
import com.github.mystery2099.woodenAccentsMod.block.textureId
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.asBlockStateVariant
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.customGroup
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModItemTags
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockLootTableProvider
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.piglin.PiglinAi
import net.minecraft.world.entity.player.Player
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.ItemStack
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties

import net.minecraft.world.InteractionResult
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import com.github.mystery2099.woodenAccentsMod.util.LootTableUtil
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.level.storage.loot.entries.LootItem
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.HolderLookup
class KitchenCabinetBlock(val baseBlock: Block, private val topBlock: Block) :
    BaseEntityBlock(BlockBehaviour.Properties.ofFullCopy(baseBlock)),
    CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider,
    CustomBlockLootTableProvider {

    override val tag: TagKey<Block> = ModBlockTags.kitchenCabinets
    override val itemGroup = ModItemGroup.STORAGE

    init {
        registerDefaultState(defaultBlockState().with {
            facing to Direction.NORTH
            open to false
        })
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
        if (blockEntity is KitchenCabinetBlockEntity) {
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
        pos: BlockPos?,
        newState: BlockState,
        moved: Boolean
    ) {
        if (state isOf newState.block) return
        with(world.getBlockEntity(pos)) {
            if (this is Container) {
                Containers.dropContents(world, pos, this)
                world.updateNeighbourForOutputSignal(pos, this@KitchenCabinetBlock)
            }
        }
        super.onRemove(state, world, pos, newState, moved)
    }


    @Deprecated("Deprecated in Java", ReplaceWith(
        "(world.getBlockEntity(pos) as? KitchenCabinetBlockEntity)?.also { it.tick() }",
        "com.github.mystery2099.woodenAccentsMod.block.entity.custom.KitchenCabinetBlockEntity"
    )
    )
    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        (world.getBlockEntity(pos) as? KitchenCabinetBlockEntity)?.also {
            it.tick()
        }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = KitchenCabinetBlockEntity(pos, state)

    override fun codec(): MapCodec<KitchenCabinetBlock> = CODEC

    @Deprecated("Deprecated in Java", ReplaceWith("RenderShape.MODEL", "net.minecraft.world.level.block.RenderShape"))
    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
    // Custom names transfer automatically; vanilla applies the item's components when placing.

    @Deprecated("Deprecated in Java", ReplaceWith("true"))
    override fun hasAnalogOutputSignal(state: BlockState): Boolean = true

    @Deprecated("Deprecated in Java", ReplaceWith(
        "AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos))",
        "net.minecraft.world.inventory.AbstractContainerMenu"
    )
    )
    override fun getAnalogOutputSignal(state: BlockState, world: Level, pos: BlockPos): Int {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos))
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "state.withProperties { facing setTo rotation.rotate(state.getValue(facing)) }",
        "com.mystery2099.wooden_accents_mod.util.BlockStateUtil.withProperties",
        "com.mystery2099.wooden_accents_mod.block.custom.KitchenCabinetBlock.Companion.facing",
        "com.mystery2099.wooden_accents_mod.block.custom.KitchenCabinetBlock.Companion.facing"
    )
    )
    override fun rotate(state: BlockState, rotation: Rotation): BlockState = state.with {
        facing to rotation.rotate(state.getValue(facing))
    }

    @Deprecated("Deprecated in Java")
    override fun mirror(state: BlockState, mirror: Mirror): BlockState =
        state.rotate(mirror.getRotation(state.getValue(facing)))

    override fun getLootTableBuilder(registries: HolderLookup.Provider): LootTable.Builder =
        LootTable.lootTable().withPool(
            LootTableUtil.applyExplosionCondition(
                this,
                LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add(
                    LootItem.lootTableItem(this).apply(
                        CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)
                    )
                )
            )
        )

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(facing, open)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(facing, ctx.horizontalDirection.opposite)

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = outlineShapes[state.getValue(facing)]
        ?: Shapes.block()

    @Deprecated("Deprecated in Java")
    override fun getCollisionShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = Shapes.block()

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 4).apply {
            define('#', baseBlock)
            define('_', topBlock)
            define('O', ModItemTags.woodenChests)
            pattern("___")
            pattern("#O#")
            pattern("###")
            customGroup(this@KitchenCabinetBlock, "kitchen_cabinets")
            requires(ModBlockTags.getItemTagFrom(ModBlockTags.kitchenCounters))
            save(recipeExporter)
        }
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val map = TextureMapping().apply {
            put(TextureSlot.TOP, topBlock.textureId)
            put(TextureSlot.SIDE, baseBlock.textureId)
        }
        val model = ModModels.kitchenCabinet.create(this, map, generator.modelOutput)
        generator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(this, model.asBlockStateVariant())
                .with(BlockModelGenerators.createHorizontalFacingDispatch())
        )
        generator.delegateItemModel(this, model)
    }

    companion object {
        val facing: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        val open: BooleanProperty = BlockStateProperties.OPEN

        val CODEC: MapCodec<KitchenCabinetBlock> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock },
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("top_block").forGetter { it.topBlock }
            ).apply(instance, ::KitchenCabinetBlock)
        }
        val directionVoxelShapeMap = mapOf(
            Direction.NORTH to AbstractKitchenCounterBlock.NORTH_SHAPE,
            Direction.EAST to AbstractKitchenCounterBlock.NORTH_SHAPE.rotateLeft(),
            Direction.SOUTH to AbstractKitchenCounterBlock.NORTH_SHAPE.flip(),
            Direction.WEST to AbstractKitchenCounterBlock.NORTH_SHAPE.rotateRight()
        )
        private val outlineShapes = directionVoxelShapeMap.mapValues { (_, shape) ->
            shape.and(AbstractKitchenCounterBlock.TOP_SHAPE)
        }
    }
}
