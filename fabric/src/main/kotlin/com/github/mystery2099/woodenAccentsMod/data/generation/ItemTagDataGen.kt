package com.github.mystery2099.woodenAccentsMod.data.generation

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModItemTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture

class ItemTagDataGen(
    output: FabricDataOutput?,
    completableFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricTagProvider.ItemTagProvider(output, completableFuture, ModDataGenerator.blockTagGen) {
    override fun addTags(arg: HolderLookup.Provider) {
        ModBlockTags.blockToItemTagMap.forEach(::copy)

        // wooden_fences and fence_gates also make these furnace fuel on both loaders.
        copy(BlockTags.WALLS, ItemTags.WALLS)
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES)
        copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES)
        copy(ConventionalBlockTags.WOODEN_FENCE_GATES, ConventionalItemTags.WOODEN_FENCE_GATES)

        // Every block copies ignitedByLava from its base block, so this picks up all crimson and warped variants.
        getOrCreateTagBuilder(ItemTags.NON_FLAMMABLE_WOOD).apply {
            ModBlocks.blocks
                .filterNot { it.defaultBlockState().ignitedByLava() }
                .map { it.asItem() }
                .filterNot { it == Items.AIR }
                .forEach(::add)
        }

        // Modern fences and gates already burn through the vanilla tags above. Thin pillars and plank flooring
        // burn for less because one plank cuts into several of them.
        getOrCreateTagBuilder(ModItemTags.furnaceFuels).apply {
            listOf(
                ModBlockTags.chairs,
                ModBlockTags.tables,
                ModBlockTags.coffeeTables,
                ModBlockTags.desks,
                ModBlockTags.deskDrawers,
                ModBlockTags.kitchenCounters,
                ModBlockTags.kitchenCabinets,
                ModBlockTags.woodenWalls,
                ModBlockTags.plankLadders,
                ModBlockTags.connectingLadders,
                ModBlockTags.simpleLadders,
                ModBlockTags.thickPillars,
                ModBlockTags.supportBeams,
                ModBlockTags.crates,
                ModBlockTags.thinBookshelves,
                ModBlockTags.bracketShelves,
                ModBlockTags.woodCutters,
            ).forEach { addTag(ModBlockTags.getItemTagFrom(it)) }
        }

        getOrCreateTagBuilder(ModItemTags.unnestable).apply {
            addTag(requireNotNull(ModBlockTags.blockToItemTagMap[ModBlockTags.crates]))
            addOptionalTag(ConventionalItemTags.SHULKER_BOXES.location)
            add(Items.SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX,
                Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX,
                Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
                Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
                Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
                Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX)
            add(Items.BUNDLE)
        }
    }
}
