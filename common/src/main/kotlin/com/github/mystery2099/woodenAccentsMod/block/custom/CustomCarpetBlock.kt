package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.woodenAccentsMod.block.modelId
import com.github.mystery2099.woodenAccentsMod.block.textureId
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CarpetBlock
import net.minecraft.world.level.material.PushReaction
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.state.BlockBehaviour

class CustomCarpetBlock(val baseBlock: Block) : CarpetBlock(
    BlockBehaviour.Properties.of().strength(0.1f).pushReaction(PushReaction.DESTROY).apply {
        mapColor(baseBlock.defaultMapColor())
        sound(baseBlock.defaultBlockState().soundType)
        if (baseBlock.defaultBlockState().ignitedByLava()) ignitedByLava()
    }
), CustomItemGroupProvider, CustomRecipeProvider, CustomTagProvider<Block>, CustomBlockStateProvider {

    override val itemGroup = ModItemGroup.BUILDING
    override val tag: TagKey<Block> = ModBlockTags.plankCarpets

    override fun codec(): MapCodec<out CustomCarpetBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock }
        ).apply(instance, ::CustomCarpetBlock)
    }
    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        offerWoodcuttingRecipe(recipeExporter, baseBlock, this, count = 8)
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
            generator.createTrivialBlock(this, TextureMapping().put(TextureSlot.WOOL, this.baseBlock.textureId), ModelTemplates.CARPET)
            generator.delegateItemModel(this, this.modelId)
    }
}
