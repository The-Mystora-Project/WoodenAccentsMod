package com.github.mystery2099.woodenAccentsMod.util

import net.minecraft.advancements.critereon.EnchantmentPredicate
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.advancements.critereon.ItemSubPredicates
import net.minecraft.advancements.critereon.MinMaxBounds
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.predicates.MatchTool

/**
 * Exposes vanilla [net.minecraft.data.loot.BlockLootSubProvider] loot helpers to shared block classes.
 */
object LootTableUtil {
    // Match enchantments through the item's ENCHANTMENTS component.
    fun hasSilkTouch(registries: HolderLookup.Provider): LootItemCondition.Builder {
        val enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT)
        return MatchTool.toolMatches(
            ItemPredicate.Builder.item()
                .withSubPredicate(
                    ItemSubPredicates.ENCHANTMENTS,
                    ItemEnchantmentsPredicate.enchantments(
                        listOf(
                            EnchantmentPredicate(
                                enchantments.getOrThrow(Enchantments.SILK_TOUCH),
                                MinMaxBounds.Ints.atLeast(1)
                            )
                        )
                    )
                )
        )
    }

    fun hasNoSilkTouch(registries: HolderLookup.Provider): LootItemCondition.Builder =
        hasSilkTouch(registries).invert()

    /** Requires the drop to survive an explosion. */
    fun <T : ConditionUserBuilder<T>> applyExplosionCondition(item: ItemLike, builder: T): T {
        return builder.`when`(ExplosionCondition.survivesExplosion())
    }

    /** Reduces the drop count based on explosion strength. */
    fun <T : FunctionUserBuilder<T>> applyExplosionDecay(item: ItemLike, builder: T): T {
        return builder.apply(ApplyExplosionDecay.explosionDecay())
    }

    /** Adds all conditions to [builder]. */
    fun <T : LootItemConditionalFunction.Builder<*>> conditionally(
        builder: T,
        vararg builders: LootItemCondition.Builder
    ): T {
        builders.forEach { builder.`when`(it) }
        return builder
    }
}
