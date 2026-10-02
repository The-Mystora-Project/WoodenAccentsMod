package com.github.mystery2099.woodenAccentsMod.recipe

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.SingleItemRecipe
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level

/** The woodcutter's equivalent of [net.minecraft.world.item.crafting.StonecutterRecipe]. */
class WoodcuttingRecipe(group: String, ingredient: Ingredient, result: ItemStack) :
    SingleItemRecipe(ModRecipeTypes.woodcutting, ModRecipeSerializers.woodcutting, group, ingredient, result) {

    override fun matches(input: SingleRecipeInput, level: Level): Boolean = ingredient.test(input.item())

    override fun getToastSymbol(): ItemStack = ItemStack(ModBlocks.woodCutter)

    class Serializer : SingleItemRecipe.Serializer<WoodcuttingRecipe>(::WoodcuttingRecipe)
}
