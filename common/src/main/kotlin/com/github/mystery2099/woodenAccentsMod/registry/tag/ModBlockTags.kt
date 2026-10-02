package com.github.mystery2099.woodenAccentsMod.registry.tag

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.item.Item
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.resources.ResourceLocation


object ModBlockTags {

    private val _blockToItemTagMap: MutableMap<TagKey<Block>, TagKey<Item>> = linkedMapOf()

    /** Block tags that should have an item tag with the same contents during data generation. */
    val blockToItemTagMap: Map<TagKey<Block>, TagKey<Item>>
        get() = _blockToItemTagMap

    // Outside
    // Pillars
    val pillars = "pillars".toBlockTag().createMatchingItemTag()
    val thinPillars = "thin_pillars".toBlockTag().createMatchingItemTag()
    val thickPillars = "thick_pillars".toBlockTag().createMatchingItemTag()
    val thinPillarsConnectable = "thin_pillars_connectable".toBlockTag()
    val thickPillarsConnectable = "thick_pillars_connectable".toBlockTag()

    // Walls
    val woodenWalls = "wooden_walls".toBlockTag().createMatchingItemTag()

    // Ladders
    val plankLadders = "plank_ladders".toBlockTag().createMatchingItemTag()
    val connectingLadders = "connecting_ladders".toBlockTag().createMatchingItemTag()
    val simpleLadders = "simple_ladders".toBlockTag().createMatchingItemTag()

    // Fences
    val modernFences = "modern_fences".toBlockTag().createMatchingItemTag()
    val modernFenceConnectable = "modern_fence_connectable".toBlockTag()
    val modernFenceGates = "modern_fence_gates".toBlockTag().createMatchingItemTag()

    val supportBeams = "support_beams".toBlockTag().createMatchingItemTag()
    val supportBeamsConnectable = "support_beams_connectable".toBlockTag()
    val supportBeamsCannotConnect = "support_beams_cannot_connect".toBlockTag()
    val crates = "crates".toBlockTag().createMatchingItemTag()

    // Living room
    val tables = "tables".toBlockTag().createMatchingItemTag()
    val coffeeTables = "coffee_tables".toBlockTag().createMatchingItemTag()
    val tallCoffeeTableConnectable = "tall_coffee_table_connectable".toBlockTag()
    val thinBookshelves = "thin_bookshelves".toBlockTag().createMatchingItemTag()
    val plankCarpets = "plank_carpets".toBlockTag().createMatchingItemTag()
    val bracketShelves = "bracket_shelves".toBlockTag().createMatchingItemTag()
    val woodCutters = "wood_cutters".toBlockTag().createMatchingItemTag()

    @JvmStatic
    val desks = "desks".toBlockTag().createMatchingItemTag()
    val deskDrawers = "desk_drawers".toBlockTag().createMatchingItemTag()

    // Kitchen
    @JvmStatic
    val kitchenCounters = "kitchen_counters".toBlockTag().createMatchingItemTag()
    val kitchenCabinets = "kitchen_cabinets".toBlockTag().createMatchingItemTag()

    val chairs = "chairs".toBlockTag().createMatchingItemTag()
    private fun String.toBlockTag(namespace: String = WoodenAccentsMod.MOD_ID): TagKey<Block> {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, this))
    }

    /** Records a same-ID item tag for data generation and returns this block tag unchanged. */
    private fun TagKey<Block>.createMatchingItemTag() = also {
        _blockToItemTagMap[this] = TagKey.create(Registries.ITEM, location())
    }

    /** Treats a missing state as a non-match. */
    operator fun TagKey<Block>?.contains(blockState: BlockState?): Boolean = this?.let { blockState?.`is`(it) } ?: false

    /** Falls back to a same-ID item tag for block tags that were not registered above. */
    fun getItemTagFrom(blockTag: TagKey<Block>): TagKey<Item> {
        return _blockToItemTagMap[blockTag] ?: TagKey.create(Registries.ITEM, blockTag.location())
    }
}
