package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.woodenAccentsMod.block.textureId
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.WallBlock
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.state.BlockBehaviour

class CustomWallBlock(val baseBlock: Block) : WallBlock(BlockBehaviour.Properties.ofFullCopy(baseBlock)), CustomItemGroupProvider,
    CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider {
    override val tag: TagKey<Block> = ModBlockTags.woodenWalls
    override val itemGroup = ModItemGroup.BUILDING

    override fun codec(): MapCodec<WallBlock> = CODEC

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        RecipeProvider.wall(recipeExporter, RecipeCategory.DECORATIONS, this, baseBlock)
        offerWoodcuttingRecipe(recipeExporter, baseBlock, this)
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        TextureMapping().put(TextureSlot.WALL, this.baseBlock.textureId).let { map ->
            generator.blockStateOutput.accept(
                BlockModelGenerators.createWall(
                    this,
                    ModelTemplates.WALL_POST.create(this, map, generator.modelOutput),
                    ModelTemplates.WALL_LOW_SIDE.create(this, map, generator.modelOutput),
                    ModelTemplates.WALL_TALL_SIDE.create(this, map, generator.modelOutput)
                )
            )
            generator.delegateItemModel(this, ModelTemplates.WALL_INVENTORY.create(this, map, generator.modelOutput))
        }
    }

    companion object {
        // vanilla WallBlock declares its codec() as an invariant MapCodec<WallBlock>, so the codec is typed against
        // WallBlock and decoded into CustomWallBlock via its constructor reference.
        val CODEC: MapCodec<WallBlock> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { (it as CustomWallBlock).baseBlock }
            ).apply(instance, ::CustomWallBlock)
        }
    }
}
