package com.github.mystery2099.woodenAccentsMod.data.generation

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod
import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.util.StringRepresentable


class BlockLootTableDataGen(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricBlockLootTableProvider(dataOutput, registryLookup) {
    override fun generate() {
        ModBlocks.blocks.forEach { block ->
            when (block) {
                is CustomBlockLootTableProvider -> block.addCustomDrop()
                else -> dropSelf(block)
            }
        }
    }

    private fun <T> addDropsDoubleWithProperty(
        drop: Block,
        property: Property<T>,
        value: T
    ) where T : Comparable<T>, T : StringRepresentable {
        LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add(
                applyExplosionDecay(
                    drop, LootItem.lootTableItem(drop).apply(
                        SetItemCountFunction.setCount(ConstantValue.exactly(2.0f))
                            .conditionally(
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value))
                            )
                    )
                )
            )
        ).also { add(drop, it) }
    }

    private fun CustomBlockLootTableProvider.addCustomDrop() {
        if (this is Block) {
            add(this, this.getLootTableBuilder(registries))
        } else {
            WoodenAccentsMod.logger.info("Interface: ${CustomBlockLootTableProvider::class.simpleName} must be used on a class which extends Block!")
        }
    }
}

/** Adds all conditions to the same loot function builder. */
fun <t : LootItemConditionalFunction.Builder<*>> t.conditionally(vararg builders: LootItemCondition.Builder): t {
    builders.forEach { this.`when`(it) }
    return this
}
