package com.github.mystery2099.woodenAccentsMod.registry.tag

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey

object ModItemTags {

    /** Plain c:chests also holds ender chests, which shouldn't be craftable into storage furniture. */
    val woodenChests = "chests/wooden".createItemTag("c")
    val unnestable = "unnestable".createItemTag()

    /** Burns for 300 ticks, like vanilla wooden blocks. Items in minecraft:non_flammable_wood are skipped. */
    val furnaceFuels = "furnace_fuels".createItemTag()

    private fun String.createItemTag(namespace: String = WoodenAccentsMod.MOD_ID): TagKey<Item> =
        TagKey.create(Registries.ITEM, this.toIdentifier(namespace))

    infix fun ItemStack.isIn(tag: TagKey<Item>) = `is`(tag)

    infix operator fun TagKey<Item>.contains(stack: ItemStack) = stack.isIn(this)

}
