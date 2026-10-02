package com.github.mystery2099.woodenAccentsMod

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.block.custom.ThinBookshelfBlock
import com.github.mystery2099.woodenAccentsMod.block.entity.ModBlockEntities
import com.github.mystery2099.woodenAccentsMod.entity.ModEntities
import com.github.mystery2099.woodenAccentsMod.item.group.ModCreativeTabs
import com.github.mystery2099.woodenAccentsMod.recipe.ModRecipeSerializers
import com.github.mystery2099.woodenAccentsMod.recipe.ModRecipeTypes
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModBlockTags
import com.github.mystery2099.woodenAccentsMod.registry.tag.ModItemTags
import com.github.mystery2099.woodenAccentsMod.registry.component.ModDataComponents
import com.github.mystery2099.woodenAccentsMod.screen.ModMenuTypes
import com.github.mystery2099.woodenAccentsMod.stat.ModStats
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType

object WoodenAccentsFabric : ModInitializer {
    override fun onInitialize() {
        WoodenAccentsMod.logger.info("Initializing ${WoodenAccentsMod.MOD_ID}")
        ModDataComponents.register()
        ModBlocks.register()
        ModBlocks.registerItems()
        ModBlockEntities.register()
        ModEntities.register()
        ModRecipeTypes.register()
        ModRecipeSerializers.register()
        ModMenuTypes.register()
        ModStats.register()
        ModCreativeTabs.register()
        allowThinBookshelvesOnChiseledBookshelfEntity()
        // Items cut several to a plank split the plank's 300 ticks so cutting one up never makes more fuel.
        // NeoForge reads the same burn times from its furnace fuel data map instead.
        FuelRegistry.INSTANCE.add(ModItemTags.furnaceFuels, 300)
        FuelRegistry.INSTANCE.add(ModBlockTags.getItemTagFrom(ModBlockTags.thinPillars), 75)
        FuelRegistry.INSTANCE.add(ModBlockTags.getItemTagFrom(ModBlockTags.plankCarpets), 37)
    }

    /**
     * Thin bookshelves extend [net.minecraft.world.level.block.ChiseledBookShelfBlock] and reuse its
     * block entity. Vanilla only lists the chiseled bookshelf as valid, so tell Fabric to accept ours too.
     */
    private fun allowThinBookshelvesOnChiseledBookshelfEntity() {
        val type = BlockEntityType.CHISELED_BOOKSHELF as FabricBlockEntityType
        ModBlocks.blocks.filterIsInstance<ThinBookshelfBlock>().forEach(type::addSupportedBlock)
    }
}
