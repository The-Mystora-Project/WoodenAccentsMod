package com.github.mystery2099.woodenAccentsMod.screen

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsModRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType

object ModMenuTypes : WoodenAccentsModRegistry {
    val woodCutter: MenuType<WoodCutterMenu> =
        MenuType({ containerId, playerInventory -> WoodCutterMenu(containerId, playerInventory) }, FeatureFlags.VANILLA_SET)

    override fun register() {
        Registry.register(BuiltInRegistries.MENU, "wood_cutter".toIdentifier(), woodCutter)
        super.register()
    }
}
