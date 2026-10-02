package com.github.mystery2099.woodenAccentsMod

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.block.custom.CoffeeTableBlock
import com.github.mystery2099.woodenAccentsMod.block.custom.SimpleLadderBlock
import com.github.mystery2099.woodenAccentsMod.block.custom.WoodCutterBlock
import com.github.mystery2099.woodenAccentsMod.block.custom.enums.CoffeeTableTypes
import com.github.mystery2099.woodenAccentsMod.block.entity.ModBlockEntities
import com.github.mystery2099.woodenAccentsMod.entity.ModEntities
import com.github.mystery2099.woodenAccentsMod.registry.component.ModDataComponents
import com.github.mystery2099.woodenAccentsMod.render.BracketShelfBlockEntityRenderer
import com.github.mystery2099.woodenAccentsMod.render.SeatRenderer
import com.github.mystery2099.woodenAccentsMod.screen.ModMenuTypes
import com.github.mystery2099.woodenAccentsMod.screen.WoodCutterScreen
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screens.MenuScreens
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation

object WoodenAccentsModClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.apply {
            ModBlocks.blocks.filter { it is SimpleLadderBlock || it is WoodCutterBlock }.forEach {
                putBlock(it, RenderType.cutout())
            }
        }
		ModBlocks.blocks.filterIsInstance<CoffeeTableBlock>().forEach {
			ItemProperties.register(
				it.asItem(), ResourceLocation.withDefaultNamespace("height")
			) { itemStack, _, _, _ ->
				if (itemStack.get(ModDataComponents.coffeeTableType) != CoffeeTableTypes.TALL) 0.5f else 1.0f
			}
		}
		EntityRendererRegistry.register(ModEntities.seatEntity, ::SeatRenderer)
		BlockEntityRenderers.register(ModBlockEntities.bracketShelf, ::BracketShelfBlockEntityRenderer)
		MenuScreens.register(ModMenuTypes.woodCutter, ::WoodCutterScreen)
	}

}
