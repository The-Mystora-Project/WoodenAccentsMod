package com.github.mystery2099.woodenAccentsMod.entity

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsModRegistry
import com.github.mystery2099.woodenAccentsMod.entity.custom.SeatEntity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry

object ModEntities : WoodenAccentsModRegistry {
    val seatEntity: EntityType<SeatEntity> = EntityType.Builder.of({ type, world ->
        SeatEntity(type, world)
    }, MobCategory.MISC)
    .sized(0.01f, 0.01f)
    .clientTrackingRange(10)
    .updateInterval(20)
    // Seats are transient: they discard themselves without a rider or chair and carry no
    // save data, so skip persistence. This also avoids the DFU "No data fixer registered"
    // warning for non-vanilla entity ids.
    .noSave()
    .build("wooden_accents_mod:seat")

    override fun register() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, "seat".toIdentifier(), seatEntity)
        super.register()
    }
}
