package com.github.mystery2099.woodenAccentsMod.recipe

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsModRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeSerializer

object ModRecipeSerializers : WoodenAccentsModRegistry {
    val woodcutting: RecipeSerializer<WoodcuttingRecipe> = WoodcuttingRecipe.Serializer()

    override fun register() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, "woodcutting".toIdentifier(), woodcutting)
        super.register()
    }
}
