package com.github.mystery2099.woodenAccentsMod.jei

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.recipe.ModRecipeTypes
import com.github.mystery2099.woodenAccentsMod.screen.ModMenuTypes
import com.github.mystery2099.woodenAccentsMod.screen.WoodCutterMenu
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.api.registration.IRecipeTransferRegistration
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation

/**
 * Shows woodcutting recipes in JEI. NeoForge finds this through [JeiPlugin]; Fabric finds it through the
 * `jei_mod_plugin` entrypoint in `fabric.mod.json`.
 */
@JeiPlugin
class WoodenAccentsJeiPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation = "jei_plugin".toIdentifier()

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(WoodcuttingRecipeCategory(registration.jeiHelpers.guiHelper))
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val recipeManager = Minecraft.getInstance().level?.recipeManager ?: return
        registration.addRecipes(
            WoodcuttingRecipeCategory.recipeType,
            recipeManager.getAllRecipesFor(ModRecipeTypes.woodcutting)
        )
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(ModBlocks.woodCutter, WoodcuttingRecipeCategory.recipeType)
    }

    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addRecipeTransferHandler(
            WoodCutterMenu::class.java,
            ModMenuTypes.woodCutter,
            WoodcuttingRecipeCategory.recipeType,
            WoodCutterMenu.INPUT_SLOT,
            1,
            WoodCutterMenu.INV_SLOT_START,
            36
        )
    }
}
