package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.withBlockModelPath
import com.github.mystery2099.woodenAccentsMod.block.itemModelId
import com.github.mystery2099.woodenAccentsMod.block.woodType
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import net.minecraft.world.level.block.Block
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.Items
import net.minecraft.tags.TagKey
import net.minecraft.resources.ResourceLocation


class ThinPillarBlock(baseBlock: Block) : AbstractPillarBlock(baseBlock, shape) {
    override val connectableBlockTag: TagKey<Block> = ModBlockTags.thinPillarsConnectable
    override val tag: TagKey<Block> = ModBlockTags.thinPillars

    override fun codec(): MapCodec<out ThinPillarBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock }
        ).apply(instance, ::ThinPillarBlock)
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        this.offerRecipe(
            exporter = recipeExporter,
            outputNum = 5,
            primaryInput = this.baseBlock,
            secondaryInput = Items.STICK
        )
        offerPillarWoodcuttingRecipe(recipeExporter)
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val map = TextureMapping.cube(this.baseBlock)
        generator.blockStateOutput.accept(
            this.genBlockStateModelSupplier(
                centerModel = ResourceLocation.withDefaultNamespace("${this.woodType.name.lowercase()}_fence_post")
                    .withBlockModelPath(),
                bottomModel = ModModels.thinPillarBottom.create(this, map, generator.modelOutput)
            )
        )
        ModModels.thinPillarInventory.create(this.itemModelId, map, generator.modelOutput)
    }

    companion object {
        val shape = CanyonShapeConfiguration(
            topShape = VoxelAssembly.createCuboidShape(4, 13, 4, 12, 16, 12),
            centerShape = VoxelAssembly.createCuboidShape(6, 0, 6, 10, 16, 10),
            baseShape = VoxelAssembly.createCuboidShape(4, 0, 4, 12, 3, 12)
        )
    }
}
