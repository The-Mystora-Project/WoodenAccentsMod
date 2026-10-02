package com.github.mystery2099.woodenAccentsMod.data.generation

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture


class BlockTagDataGen(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider.BlockTagProvider(output, registriesFuture) {
    private val TagKey<Block>.tagBuilder: FabricTagBuilder
        get() = getOrCreateTagBuilder(this)

    override fun addTags(arg: HolderLookup.Provider) {
        BlockTags.MINEABLE_WITH_AXE.addTags(*ModBlockTags.blockToItemTagMap.keys.toTypedArray())

        ModBlocks.blocks.filterIsInstance<CustomTagProvider<Block>>().forEach {
            it.tag += it as Block
        }

        ModBlockTags.pillars.addTags(ModBlockTags.thinPillars, ModBlockTags.thickPillars)
        ModBlockTags.thinPillarsConnectable.apply {
            addTags(
                ModBlockTags.thinPillars,
                ModBlockTags.supportBeams,
                ModBlockTags.tables,
            )
            // This provider doesn't define minecraft:fences, so it can't validate the reference.
            forceAdd(BlockTags.FENCES)
            addBlocks(Blocks.END_ROD, Blocks.HOPPER, Blocks.LIGHTNING_ROD)
        }
        ModBlockTags.thickPillarsConnectable.addTags(
            ModBlockTags.thickPillars,
            ModBlockTags.thinPillars,
            BlockTags.WALLS
        )
        // Vanilla wooden fences connect to anything in wooden_fences, so modern fences connect back to keep it two-way.
        ModBlockTags.modernFenceConnectable.addTags(
            ModBlockTags.modernFenceGates,
            ModBlockTags.modernFences,
            ModBlockTags.thickPillars,
            ModBlockTags.thinPillars,
            BlockTags.WOODEN_FENCES,
        )
        ModBlockTags.kitchenCounters += ModBlockTags.kitchenCabinets

        BlockTags.WALLS += ModBlockTags.woodenWalls
        BlockTags.WOODEN_FENCES += ModBlockTags.modernFences
        BlockTags.FENCE_GATES += ModBlockTags.modernFenceGates
        BlockTags.CLIMBABLE.addTags(ModBlockTags.plankLadders, ModBlockTags.connectingLadders, ModBlockTags.simpleLadders)
        BlockTags.COMBINATION_STEP_SOUND_BLOCKS += ModBlockTags.plankCarpets

        ConventionalBlockTags.WOODEN_FENCE_GATES += ModBlockTags.modernFenceGates
    }

    private fun TagKey<Block>.add(tag: TagKey<Block>): FabricTagBuilder = tagBuilder.addTag(tag)

    private fun TagKey<Block>.add(block: Block): FabricTagBuilder = tagBuilder.add(block)

    private fun TagKey<Block>.forceAdd(tag: TagKey<Block>): FabricTagBuilder = tagBuilder.forceAddTag(tag)

    private operator fun TagKey<Block>.plusAssign(tag: TagKey<Block>) {
        add(tag)
    }

    private operator fun TagKey<Block>.plusAssign(block: Block) {
        add(block)
    }

    private fun TagKey<Block>.addTags(vararg tags: TagKey<Block>) = tagBuilder.also { tags.forEach(it::addTag) }

    private fun TagKey<Block>.addBlocks(vararg blocks: Block) = tagBuilder.also { blocks.forEach(it::add) }
}
