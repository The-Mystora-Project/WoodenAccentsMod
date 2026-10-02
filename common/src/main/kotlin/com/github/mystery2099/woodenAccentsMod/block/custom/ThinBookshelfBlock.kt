package com.github.mystery2099.woodenAccentsMod.block.custom

import net.minecraft.world.level.block.ChiseledBookShelfBlock
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.voxlib.rotation.VoxelRotation.flip
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateLeft
import com.github.mystery2099.voxlib.rotation.VoxelRotation.rotateRight
import com.github.mystery2099.woodenAccentsMod.block.itemModelId
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.asBlockStateVariant
import com.github.mystery2099.woodenAccentsMod.data.client.BlockStateVariantUtil.withYRotationOf
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import com.github.mystery2099.woodenAccentsMod.util.WhenUtil
import com.github.mystery2099.woodenAccentsMod.util.WhenUtil.allOf
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.data.models.*
import net.minecraft.data.models.blockstates.*
import net.minecraft.data.models.model.*
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockBehaviour

/**
 * Narrow chiseled bookshelf that reuses vanilla's chiseled-bookshelf block entity and interaction logic.
 * Fabric and NeoForge each add these blocks to [net.minecraft.world.level.block.entity.BlockEntityType.CHISELED_BOOKSHELF]
 * so [net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity] accepts them.
 */
class ThinBookshelfBlock(val baseBlock: Block) :
    ChiseledBookShelfBlock(Properties.ofFullCopy(baseBlock)),
    CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider {

    override val tag: TagKey<Block> = ModBlockTags.thinBookshelves

    // vanilla ChiseledBookShelfBlock declares its codec() as an invariant MapCodec<ChiseledBookShelfBlock>, so the
    // codec is typed against ChiseledBookShelfBlock while decoding into ThinBookshelfBlock.
    override fun codec(): MapCodec<ChiseledBookShelfBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { (it as ThinBookshelfBlock).baseBlock }
        ).apply(instance, ::ThinBookshelfBlock)
    }
    override val itemGroup: ModItemGroup = ModItemGroup.STORAGE

    @Deprecated("Deprecated in Java")
    override fun getShape(
        state: BlockState,
        world: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?
    ): VoxelShape = when (state.getValue(HorizontalDirectionalBlock.FACING)) {
        Direction.NORTH -> northShape
        Direction.EAST -> eastShape
        Direction.SOUTH -> southShape
        Direction.WEST -> westShape
        else -> Shapes.block()
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 2).apply {
            define('#', baseBlock)
            define('_', Ingredient.of(ItemTags.WOODEN_SLABS))
            pattern("##")
            pattern("__")
            pattern("##")
            group("thin_bookshelves")
            requires(baseBlock)
            save(recipeExporter)
        }
        offerWoodcuttingRecipe(recipeExporter, baseBlock, this)
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        MultiPartGenerator.multiPart(this).apply {
            TextureMapping.cube(baseBlock).let { map ->
                ModModels.thinBookshelfItem.create(this@ThinBookshelfBlock.itemModelId, map, generator.modelOutput)

                val bookshelfModel =
                    ModModels.thinBookshelfBlock.create(this@ThinBookshelfBlock, map, generator.modelOutput)

                val slotModels = arrayOf(
                    ModModels.thinBookshelfSlot0,
                    ModModels.thinBookshelfSlot1,
                    ModModels.thinBookshelfSlot2,
                    ModModels.thinBookshelfSlot3,
                    ModModels.thinBookshelfSlot4,
                    ModModels.thinBookshelfSlot5
                )
                val directions = arrayOf(
                    WhenUtil.facingNorthHorizontal,
                    WhenUtil.facingEastHorizontal,
                    WhenUtil.facingSouthHorizontal,
                    WhenUtil.facingWestHorizontal
                )
                val variants = Array(4) { bookshelfModel.asBlockStateVariant() }
                val slotVariants = Array(6) { i ->
                    Array(4) {
                        slotModels[i].asBlockStateVariant().withYRotationOf(VariantProperties.Rotation.entries[it])
                    }
                }

                for (i in directions.indices) {
                    with(directions[i], variants[i].withYRotationOf(VariantProperties.Rotation.entries[i]))
                    for (j in slotVariants.indices) {
                        with(
                            allOf(directions[i], Condition.condition().term(SLOT_OCCUPIED_PROPERTIES[j], true)),
                            slotVariants[j][i]
                        )
                    }
                }
            }
        }.also { generator.blockStateOutput.accept(it) }
    }

    companion object {
        private val northShape = VoxelAssembly.createCuboidShape(0, 0, 11, 16, 16, 16)
        private val eastShape = northShape.rotateLeft()
        private val southShape = northShape.flip()
        private val westShape = northShape.rotateRight()
    }
}
