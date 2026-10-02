package com.github.mystery2099.woodenAccentsMod.stat

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsModRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.StatFormatter
import net.minecraft.stats.Stats

object ModStats : WoodenAccentsModRegistry {
    val interactWithWoodCutter: ResourceLocation = "interact_with_wood_cutter".toIdentifier()

    override fun register() {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, interactWithWoodCutter, interactWithWoodCutter)
        Stats.CUSTOM.get(interactWithWoodCutter, StatFormatter.DEFAULT)
        super.register()
    }
}
