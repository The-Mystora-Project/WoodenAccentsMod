package com.github.mystery2099.woodenAccentsMod.block.custom

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.block.BlockStateUtil.isOf
import com.github.mystery2099.woodenAccentsMod.block.entity.ModBlockEntities
import com.github.mystery2099.woodenAccentsMod.block.entity.custom.CrateBlockEntity
import com.github.mystery2099.woodenAccentsMod.block.textureId
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.customGroup
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.*
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModItemTags
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.piglin.PiglinAi
import net.minecraft.world.entity.player.Player
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Container
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.ItemContainerContents
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.ChatFormatting
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import java.util.function.Consumer
import net.minecraft.world.level.block.state.BlockBehaviour
import com.github.mystery2099.woodenAccentsMod.util.LootTableUtil
import net.minecraft.data.recipes.RecipeOutput

class CrateBlock(val baseBlock: Block, private val edgeBlock: Block) :
    BaseEntityBlock(BlockBehaviour.Properties.ofFullCopy(baseBlock)),
    CustomBlockStateProvider, CustomItemGroupProvider, CustomTagProvider<Block>, CustomRecipeProvider,
    CustomBlockLootTableProvider {

    override val itemGroup: ModItemGroup = ModItemGroup.STORAGE
    override val tag: TagKey<Block> = ModBlockTags.crates

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = CrateBlockEntity(pos, state)

    @Deprecated("Deprecated in Java", ReplaceWith("RenderShape.MODEL", "net.minecraft.world.level.block.RenderShape"))
    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if (world.isClientSide) return InteractionResult.SUCCESS

        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is CrateBlockEntity) {
            player.openMenu(blockEntity)
            PiglinAi.angerNearbyPiglins(player, true)
        }

        return InteractionResult.CONSUME
    }


    override fun codec(): MapCodec<CrateBlock> = CODEC

    override fun playerWillDestroy(world: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is CrateBlockEntity) {

            // Match shulker boxes: creative players keep the crate and its contents together.
            if (!world.isClientSide && player.isCreative && !blockEntity.isEmpty()) {

                // saveToItem carries the contents, custom name, lock, and loot table via components.
                val itemStack = ItemStack(this)
                blockEntity.saveToItem(itemStack, world.registryAccess())

                val itemEntity =
                    ItemEntity(world, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, itemStack)
                itemEntity.setDefaultPickUpDelay()
                world.addFreshEntity(itemEntity)

            } else blockEntity.unpackLootTable(player)

        }
        return super.playerWillDestroy(world, pos, state, player)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun getDrops(state: BlockState, builder: LootParams.Builder): List<ItemStack> {
        var newBuilder: LootParams.Builder = builder
        val blockEntity = newBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY)
        if (blockEntity is CrateBlockEntity) {
            newBuilder = newBuilder.withDynamicDrop("crate_contents".toIdentifier()) { consumer: Consumer<ItemStack> ->
                for (i in 0 until blockEntity.containerSize) {
                    consumer.accept(blockEntity.getItem(i))
                }
            }
        }
        return super.getDrops(state, newBuilder)
    }

    // Custom names transfer automatically; vanilla applies the item's components when placing.

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
        if (world.getBlockEntity(pos) is CrateBlockEntity) world.updateNeighbourForOutputSignal(pos, state.block)
        super.onRemove(state, world, pos, newState, moved)
    }

    override fun appendHoverText(
        stack: ItemStack,
        world: Item.TooltipContext?,
        tooltip: MutableList<Component>,
        options: TooltipFlag
    ) {
        super.appendHoverText(stack, world, tooltip, options)
        // Contents travel in DataComponents.CONTAINER; loot-table and lock state in their own components.
        if (stack.has(DataComponents.CONTAINER_LOOT)) {
            tooltip.add(Component.literal("???????"))
        }
        val contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
        var shown: Short = 0
        var total: Short = 0
        contents.nonEmptyItems().forEach { itemStack ->
            ++total
            if (shown > 4) return@forEach
            ++shown
            tooltip.add(itemStack.hoverName.copy().append(" x").append(itemStack.count.toString()))
        }
        if (total - shown > 0) {
            tooltip.add(Component.translatable("container.crate.more", total - shown).withStyle(ChatFormatting.ITALIC))
        }
    }


    @Deprecated("Deprecated in Java", ReplaceWith("true"))
    override fun hasAnalogOutputSignal(state: BlockState): Boolean = true

    @Deprecated("Deprecated in Java", ReplaceWith(
        "AbstractContainerMenu.getRedstoneSignalFromContainer(world.getBlockEntity(pos) as Container)",
        "net.minecraft.world.inventory.AbstractContainerMenu",
        "net.minecraft.world.Container"
    )
    )
    override fun getAnalogOutputSignal(state: BlockState, world: Level, pos: BlockPos): Int {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(world.getBlockEntity(pos) as Container)
    }

    override fun getCloneItemStack(world: LevelReader, pos: BlockPos, state: BlockState): ItemStack {
        val itemStack = super.getCloneItemStack(world, pos, state)
        world.getBlockEntity(pos, ModBlockEntities.crate).ifPresent { blockEntity: CrateBlockEntity ->
            blockEntity.saveToItem(itemStack, world.registryAccess())
        }
        return itemStack
    }


    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val map: TextureMapping = TextureMapping().put(TextureSlot.INSIDE, baseBlock.textureId)
            .put(TextureSlot.EDGE, edgeBlock.textureId)
            .put(TextureSlot.CROSS, edgeBlock.textureId.run {
                if (this.path.contains("stripped")) this
                else this.withPath(this.path.replace("block/", "block/stripped_"))
            })
        ModModels.crate.create(this, map, generator.modelOutput)
        generator.createNonTemplateModelBlock(this)
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, this).apply {
            define('n', baseBlock)
            define('t', edgeBlock)
            define('u', ModItemTags.woodenChests)
            pattern("tnt")
            pattern("nun")
            pattern("tnt")
            customGroup(this@CrateBlock, "crates")
            requires(ModItemTags.woodenChests)
            save(recipeExporter)
        }
    }

    override fun getLootTableBuilder(registries: HolderLookup.Provider): LootTable.Builder {
        // Mirrors vanilla shulker box drops: name, contents, lock, and loot table ride the item as components.
        return LootTable.lootTable().withPool(
            LootTableUtil.applyExplosionCondition(
                this, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add(
                    LootItem.lootTableItem(this).apply(
                        CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                            .include(DataComponents.CUSTOM_NAME)
                            .include(DataComponents.CONTAINER)
                            .include(DataComponents.LOCK)
                            .include(DataComponents.CONTAINER_LOOT)
                    )
                )
            )
        )
    }

    companion object {
        val CODEC: MapCodec<CrateBlock> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock },
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("edge_block").forGetter { it.edgeBlock }
            ).apply(instance, ::CrateBlock)
        }
    }
}
