package com.github.mystery2099.woodenAccentsMod.block.custom

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import com.github.mystery2099.voxlib.combination.VoxelAssembly
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.withBlockModelPath
import com.github.mystery2099.woodenAccentsMod.block.itemModelId
import com.github.mystery2099.woodenAccentsMod.block.woodType
import com.github.mystery2099.woodenAccentsMod.data.client.ModModels
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.tags.TagKey
import net.minecraft.core.BlockPos
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.level.BlockGetter

class ThickPillarBlock(baseBlock: Block) : AbstractPillarBlock(baseBlock, shape) {
    override val connectableBlockTag: TagKey<Block> = ModBlockTags.thickPillarsConnectable
    override val tag: TagKey<Block> = ModBlockTags.thickPillars

    override fun codec(): MapCodec<out ThickPillarBlock> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter { it.baseBlock }
        ).apply(instance, ::ThickPillarBlock)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun getCollisionShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = if (!state.getValue(AbstractPillarBlock.up) && !state.getValue(AbstractPillarBlock.down)) {
        Shapes.block()
    } else {
        getShape(state, world, pos, context)
    }

    override fun offerRecipeTo(recipeExporter: RecipeOutput) {
        this.offerRecipe(exporter = recipeExporter, outputNum = 6, primaryInput = baseBlock, secondaryInput = baseBlock)
        offerPillarWoodcuttingRecipe(recipeExporter)
    }

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        val map = TextureMapping.cube(this.baseBlock)
        generator.blockStateOutput.accept(
            this.genBlockStateModelSupplier(
                centerModel = "${this.woodType.name.lowercase()}_plank_wall_post".toIdentifier().withBlockModelPath(),
                bottomModel = ModModels.thickPillarBottom.create(this, map, generator.modelOutput)
            )
        )
        ModModels.thickPillarInventory.create(this.itemModelId, map, generator.modelOutput)
    }

    companion object {
        val shape = CanyonShapeConfiguration(
            topShape = VoxelAssembly.createCuboidShape(1, 10, 1, 15, 16, 15),
            centerShape = VoxelAssembly.createCuboidShape(4, 0, 4, 12, 16, 12),
            baseShape = VoxelAssembly.createCuboidShape(1, 0, 1, 15, 6, 15)
        )
    }
}
