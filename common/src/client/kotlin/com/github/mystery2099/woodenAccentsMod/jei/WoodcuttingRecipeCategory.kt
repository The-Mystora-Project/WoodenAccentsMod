package com.github.mystery2099.woodenAccentsMod.jei

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.recipe.WoodcuttingRecipe
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.AbstractRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder

/** Lays woodcutting recipes out the same way JEI shows stonecutting. */
class WoodcuttingRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<RecipeHolder<WoodcuttingRecipe>>(
    recipeType,
    Component.translatable("category.wooden_accents_mod.woodcutting"),
    guiHelper.createDrawableItemLike(ModBlocks.woodCutter),
    82,
    34
) {

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: RecipeHolder<WoodcuttingRecipe>, focuses: IFocusGroup) {
        val registries = Minecraft.getInstance().level?.registryAccess()
        val result = if (registries == null) ItemStack.EMPTY else recipe.value().getResultItem(registries)

        builder.addInputSlot(1, 9)
            .setStandardSlotBackground()
            .addIngredients(recipe.value().ingredients.first())
        builder.addOutputSlot(61, 9)
            .setOutputSlotBackground()
            .addItemStack(result)
    }

    override fun createRecipeExtras(builder: IRecipeExtrasBuilder, recipe: RecipeHolder<WoodcuttingRecipe>, focuses: IFocusGroup) {
        builder.addRecipeArrowWidget().setPosition(26, 9)
    }

    companion object {
        val recipeType: RecipeType<RecipeHolder<WoodcuttingRecipe>> =
            RecipeType.createRecipeHolderType("woodcutting".toIdentifier())
    }
}
