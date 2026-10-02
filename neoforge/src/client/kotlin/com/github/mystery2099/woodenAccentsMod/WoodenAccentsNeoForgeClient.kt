package com.github.mystery2099.woodenAccentsMod

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.block.custom.CoffeeTableBlock
import com.github.mystery2099.woodenAccentsMod.block.custom.SimpleLadderBlock
import com.github.mystery2099.woodenAccentsMod.block.custom.WoodCutterBlock
import com.github.mystery2099.woodenAccentsMod.block.custom.enums.CoffeeTableTypes
import com.github.mystery2099.woodenAccentsMod.block.entity.ModBlockEntities
import com.github.mystery2099.woodenAccentsMod.entity.ModEntities
import com.github.mystery2099.woodenAccentsMod.recipe.ModRecipeTypes
import com.github.mystery2099.woodenAccentsMod.registry.component.ModDataComponents
import com.github.mystery2099.woodenAccentsMod.render.BracketShelfBlockEntityRenderer
import com.github.mystery2099.woodenAccentsMod.render.SeatRenderer
import com.github.mystery2099.woodenAccentsMod.screen.ModMenuTypes
import com.github.mystery2099.woodenAccentsMod.screen.WoodCutterScreen
import com.github.mystery2099.woodenAccentsMod.shulkerBoxTooltip.NeoForgeTooltipPlugin
import com.misterpemodder.shulkerboxtooltip.api.neoforge.ShulkerBoxTooltipPlugin
import net.minecraft.client.RecipeBookCategories
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.ModList
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent

@Mod(value = WoodenAccentsMod.MOD_ID, dist = [Dist.CLIENT])
class WoodenAccentsNeoForgeClient(modBus: IEventBus) {
    init {
        if (ModList.get().isLoaded("shulkerboxtooltip")) {
            ModLoadingContext.get().registerExtensionPoint(ShulkerBoxTooltipPlugin::class.java) {
                ShulkerBoxTooltipPlugin(::NeoForgeTooltipPlugin)
            }
        }
        modBus.addListener(::registerRenderers)
        modBus.addListener(::clientSetup)
        modBus.addListener(::registerMenuScreens)
        modBus.addListener(::registerRecipeBookCategories)
    }

    private fun registerMenuScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenuTypes.woodCutter, ::WoodCutterScreen)
    }

    // Like stonecutting, woodcutting has no recipe book screen; this only stops the "Unknown recipe category" warning.
    private fun registerRecipeBookCategories(event: RegisterRecipeBookCategoriesEvent) {
        event.registerRecipeCategoryFinder(ModRecipeTypes.woodcutting) { RecipeBookCategories.STONECUTTER }
    }

    private fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(ModEntities.seatEntity, ::SeatRenderer)
        event.registerBlockEntityRenderer(ModBlockEntities.bracketShelf, ::BracketShelfBlockEntityRenderer)
    }

    private fun clientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            ModBlocks.blocks.filter { it is SimpleLadderBlock || it is WoodCutterBlock }.forEach {
                ItemBlockRenderTypes.setRenderLayer(it, RenderType.cutout())
            }
            ModBlocks.blocks.filterIsInstance<CoffeeTableBlock>().forEach {
                ItemProperties.register(it.asItem(), ResourceLocation.withDefaultNamespace("height")) { stack, _, _, _ ->
                    if (stack.get(ModDataComponents.coffeeTableType) != CoffeeTableTypes.TALL) 0.5f else 1.0f
                }
            }
        }
    }
}
