package com.github.mystery2099.woodenAccentsMod

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.block.custom.ThinBookshelfBlock
import com.github.mystery2099.woodenAccentsMod.block.entity.ModBlockEntities
import com.github.mystery2099.woodenAccentsMod.entity.ModEntities
import com.github.mystery2099.woodenAccentsMod.item.group.ItemGroupContent
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.recipe.ModRecipeSerializers
import com.github.mystery2099.woodenAccentsMod.recipe.ModRecipeTypes
import com.github.mystery2099.woodenAccentsMod.registry.component.ModDataComponents
import com.github.mystery2099.woodenAccentsMod.screen.ModMenuTypes
import com.github.mystery2099.woodenAccentsMod.stat.ModStats
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.RegisterEvent

@Mod(WoodenAccentsMod.MOD_ID)
class WoodenAccentsNeoForge(modBus: IEventBus) {
    init {
        WoodenAccentsMod.logger.info("Initializing ${WoodenAccentsMod.MOD_ID}")
        modBus.addListener(::register)
        modBus.addListener(::buildCreativeTabContents)
        modBus.addListener(::allowThinBookshelvesOnChiseledBookshelfEntity)
    }

    /**
     * Thin bookshelves extend [net.minecraft.world.level.block.ChiseledBookShelfBlock] and reuse its
     * block entity. Vanilla only lists the chiseled bookshelf as valid, so add ours through NeoForge.
     */
    private fun allowThinBookshelvesOnChiseledBookshelfEntity(event: BlockEntityTypeAddBlocksEvent) {
        event.modify(
            BlockEntityType.CHISELED_BOOKSHELF,
            *ModBlocks.blocks.filterIsInstance<ThinBookshelfBlock>().toTypedArray()
        )
    }

    private fun register(event: RegisterEvent) {
        when (event.registryKey) {
            Registries.DATA_COMPONENT_TYPE -> ModDataComponents.register()
            Registries.BLOCK -> ModBlocks.register()
            Registries.ITEM -> ModBlocks.registerItems()
            Registries.BLOCK_ENTITY_TYPE -> ModBlockEntities.register()
            Registries.ENTITY_TYPE -> ModEntities.register()
            Registries.RECIPE_TYPE -> ModRecipeTypes.register()
            Registries.RECIPE_SERIALIZER -> ModRecipeSerializers.register()
            Registries.MENU -> ModMenuTypes.register()
            Registries.CUSTOM_STAT -> ModStats.register()
            Registries.CREATIVE_MODE_TAB -> registerCreativeTabs()
        }
    }

    private fun registerCreativeTabs() {
        for (group in ModItemGroup.entries) {
            val id = group.path.toIdentifier()
            Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                id,
                CreativeModeTab.builder()
                    .icon { ItemGroupContent.getEntries(group).first() }
                    .title(Component.translatable(id.toLanguageKey()))
                    .build()
            )
        }
    }

    private fun buildCreativeTabContents(event: BuildCreativeModeTabContentsEvent) {
        for (group in ModItemGroup.entries) {
            val key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, group.path.toIdentifier())
            if (event.tabKey == key) {
                ItemGroupContent.getEntries(group).forEach(event::accept)
            }
        }
    }
}
