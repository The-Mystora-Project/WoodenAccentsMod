package com.github.mystery2099.woodenAccentsMod.rei

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.recipe.ModRecipeTypes
import com.github.mystery2099.woodenAccentsMod.recipe.WoodcuttingRecipe
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.world.item.crafting.RecipeHolder
import java.util.function.Function

/**
 * Shows woodcutting recipes in REI. Fabric loads this through the `rei_client` entrypoint; NeoForge needs an
 * annotated subclass because the annotation only ships in the NeoForge REI jar.
 */
open class WoodenAccentsReiPlugin : REIClientPlugin {
    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(WoodcuttingCategory())
        registry.addWorkstations(WoodcuttingDisplay.categoryId, EntryStacks.of(ModBlocks.woodCutter))
    }

    override fun registerDisplays(registry: DisplayRegistry) {
        registry.registerRecipeFiller(
            WoodcuttingRecipe::class.java,
            ModRecipeTypes.woodcutting,
            Function<RecipeHolder<WoodcuttingRecipe>, WoodcuttingDisplay?>(::WoodcuttingDisplay)
        )
    }

    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry) {
        registry.register(WoodcuttingDisplay.categoryId, WoodcuttingDisplay.serializer)
    }
}
