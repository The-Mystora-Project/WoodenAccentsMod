package com.github.mystery2099.woodenAccentsMod.recipe

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsModRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeType

object ModRecipeTypes : WoodenAccentsModRegistry {
    private val woodcuttingId = "woodcutting".toIdentifier()
    val woodcutting: RecipeType<WoodcuttingRecipe> = object : RecipeType<WoodcuttingRecipe> {
        override fun toString(): String = woodcuttingId.toString()
    }

    override fun register() {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, woodcuttingId, woodcutting)
        super.register()
    }
}
