package com.github.mystery2099.woodenAccentsMod.block

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.WoodenAccentsModRegistry
import com.github.mystery2099.woodenAccentsMod.block.custom.*
import com.github.mystery2099.woodenAccentsMod.item.CustomBlockItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.WoodType
import net.minecraft.data.models.model.ModelLocationUtils
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation

@Suppress("unused")
object ModBlocks : WoodenAccentsModRegistry {
    private data class BlockItemRegistration(val block: Block, val maxStackSize: Int)

    private val registries = mutableSetOf<Block>()
    private val blockItems = linkedMapOf<ResourceLocation, BlockItemRegistration>()
    val blocks: Set<Block> = registries

    fun registerItems() {
        blockItems.forEach { (identifier, registration) ->
            Registry.register(
                BuiltInRegistries.ITEM,
                identifier,
                CustomBlockItem(registration.block, Item.Properties().stacksTo(registration.maxStackSize))
            )
        }
    }

    // Seating
    val oakPlankChair = ChairBlock(Blocks.OAK_PLANKS).registerAs("oak_plank_chair")
    val sprucePlankChair = ChairBlock(Blocks.SPRUCE_PLANKS).registerAs("spruce_plank_chair")
    val birchPlankChair = ChairBlock(Blocks.BIRCH_PLANKS).registerAs("birch_plank_chair")
    val junglePlankChair = ChairBlock(Blocks.JUNGLE_PLANKS).registerAs("jungle_plank_chair")
    val acaciaPlankChair = ChairBlock(Blocks.ACACIA_PLANKS).registerAs("acacia_plank_chair")
    val darkOakPlankChair = ChairBlock(Blocks.DARK_OAK_PLANKS).registerAs("dark_oak_plank_chair")
    val mangrovePlankChair = ChairBlock(Blocks.MANGROVE_PLANKS).registerAs("mangrove_plank_chair")
    val cherryPlankChair = ChairBlock(Blocks.CHERRY_PLANKS).registerAs("cherry_plank_chair")
    val bambooPlankChair = ChairBlock(Blocks.BAMBOO_PLANKS).registerAs("bamboo_plank_chair")
    val bambooMosaicChair = ChairBlock(Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_chair")
    val crimsonPlankChair = ChairBlock(Blocks.CRIMSON_PLANKS).registerAs("crimson_plank_chair")
    val warpedPlankChair = ChairBlock(Blocks.WARPED_PLANKS).registerAs("warped_plank_chair")

    // Tables
    // Plank tables
    val oakPlankTable = TableBlock(Blocks.OAK_PLANKS, Blocks.OAK_PLANKS).registerAs("oak_plank_table")
    val sprucePlankTable = TableBlock(Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_PLANKS).registerAs("spruce_plank_table")
    val birchPlankTable = TableBlock(Blocks.BIRCH_PLANKS, Blocks.BIRCH_PLANKS).registerAs("birch_plank_table")
    val junglePlankTable = TableBlock(Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_PLANKS).registerAs("jungle_plank_table")
    val acaciaPlankTable = TableBlock(Blocks.ACACIA_PLANKS, Blocks.ACACIA_PLANKS).registerAs("acacia_plank_table")
    val darkOakPlankTable =
        TableBlock(Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_PLANKS).registerAs("dark_oak_plank_table")
    val mangrovePlankTable =
        TableBlock(Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_PLANKS).registerAs("mangrove_plank_table")
    val cherryPlankTable = TableBlock(Blocks.CHERRY_PLANKS, Blocks.CHERRY_PLANKS).registerAs("cherry_plank_table")
    val bambooPlankTable = TableBlock(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_PLANKS).registerAs("bamboo_plank_table")
    val bambooMosaicTable = TableBlock(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_table")
    val crimsonPlankTable = TableBlock(Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_PLANKS).registerAs("crimson_plank_table")
    val warpedPlankTable = TableBlock(Blocks.WARPED_PLANKS, Blocks.WARPED_PLANKS).registerAs("warped_plank_table")

    // Wood tables
    val oakTable = TableBlock(Blocks.OAK_PLANKS, Blocks.OAK_LOG).registerAs("oak_table")
    val spruceTable = TableBlock(Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG).registerAs("spruce_table")
    val birchTable = TableBlock(Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG).registerAs("birch_table")
    val jungleTable = TableBlock(Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG).registerAs("jungle_table")
    val acaciaTable = TableBlock(Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG).registerAs("acacia_table")
    val darkOakTable = TableBlock(Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG).registerAs("dark_oak_table")
    val mangroveTable = TableBlock(Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG).registerAs("mangrove_table")
    val cherryTable = TableBlock(Blocks.CHERRY_PLANKS, Blocks.CHERRY_LOG).registerAs("cherry_table")
    val bambooTable = TableBlock(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK).registerAs("bamboo_table")
    val crimsonTable = TableBlock(Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM).registerAs("crimson_table")
    val warpedTable = TableBlock(Blocks.WARPED_PLANKS, Blocks.WARPED_STEM).registerAs("warped_table")

    // Stripped tables
    val strippedOakTable = TableBlock(Blocks.OAK_PLANKS, Blocks.STRIPPED_OAK_LOG).registerAs("stripped_oak_table")
    val strippedSpruceTable =
        TableBlock(Blocks.SPRUCE_PLANKS, Blocks.STRIPPED_SPRUCE_LOG).registerAs("stripped_spruce_table")
    val strippedBirchTable =
        TableBlock(Blocks.BIRCH_PLANKS, Blocks.STRIPPED_BIRCH_LOG).registerAs("stripped_birch_table")
    val strippedJungleTable =
        TableBlock(Blocks.JUNGLE_PLANKS, Blocks.STRIPPED_JUNGLE_LOG).registerAs("stripped_jungle_table")
    val strippedAcaciaTable =
        TableBlock(Blocks.ACACIA_PLANKS, Blocks.STRIPPED_ACACIA_LOG).registerAs("stripped_acacia_table")
    val strippedDarkOakTable =
        TableBlock(Blocks.DARK_OAK_PLANKS, Blocks.STRIPPED_DARK_OAK_LOG).registerAs("stripped_dark_oak_table")
    val strippedMangroveTable =
        TableBlock(Blocks.MANGROVE_PLANKS, Blocks.STRIPPED_MANGROVE_LOG).registerAs("stripped_mangrove_table")
    val strippedCherryTable =
        TableBlock(Blocks.CHERRY_PLANKS, Blocks.STRIPPED_CHERRY_LOG).registerAs("stripped_cherry_table")
    val strippedBambooTable =
        TableBlock(Blocks.BAMBOO_PLANKS, Blocks.STRIPPED_BAMBOO_BLOCK).registerAs("stripped_bamboo_table")
    val strippedCrimsonTable =
        TableBlock(Blocks.CRIMSON_PLANKS, Blocks.STRIPPED_CRIMSON_STEM).registerAs("stripped_crimson_table")
    val strippedWarpedTable =
        TableBlock(Blocks.WARPED_PLANKS, Blocks.STRIPPED_WARPED_STEM).registerAs("stripped_warped_table")

    // Plank coffee tables
    val oakPlankCoffeeTable =
        CoffeeTableBlock(Blocks.OAK_PLANKS, Blocks.OAK_PLANKS).registerAs("oak_plank_coffee_table")
    val sprucePlankCoffeeTable =
        CoffeeTableBlock(Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_PLANKS).registerAs("spruce_plank_coffee_table")
    val birchPlankCoffeeTable =
        CoffeeTableBlock(Blocks.BIRCH_PLANKS, Blocks.BIRCH_PLANKS).registerAs("birch_plank_coffee_table")
    val junglePlankCoffeeTable =
        CoffeeTableBlock(Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_PLANKS).registerAs("jungle_plank_coffee_table")
    val acaciaPlankCoffeeTable =
        CoffeeTableBlock(Blocks.ACACIA_PLANKS, Blocks.ACACIA_PLANKS).registerAs("acacia_plank_coffee_table")
    val darkOakPlankCoffeeTable =
        CoffeeTableBlock(Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_PLANKS).registerAs("dark_oak_plank_coffee_table")
    val mangrovePlankCoffeeTable =
        CoffeeTableBlock(Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_PLANKS).registerAs("mangrove_plank_coffee_table")
    val cherryPlankCoffeeTable =
        CoffeeTableBlock(Blocks.CHERRY_PLANKS, Blocks.CHERRY_PLANKS).registerAs("cherry_plank_coffee_table")
    val bambooPlankCoffeeTable =
        CoffeeTableBlock(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_PLANKS).registerAs("bamboo_plank_coffee_table")
    val bambooMosaicCoffeeTable =
        CoffeeTableBlock(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_coffee_table")
    val crimsonPlankCoffeeTable =
        CoffeeTableBlock(Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_PLANKS).registerAs("crimson_plank_coffee_table")
    val warpedPlankCoffeeTable =
        CoffeeTableBlock(Blocks.WARPED_PLANKS, Blocks.WARPED_PLANKS).registerAs("warped_plank_coffee_table")

    // Wood coffee tables
    val oakCoffeeTable = CoffeeTableBlock(Blocks.OAK_PLANKS, Blocks.OAK_LOG).registerAs("oak_coffee_table")
    val spruceCoffeeTable = CoffeeTableBlock(Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG).registerAs("spruce_coffee_table")
    val birchCoffeeTable = CoffeeTableBlock(Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG).registerAs("birch_coffee_table")
    val jungleCoffeeTable = CoffeeTableBlock(Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG).registerAs("jungle_coffee_table")
    val acaciaCoffeeTable = CoffeeTableBlock(Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG).registerAs("acacia_coffee_table")
    val darkOakCoffeeTable =
        CoffeeTableBlock(Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG).registerAs("dark_oak_coffee_table")
    val mangroveCoffeeTable =
        CoffeeTableBlock(Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG).registerAs("mangrove_coffee_table")
    val cherryCoffeeTable = CoffeeTableBlock(Blocks.CHERRY_PLANKS, Blocks.CHERRY_LOG).registerAs("cherry_coffee_table")
    val bambooCoffeeTable =
        CoffeeTableBlock(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK).registerAs("bamboo_coffee_table")
    val crimsonCoffeeTable =
        CoffeeTableBlock(Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM).registerAs("crimson_coffee_table")
    val warpedCoffeeTable = CoffeeTableBlock(Blocks.WARPED_PLANKS, Blocks.WARPED_STEM).registerAs("warped_coffee_table")

    // Stripped coffee tables
    val strippedOakCoffeeTable =
        CoffeeTableBlock(Blocks.OAK_PLANKS, Blocks.STRIPPED_OAK_LOG).registerAs("stripped_oak_coffee_table")
    val strippedSpruceCoffeeTable =
        CoffeeTableBlock(Blocks.SPRUCE_PLANKS, Blocks.STRIPPED_SPRUCE_LOG).registerAs("stripped_spruce_coffee_table")
    val strippedBirchCoffeeTable =
        CoffeeTableBlock(Blocks.BIRCH_PLANKS, Blocks.STRIPPED_BIRCH_LOG).registerAs("stripped_birch_coffee_table")
    val strippedJungleCoffeeTable =
        CoffeeTableBlock(Blocks.JUNGLE_PLANKS, Blocks.STRIPPED_JUNGLE_LOG).registerAs("stripped_jungle_coffee_table")
    val strippedAcaciaCoffeeTable =
        CoffeeTableBlock(Blocks.ACACIA_PLANKS, Blocks.STRIPPED_ACACIA_LOG).registerAs("stripped_acacia_coffee_table")
    val strippedDarkOakCoffeeTable = CoffeeTableBlock(
        Blocks.DARK_OAK_PLANKS,
        Blocks.STRIPPED_DARK_OAK_LOG
    ).registerAs("stripped_dark_oak_coffee_table")
    val strippedMangroveCoffeeTable = CoffeeTableBlock(
        Blocks.MANGROVE_PLANKS,
        Blocks.STRIPPED_MANGROVE_LOG
    ).registerAs("stripped_mangrove_coffee_table")
    val strippedCherryCoffeeTable =
        CoffeeTableBlock(Blocks.CHERRY_PLANKS, Blocks.STRIPPED_CHERRY_LOG).registerAs("stripped_cherry_coffee_table")
    val strippedBambooCoffeeTable =
        CoffeeTableBlock(Blocks.BAMBOO_PLANKS, Blocks.STRIPPED_BAMBOO_BLOCK).registerAs("stripped_bamboo_coffee_table")
    val strippedCrimsonCoffeeTable = CoffeeTableBlock(
        Blocks.CRIMSON_PLANKS,
        Blocks.STRIPPED_CRIMSON_STEM
    ).registerAs("stripped_crimson_coffee_table")
    val strippedWarpedCoffeeTable =
        CoffeeTableBlock(Blocks.WARPED_PLANKS, Blocks.STRIPPED_WARPED_STEM).registerAs("stripped_warped_coffee_table")


    // Desks
    val oakDesk = DeskBlock(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).registerAs("oak_desk")
    val spruceDesk = DeskBlock(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).registerAs("spruce_desk")
    val birchDesk = DeskBlock(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).registerAs("birch_desk")
    val jungleDesk = DeskBlock(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).registerAs("jungle_desk")
    val acaciaDesk = DeskBlock(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).registerAs("acacia_desk")
    val darkOakDesk = DeskBlock(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).registerAs("dark_oak_desk")
    val mangroveDesk = DeskBlock(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG).registerAs("mangrove_desk")
    val cherryDesk = DeskBlock(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG).registerAs("cherry_desk")
    val bambooDesk = DeskBlock(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK).registerAs("bamboo_desk")
    val bambooMosaicDesk = DeskBlock(Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_desk")
    val warpedDesk = DeskBlock(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).registerAs("warped_desk")
    val crimsonDesk = DeskBlock(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).registerAs("crimson_desk")

    // Desk drawers
    val oakDeskDrawer = DeskDrawerBlock(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).registerAs("oak_desk_drawer")
    val spruceDeskDrawer =
        DeskDrawerBlock(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).registerAs("spruce_desk_drawer")
    val birchDeskDrawer =
        DeskDrawerBlock(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).registerAs("birch_desk_drawer")
    val jungleDeskDrawer =
        DeskDrawerBlock(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).registerAs("jungle_desk_drawer")
    val acaciaDeskDrawer =
        DeskDrawerBlock(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).registerAs("acacia_desk_drawer")
    val darkOakDeskDrawer =
        DeskDrawerBlock(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).registerAs("dark_oak_desk_drawer")
    val mangroveDeskDrawer =
        DeskDrawerBlock(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG).registerAs("mangrove_desk_drawer")
    val cherryDeskDrawer =
        DeskDrawerBlock(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG).registerAs("cherry_desk_drawer")
    val bambooDeskDrawer =
        DeskDrawerBlock(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK).registerAs("bamboo_desk_drawer")
    val bambooMosaicDeskDrawer =
        DeskDrawerBlock(Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_desk_drawer")
    val warpedDeskDrawer =
        DeskDrawerBlock(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).registerAs("warped_desk_drawer")
    val crimsonDeskDrawer =
        DeskDrawerBlock(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).registerAs("crimson_desk_drawer")

    val woodCutter = WoodCutterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion())
        .registerAs("wood_cutter")

    // Kitchen counters
    val oakKitchenCounter = KitchenCounterBlock(Blocks.OAK_PLANKS, Blocks.OAK_LOG).registerAs("oak_kitchen_counter")
    val spruceKitchenCounter =
        KitchenCounterBlock(Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG).registerAs("spruce_kitchen_counter")
    val birchKitchenCounter =
        KitchenCounterBlock(Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG).registerAs("birch_kitchen_counter")
    val jungleKitchenCounter =
        KitchenCounterBlock(Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG).registerAs("jungle_kitchen_counter")
    val acaciaKitchenCounter =
        KitchenCounterBlock(Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG).registerAs("acacia_kitchen_counter")
    val darkOakKitchenCounter =
        KitchenCounterBlock(Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG).registerAs("dark_oak_kitchen_counter")
    val mangroveKitchenCounter =
        KitchenCounterBlock(Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG).registerAs("mangrove_kitchen_counter")
    val cherryKitchenCounter =
        KitchenCounterBlock(Blocks.CHERRY_PLANKS, Blocks.CHERRY_LOG).registerAs("cherry_kitchen_counter")
    val bambooKitchenCounter =
        KitchenCounterBlock(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK).registerAs("bamboo_kitchen_counter")
    val bambooMosaicKitchenCounter =
        KitchenCounterBlock(Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_BLOCK).registerAs("bamboo_mosaic_kitchen_counter")
    val crimsonKitchenCounter =
        KitchenCounterBlock(Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM).registerAs("crimson_kitchen_counter")
    val warpedKitchenCounter =
        KitchenCounterBlock(Blocks.WARPED_PLANKS, Blocks.WARPED_STEM).registerAs("warped_kitchen_counter")

    // Stripped kitchen counters
    val strippedOakKitchenCounter =
        KitchenCounterBlock(Blocks.OAK_PLANKS, Blocks.STRIPPED_OAK_LOG).registerAs("stripped_oak_kitchen_counter")
    val strippedSpruceKitchenCounter = KitchenCounterBlock(
        Blocks.SPRUCE_PLANKS,
        Blocks.STRIPPED_SPRUCE_LOG
    ).registerAs("stripped_spruce_kitchen_counter")
    val strippedBirchKitchenCounter =
        KitchenCounterBlock(Blocks.BIRCH_PLANKS, Blocks.STRIPPED_BIRCH_LOG).registerAs("stripped_birch_kitchen_counter")
    val strippedJungleKitchenCounter = KitchenCounterBlock(
        Blocks.JUNGLE_PLANKS,
        Blocks.STRIPPED_JUNGLE_LOG
    ).registerAs("stripped_jungle_kitchen_counter")
    val strippedAcaciaKitchenCounter = KitchenCounterBlock(
        Blocks.ACACIA_PLANKS,
        Blocks.STRIPPED_ACACIA_LOG
    ).registerAs("stripped_acacia_kitchen_counter")
    val strippedDarkOakKitchenCounter = KitchenCounterBlock(
        Blocks.DARK_OAK_PLANKS,
        Blocks.STRIPPED_DARK_OAK_LOG
    ).registerAs("stripped_dark_oak_kitchen_counter")
    val strippedMangroveKitchenCounter = KitchenCounterBlock(
        Blocks.MANGROVE_PLANKS,
        Blocks.STRIPPED_MANGROVE_LOG
    ).registerAs("stripped_mangrove_kitchen_counter")
    val strippedCherryKitchenCounter = KitchenCounterBlock(
        Blocks.CHERRY_PLANKS,
        Blocks.STRIPPED_CHERRY_LOG
    ).registerAs("stripped_cherry_kitchen_counter")
    val strippedBambooKitchenCounter = KitchenCounterBlock(
        Blocks.BAMBOO_PLANKS,
        Blocks.STRIPPED_BAMBOO_BLOCK
    ).registerAs("stripped_bamboo_kitchen_counter")
    val strippedBambooMosaicKitchenCounter = KitchenCounterBlock(
        Blocks.BAMBOO_MOSAIC,
        Blocks.STRIPPED_BAMBOO_BLOCK
    ).registerAs("stripped_bamboo_mosaic_kitchen_counter")
    val strippedCrimsonKitchenCounter = KitchenCounterBlock(
        Blocks.CRIMSON_PLANKS,
        Blocks.STRIPPED_CRIMSON_STEM
    ).registerAs("stripped_crimson_kitchen_counter")
    val strippedWarpedKitchenCounter = KitchenCounterBlock(
        Blocks.WARPED_PLANKS,
        Blocks.STRIPPED_WARPED_STEM
    ).registerAs("stripped_warped_kitchen_counter")

    // Kitchen cabinets
    val oakKitchenCabinet = KitchenCabinetBlock(Blocks.OAK_PLANKS, Blocks.OAK_LOG).registerAs("oak_kitchen_cabinet")
    val spruceKitchenCabinet =
        KitchenCabinetBlock(Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG).registerAs("spruce_kitchen_cabinet")
    val birchKitchenCabinet =
        KitchenCabinetBlock(Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG).registerAs("birch_kitchen_cabinet")
    val jungleKitchenCabinet =
        KitchenCabinetBlock(Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG).registerAs("jungle_kitchen_cabinet")
    val acaciaKitchenCabinet =
        KitchenCabinetBlock(Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG).registerAs("acacia_kitchen_cabinet")
    val darkOakKitchenCabinet =
        KitchenCabinetBlock(Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG).registerAs("dark_oak_kitchen_cabinet")
    val mangroveKitchenCabinet =
        KitchenCabinetBlock(Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG).registerAs("mangrove_kitchen_cabinet")
    val cherryKitchenCabinet =
        KitchenCabinetBlock(Blocks.CHERRY_PLANKS, Blocks.CHERRY_LOG).registerAs("cherry_kitchen_cabinet")
    val bambooKitchenCabinet =
        KitchenCabinetBlock(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK).registerAs("bamboo_kitchen_cabinet")
    val bambooMosaicKitchenCabinet =
        KitchenCabinetBlock(Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_BLOCK).registerAs("bamboo_mosaic_kitchen_cabinet")
    val crimsonKitchenCabinet =
        KitchenCabinetBlock(Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM).registerAs("crimson_kitchen_cabinet")
    val warpedKitchenCabinet =
        KitchenCabinetBlock(Blocks.WARPED_PLANKS, Blocks.WARPED_STEM).registerAs("warped_kitchen_cabinet")

    // Stripped kitchen cabinets
    val strippedOakKitchenCabinet =
        KitchenCabinetBlock(Blocks.OAK_PLANKS, Blocks.STRIPPED_OAK_LOG).registerAs("stripped_oak_kitchen_cabinet")
    val strippedSpruceKitchenCabinet = KitchenCabinetBlock(
        Blocks.SPRUCE_PLANKS,
        Blocks.STRIPPED_SPRUCE_LOG
    ).registerAs("stripped_spruce_kitchen_cabinet")
    val strippedBirchKitchenCabinet =
        KitchenCabinetBlock(Blocks.BIRCH_PLANKS, Blocks.STRIPPED_BIRCH_LOG).registerAs("stripped_birch_kitchen_cabinet")
    val strippedJungleKitchenCabinet = KitchenCabinetBlock(
        Blocks.JUNGLE_PLANKS,
        Blocks.STRIPPED_JUNGLE_LOG
    ).registerAs("stripped_jungle_kitchen_cabinet")
    val strippedAcaciaKitchenCabinet = KitchenCabinetBlock(
        Blocks.ACACIA_PLANKS,
        Blocks.STRIPPED_ACACIA_LOG
    ).registerAs("stripped_acacia_kitchen_cabinet")
    val strippedDarkOakKitchenCabinet = KitchenCabinetBlock(
        Blocks.DARK_OAK_PLANKS,
        Blocks.STRIPPED_DARK_OAK_LOG
    ).registerAs("stripped_dark_oak_kitchen_cabinet")
    val strippedMangroveKitchenCabinet = KitchenCabinetBlock(
        Blocks.MANGROVE_PLANKS,
        Blocks.STRIPPED_MANGROVE_LOG
    ).registerAs("stripped_mangrove_kitchen_cabinet")
    val strippedCherryKitchenCabinet = KitchenCabinetBlock(
        Blocks.CHERRY_PLANKS,
        Blocks.STRIPPED_CHERRY_LOG
    ).registerAs("stripped_cherry_kitchen_cabinet")
    val strippedBambooKitchenCabinet = KitchenCabinetBlock(
        Blocks.BAMBOO_PLANKS,
        Blocks.STRIPPED_BAMBOO_BLOCK
    ).registerAs("stripped_bamboo_kitchen_cabinet")
    val strippedBambooMosaicKitchenCabinet = KitchenCabinetBlock(
        Blocks.BAMBOO_MOSAIC,
        Blocks.STRIPPED_BAMBOO_BLOCK
    ).registerAs("stripped_bamboo_mosaic_kitchen_cabinet")
    val strippedCrimsonKitchenCabinet = KitchenCabinetBlock(
        Blocks.CRIMSON_PLANKS,
        Blocks.STRIPPED_CRIMSON_STEM
    ).registerAs("stripped_crimson_kitchen_cabinet")
    val strippedWarpedKitchenCabinet = KitchenCabinetBlock(
        Blocks.WARPED_PLANKS,
        Blocks.STRIPPED_WARPED_STEM
    ).registerAs("stripped_warped_kitchen_cabinet")


    // Walls
    val oakPlankWall = CustomWallBlock(Blocks.OAK_PLANKS).registerAs("oak_plank_wall")
    val sprucePlankWall = CustomWallBlock(Blocks.SPRUCE_PLANKS).registerAs("spruce_plank_wall")
    val birchPlankWall = CustomWallBlock(Blocks.BIRCH_PLANKS).registerAs("birch_plank_wall")
    val junglePlankWall = CustomWallBlock(Blocks.JUNGLE_PLANKS).registerAs("jungle_plank_wall")
    val acaciaPlankWall = CustomWallBlock(Blocks.ACACIA_PLANKS).registerAs("acacia_plank_wall")
    val darkOakPlankWall = CustomWallBlock(Blocks.DARK_OAK_PLANKS).registerAs("dark_oak_plank_wall")
    val mangrovePlankWall = CustomWallBlock(Blocks.MANGROVE_PLANKS).registerAs("mangrove_plank_wall")
    val cherryPlankWall = CustomWallBlock(Blocks.CHERRY_PLANKS).registerAs("cherry_plank_wall")
    val bambooPlankWall = CustomWallBlock(Blocks.BAMBOO_PLANKS).registerAs("bamboo_plank_wall")
    val bambooMosaicPlankWall = CustomWallBlock(Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_wall")
    val crimsonPlankWall = CustomWallBlock(Blocks.CRIMSON_PLANKS).registerAs("crimson_plank_wall")
    val warpedPlankWall = CustomWallBlock(Blocks.WARPED_PLANKS).registerAs("warped_plank_wall")

    // Modern fences
    val modernOakFence =
        ModernFenceBlock(Blocks.OAK_FENCE, Blocks.STRIPPED_OAK_LOG, Blocks.OAK_LOG).registerAs("modern_oak_fence")
    val modernSpruceFence = ModernFenceBlock(
        Blocks.SPRUCE_FENCE,
        Blocks.STRIPPED_SPRUCE_LOG,
        Blocks.SPRUCE_LOG
    ).registerAs("modern_spruce_fence")
    val modernBirchFence = ModernFenceBlock(
        Blocks.BIRCH_FENCE,
        Blocks.STRIPPED_BIRCH_LOG,
        Blocks.BIRCH_LOG
    ).registerAs("modern_birch_fence")
    val modernJungleFence = ModernFenceBlock(
        Blocks.JUNGLE_FENCE,
        Blocks.STRIPPED_JUNGLE_LOG,
        Blocks.JUNGLE_LOG
    ).registerAs("modern_jungle_fence")
    val modernAcaciaFence = ModernFenceBlock(
        Blocks.ACACIA_FENCE,
        Blocks.STRIPPED_ACACIA_LOG,
        Blocks.ACACIA_LOG
    ).registerAs("modern_acacia_fence")
    val modernDarkOakFence = ModernFenceBlock(
        Blocks.DARK_OAK_FENCE,
        Blocks.STRIPPED_DARK_OAK_LOG,
        Blocks.DARK_OAK_LOG
    ).registerAs("modern_dark_oak_fence")
    val modernMangroveFence = ModernFenceBlock(
        Blocks.MANGROVE_FENCE,
        Blocks.STRIPPED_MANGROVE_LOG,
        Blocks.MANGROVE_LOG
    ).registerAs("modern_mangrove_fence")
    val modernCherryFence = ModernFenceBlock(
        Blocks.CHERRY_FENCE,
        Blocks.STRIPPED_CHERRY_LOG,
        Blocks.CHERRY_LOG
    ).registerAs("modern_cherry_fence")
    val modernBambooFence = ModernFenceBlock(
        Blocks.BAMBOO_FENCE,
        Blocks.STRIPPED_BAMBOO_BLOCK,
        Blocks.BAMBOO_BLOCK
    ).registerAs("modern_bamboo_fence")
    val modernCrimsonFence = ModernFenceBlock(
        Blocks.CRIMSON_FENCE,
        Blocks.STRIPPED_CRIMSON_STEM,
        Blocks.CRIMSON_STEM
    ).registerAs("modern_crimson_fence")
    val modernWarpedFence = ModernFenceBlock(
        Blocks.WARPED_FENCE,
        Blocks.STRIPPED_WARPED_STEM,
        Blocks.WARPED_STEM
    ).registerAs("modern_warped_fence")

    // Modern fence gates
    val modernOakFenceGate = ModernFenceGateBlock(
        Blocks.OAK_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_OAK_LOG
    ).registerAs("modern_oak_fence_gate")
    val modernSpruceFenceGate = ModernFenceGateBlock(
        Blocks.SPRUCE_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_SPRUCE_LOG
    ).registerAs("modern_spruce_fence_gate")
    val modernBirchFenceGate = ModernFenceGateBlock(
        Blocks.BIRCH_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_BIRCH_LOG
    ).registerAs("modern_birch_fence_gate")
    val modernJungleFenceGate = ModernFenceGateBlock(
        Blocks.JUNGLE_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_JUNGLE_LOG
    ).registerAs("modern_jungle_fence_gate")
    val modernAcaciaFenceGate = ModernFenceGateBlock(
        Blocks.ACACIA_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_ACACIA_LOG
    ).registerAs("modern_acacia_fence_gate")
    val modernDarkOakFenceGate = ModernFenceGateBlock(
        Blocks.DARK_OAK_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_DARK_OAK_LOG
    ).registerAs("modern_dark_oak_fence_gate")
    val modernMangroveFenceGate = ModernFenceGateBlock(
        Blocks.MANGROVE_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_MANGROVE_LOG
    ).registerAs("modern_mangrove_fence_gate")
    val modernCherryFenceGate = ModernFenceGateBlock(
        Blocks.CHERRY_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_CHERRY_LOG
    ).registerAs("modern_cherry_fence_gate")
    val modernBambooFenceGate = ModernFenceGateBlock(
        Blocks.BAMBOO_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_BAMBOO_BLOCK
    ).registerAs("modern_bamboo_fence_gate")
    val modernCrimsonFenceGate = ModernFenceGateBlock(
        Blocks.CRIMSON_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_CRIMSON_STEM
    ).registerAs("modern_crimson_fence_gate")
    val modernWarpedFenceGate = ModernFenceGateBlock(
        Blocks.WARPED_FENCE_GATE as FenceGateBlock,
        Blocks.STRIPPED_WARPED_STEM
    ).registerAs("modern_warped_fence_gate")

    // Plank ladders
    val oakPlankLadder = PlankLadderBlock(Blocks.OAK_PLANKS).registerAs("oak_plank_ladder")
    val sprucePlankLadder = PlankLadderBlock(Blocks.SPRUCE_PLANKS).registerAs("spruce_plank_ladder")
    val birchPlankLadder = PlankLadderBlock(Blocks.BIRCH_PLANKS).registerAs("birch_plank_ladder")
    val junglePlankLadder = PlankLadderBlock(Blocks.JUNGLE_PLANKS).registerAs("jungle_plank_ladder")
    val acaciaPlankLadder = PlankLadderBlock(Blocks.ACACIA_PLANKS).registerAs("acacia_plank_ladder")
    val darkOakPlankLadder = PlankLadderBlock(Blocks.DARK_OAK_PLANKS).registerAs("dark_oak_plank_ladder")
    val mangrovePlankLadder = PlankLadderBlock(Blocks.MANGROVE_PLANKS).registerAs("mangrove_plank_ladder")
    val cherryPlankLadder = PlankLadderBlock(Blocks.CHERRY_PLANKS).registerAs("cherry_plank_ladder")
    val bambooPlankLadder = PlankLadderBlock(Blocks.BAMBOO_PLANKS).registerAs("bamboo_plank_ladder")
    val bambooMosaicPlankLadder = PlankLadderBlock(Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_ladder")
    val crimsonPlankLadder = PlankLadderBlock(Blocks.CRIMSON_PLANKS).registerAs("crimson_plank_ladder")
    val warpedPlankLadder = PlankLadderBlock(Blocks.WARPED_PLANKS).registerAs("warped_plank_ladder")

    // Stripped ladders
    val strippedOakLadder = ConnectingLadderBlock(Blocks.STRIPPED_OAK_LOG).registerAs("stripped_oak_ladder")
    val strippedSpruceLadder = ConnectingLadderBlock(Blocks.STRIPPED_SPRUCE_LOG).registerAs("stripped_spruce_ladder")
    val strippedBirchLadder = ConnectingLadderBlock(Blocks.STRIPPED_BIRCH_LOG).registerAs("stripped_birch_ladder")
    val strippedJungleLadder = ConnectingLadderBlock(Blocks.STRIPPED_JUNGLE_LOG).registerAs("stripped_jungle_ladder")
    val strippedAcaciaLadder = ConnectingLadderBlock(Blocks.STRIPPED_ACACIA_LOG).registerAs("stripped_acacia_ladder")
    val strippedDarkOakLadder =
        ConnectingLadderBlock(Blocks.STRIPPED_DARK_OAK_LOG).registerAs("stripped_dark_oak_ladder")
    val strippedMangroveLadder =
        ConnectingLadderBlock(Blocks.STRIPPED_MANGROVE_LOG).registerAs("stripped_mangrove_ladder")
    val strippedCherryLadder = ConnectingLadderBlock(Blocks.STRIPPED_CHERRY_LOG).registerAs("stripped_cherry_ladder")
    val strippedBambooLadder = ConnectingLadderBlock(Blocks.STRIPPED_BAMBOO_BLOCK).registerAs("stripped_bamboo_ladder")
    val strippedCrimsonLadder =
        ConnectingLadderBlock(Blocks.STRIPPED_CRIMSON_STEM).registerAs("stripped_crimson_ladder")
    val strippedWarpedLadder = ConnectingLadderBlock(Blocks.STRIPPED_WARPED_STEM).registerAs("stripped_warped_ladder")

    // ContextlessType ladders
    val oakSimpleLadder = SimpleLadderBlock(Blocks.OAK_PLANKS).registerAs("oak_simple_ladder")
    val spruceSimpleLadder = SimpleLadderBlock(Blocks.SPRUCE_PLANKS).registerAs("spruce_simple_ladder")
    val birchSimpleLadder = SimpleLadderBlock(Blocks.BIRCH_PLANKS).registerAs("birch_simple_ladder")
    val jungleSimpleLadder = SimpleLadderBlock(Blocks.JUNGLE_PLANKS).registerAs("jungle_simple_ladder")
    val acaciaSimpleLadder = SimpleLadderBlock(Blocks.ACACIA_PLANKS).registerAs("acacia_simple_ladder")
    val darkOakSimpleLadder =
        SimpleLadderBlock(Blocks.DARK_OAK_PLANKS).registerAs("dark_oak_simple_ladder")
    val mangroveSimpleLadder =
        SimpleLadderBlock(Blocks.MANGROVE_PLANKS).registerAs("mangrove_simple_ladder")
    val cherrySimpleLadder = SimpleLadderBlock(Blocks.CHERRY_PLANKS).registerAs("cherry_simple_ladder")
    val bambooSimpleLadder = SimpleLadderBlock(Blocks.BAMBOO_PLANKS).registerAs("bamboo_simple_ladder")
    val crimsonSimpleLadder =
        SimpleLadderBlock(Blocks.CRIMSON_PLANKS).registerAs("crimson_simple_ladder")
    val warpedSimpleLadder = SimpleLadderBlock(Blocks.WARPED_PLANKS).registerAs("warped_simple_ladder")

    // Plank support beams
    val oakPlankSupportBeam = SupportBeamBlock(Blocks.OAK_PLANKS).registerAs("oak_plank_support_beam")
    val sprucePlankSupportBeam = SupportBeamBlock(Blocks.SPRUCE_PLANKS).registerAs("spruce_plank_support_beam")
    val birchPlankSupportBeam = SupportBeamBlock(Blocks.BIRCH_PLANKS).registerAs("birch_plank_support_beam")
    val junglePlankSupportBeam = SupportBeamBlock(Blocks.JUNGLE_PLANKS).registerAs("jungle_plank_support_beam")
    val acaciaPlankSupportBeam = SupportBeamBlock(Blocks.ACACIA_PLANKS).registerAs("acacia_plank_support_beam")
    val darkOakPlankSupportBeam = SupportBeamBlock(Blocks.DARK_OAK_PLANKS).registerAs("dark_oak_plank_support_beam")
    val mangrovePlankSupportBeam = SupportBeamBlock(Blocks.MANGROVE_PLANKS).registerAs("mangrove_plank_support_beam")
    val cherryPlankSupportBeam = SupportBeamBlock(Blocks.CHERRY_PLANKS).registerAs("cherry_plank_support_beam")
    val bambooPlankSupportBeam = SupportBeamBlock(Blocks.BAMBOO_PLANKS).registerAs("bamboo_plank_support_beam")
    val bambooMosaicSupportBeam = SupportBeamBlock(Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_support_beam")
    val crimsonPlankSupportBeam = SupportBeamBlock(Blocks.CRIMSON_PLANKS).registerAs("crimson_plank_support_beam")
    val warpedPlankSupportBeam = SupportBeamBlock(Blocks.WARPED_PLANKS).registerAs("warped_plank_support_beam")

    // Wood support beams
    val oakSupportBeam = SupportBeamBlock(Blocks.OAK_LOG).registerAs("oak_support_beam")
    val spruceSupportBeam = SupportBeamBlock(Blocks.SPRUCE_LOG).registerAs("spruce_support_beam")
    val birchSupportBeam = SupportBeamBlock(Blocks.BIRCH_LOG).registerAs("birch_support_beam")
    val jungleSupportBeam = SupportBeamBlock(Blocks.JUNGLE_LOG).registerAs("jungle_support_beam")
    val acaciaSupportBeam = SupportBeamBlock(Blocks.ACACIA_LOG).registerAs("acacia_support_beam")
    val darkOakSupportBeam = SupportBeamBlock(Blocks.DARK_OAK_LOG).registerAs("dark_oak_support_beam")
    val mangroveSupportBeam = SupportBeamBlock(Blocks.MANGROVE_LOG).registerAs("mangrove_support_beam")
    val cherrySupportBeam = SupportBeamBlock(Blocks.CHERRY_LOG).registerAs("cherry_support_beam")
    val bambooSupportBeam = SupportBeamBlock(Blocks.BAMBOO_BLOCK).registerAs("bamboo_support_beam")
    val crimsonSupportBeam = SupportBeamBlock(Blocks.CRIMSON_STEM).registerAs("crimson_support_beam")
    val warpedSupportBeam = SupportBeamBlock(Blocks.WARPED_STEM).registerAs("warped_support_beam")

    // Stripped support beams
    val strippedOakSupportBeam = SupportBeamBlock(Blocks.STRIPPED_OAK_LOG).registerAs("stripped_oak_support_beam")
    val strippedSpruceSupportBeam =
        SupportBeamBlock(Blocks.STRIPPED_SPRUCE_LOG).registerAs("stripped_spruce_support_beam")
    val strippedBirchSupportBeam = SupportBeamBlock(Blocks.STRIPPED_BIRCH_LOG).registerAs("stripped_birch_support_beam")
    val strippedJungleSupportBeam =
        SupportBeamBlock(Blocks.STRIPPED_JUNGLE_LOG).registerAs("stripped_jungle_support_beam")
    val strippedAcaciaSupportBeam =
        SupportBeamBlock(Blocks.STRIPPED_ACACIA_LOG).registerAs("stripped_acacia_support_beam")
    val strippedDarkOakSupportBeam =
        SupportBeamBlock(Blocks.STRIPPED_DARK_OAK_LOG).registerAs("stripped_dark_oak_support_beam")
    val strippedMangroveSupportBeam =
        SupportBeamBlock(Blocks.STRIPPED_MANGROVE_LOG).registerAs("stripped_mangrove_support_beam")
    val strippedCherrySupportBeam =
        SupportBeamBlock(Blocks.STRIPPED_CHERRY_LOG).registerAs("stripped_cherry_support_beam")
    val strippedBambooSupportBeam =
        SupportBeamBlock(Blocks.STRIPPED_BAMBOO_BLOCK).registerAs("stripped_bamboo_support_beam")
    val strippedCrimsonSupportBeam =
        SupportBeamBlock(Blocks.STRIPPED_CRIMSON_STEM).registerAs("stripped_crimson_support_beam")
    val strippedWarpedSupportBeam =
        SupportBeamBlock(Blocks.STRIPPED_WARPED_STEM).registerAs("stripped_warped_support_beam")

    // Thin pillars
    val thinOakPillar = ThinPillarBlock(Blocks.OAK_PLANKS).registerAs("thin_oak_plank_pillar")
    val thinSprucePillar = ThinPillarBlock(Blocks.SPRUCE_PLANKS).registerAs("thin_spruce_plank_pillar")
    val thinBirchPillar = ThinPillarBlock(Blocks.BIRCH_PLANKS).registerAs("thin_birch_plank_pillar")
    val thinJunglePillar = ThinPillarBlock(Blocks.JUNGLE_PLANKS).registerAs("thin_jungle_plank_pillar")
    val thinAcaciaPillar = ThinPillarBlock(Blocks.ACACIA_PLANKS).registerAs("thin_acacia_plank_pillar")
    val thinDarkOakPillar = ThinPillarBlock(Blocks.DARK_OAK_PLANKS).registerAs("thin_dark_oak_plank_pillar")
    val thinMangrovePillar = ThinPillarBlock(Blocks.MANGROVE_PLANKS).registerAs("thin_mangrove_plank_pillar")
    val thinCherryPillar = ThinPillarBlock(Blocks.CHERRY_PLANKS).registerAs("thin_cherry_plank_pillar")
    val thinBambooPillar = ThinPillarBlock(Blocks.BAMBOO_PLANKS).registerAs("thin_bamboo_plank_pillar")
    val thinBambooMosaicPillar = ThinPillarBlock(Blocks.BAMBOO_MOSAIC).registerAs("thin_bamboo_mosaic_pillar")
    val thinCrimsonPillar = ThinPillarBlock(Blocks.CRIMSON_PLANKS).registerAs("thin_crimson_plank_pillar")
    val thinWarpedPillar = ThinPillarBlock(Blocks.WARPED_PLANKS).registerAs("thin_warped_plank_pillar")

    // Thick pillars
    val thickOakPillar = ThickPillarBlock(Blocks.OAK_PLANKS).registerAs("thick_oak_plank_pillar")
    val thickSprucePillar = ThickPillarBlock(Blocks.SPRUCE_PLANKS).registerAs("thick_spruce_plank_pillar")
    val thickBirchPillar = ThickPillarBlock(Blocks.BIRCH_PLANKS).registerAs("thick_birch_plank_pillar")
    val thickJunglePillar = ThickPillarBlock(Blocks.JUNGLE_PLANKS).registerAs("thick_jungle_plank_pillar")
    val thickAcaciaPillar = ThickPillarBlock(Blocks.ACACIA_PLANKS).registerAs("thick_acacia_plank_pillar")
    val thickDarkOakPillar = ThickPillarBlock(Blocks.DARK_OAK_PLANKS).registerAs("thick_dark_oak_plank_pillar")
    val thickMangrovePillar = ThickPillarBlock(Blocks.MANGROVE_PLANKS).registerAs("thick_mangrove_plank_pillar")
    val thickCherryPillar = ThickPillarBlock(Blocks.CHERRY_PLANKS).registerAs("thick_cherry_plank_pillar")
    val thickBambooPillar = ThickPillarBlock(Blocks.BAMBOO_PLANKS).registerAs("thick_bamboo_plank_pillar")
    val thickBambooMosaicPillar = ThickPillarBlock(Blocks.BAMBOO_MOSAIC).registerAs("thick_bamboo_mosaic_pillar")
    val thickCrimsonPillar = ThickPillarBlock(Blocks.CRIMSON_PLANKS).registerAs("thick_crimson_plank_pillar")
    val thickWarpedPillar = ThickPillarBlock(Blocks.WARPED_PLANKS).registerAs("thick_warped_plank_pillar")

    // Crates
    val oakCrate = CrateBlock(Blocks.OAK_PLANKS, Blocks.OAK_LOG).registerAs("oak_crate", 4)
    val spruceCrate = CrateBlock(Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG).registerAs("spruce_crate", 4)
    val birchCrate = CrateBlock(Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG).registerAs("birch_crate", 4)
    val jungleCrate = CrateBlock(Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG).registerAs("jungle_crate", 4)
    val acaciaCrate = CrateBlock(Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG).registerAs("acacia_crate", 4)
    val darkOakCrate = CrateBlock(Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG).registerAs("dark_oak_crate", 4)
    val mangroveCrate = CrateBlock(Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG).registerAs("mangrove_crate", 4)
    val cherryCrate = CrateBlock(Blocks.CHERRY_PLANKS, Blocks.CHERRY_LOG).registerAs("cherry_crate", 4)
    val bambooCrate = CrateBlock(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK).registerAs("bamboo_crate", 4)
    val bambooMosaicCrate = CrateBlock(Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_BLOCK).registerAs("bamboo_mosaic_crate", 4)
    val crimsonCrate = CrateBlock(Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM).registerAs("crimson_crate", 4)
    val warpedCrate = CrateBlock(Blocks.WARPED_PLANKS, Blocks.WARPED_STEM).registerAs("warped_crate", 4)

    // Stripped crates
    val strippedOakCrate = CrateBlock(Blocks.OAK_PLANKS, Blocks.STRIPPED_OAK_LOG).registerAs("stripped_oak_crate", 4)
    val strippedSpruceCrate =
        CrateBlock(Blocks.SPRUCE_PLANKS, Blocks.STRIPPED_SPRUCE_LOG).registerAs("stripped_spruce_crate", 4)
    val strippedBirchCrate =
        CrateBlock(Blocks.BIRCH_PLANKS, Blocks.STRIPPED_BIRCH_LOG).registerAs("stripped_birch_crate", 4)
    val strippedJungleCrate =
        CrateBlock(Blocks.JUNGLE_PLANKS, Blocks.STRIPPED_JUNGLE_LOG).registerAs("stripped_jungle_crate", 4)
    val strippedAcaciaCrate =
        CrateBlock(Blocks.ACACIA_PLANKS, Blocks.STRIPPED_ACACIA_LOG).registerAs("stripped_acacia_crate", 4)
    val strippedDarkOakCrate =
        CrateBlock(Blocks.DARK_OAK_PLANKS, Blocks.STRIPPED_DARK_OAK_LOG).registerAs("stripped_dark_oak_crate", 4)
    val strippedMangroveCrate =
        CrateBlock(Blocks.MANGROVE_PLANKS, Blocks.STRIPPED_MANGROVE_LOG).registerAs("stripped_mangrove_crate", 4)
    val strippedCherryCrate =
        CrateBlock(Blocks.CHERRY_PLANKS, Blocks.STRIPPED_CHERRY_LOG).registerAs("stripped_cherry_crate", 4)
    val strippedBambooCrate =
        CrateBlock(Blocks.BAMBOO_PLANKS, Blocks.STRIPPED_BAMBOO_BLOCK).registerAs("stripped_bamboo_crate", 4)
    val strippedBambooMosaicCrate =
        CrateBlock(Blocks.BAMBOO_MOSAIC, Blocks.STRIPPED_BAMBOO_BLOCK).registerAs("stripped_bamboo_mosaic_crate", 4)
    val strippedCrimsonCrate =
        CrateBlock(Blocks.CRIMSON_PLANKS, Blocks.STRIPPED_CRIMSON_STEM).registerAs("stripped_crimson_crate", 4)
    val strippedWarpedCrate =
        CrateBlock(Blocks.WARPED_PLANKS, Blocks.STRIPPED_WARPED_STEM).registerAs("stripped_warped_crate", 4)

    // Thin bookshelves
    val oakPlankBookshelf = ThinBookshelfBlock(Blocks.OAK_PLANKS).registerAs("oak_plank_bookshelf")
    val sprucePlankBookshelf = ThinBookshelfBlock(Blocks.SPRUCE_PLANKS).registerAs("spruce_plank_bookshelf")
    val birchPlankBookshelf = ThinBookshelfBlock(Blocks.BIRCH_PLANKS).registerAs("birch_plank_bookshelf")
    val junglePlankBookshelf = ThinBookshelfBlock(Blocks.JUNGLE_PLANKS).registerAs("jungle_plank_bookshelf")
    val acaciaPlankBookshelf = ThinBookshelfBlock(Blocks.ACACIA_PLANKS).registerAs("acacia_plank_bookshelf")
    val darkOakPlankBookshelf = ThinBookshelfBlock(Blocks.DARK_OAK_PLANKS).registerAs("dark_oak_plank_bookshelf")
    val mangrovePlankBookshelf = ThinBookshelfBlock(Blocks.MANGROVE_PLANKS).registerAs("mangrove_plank_bookshelf")
    val cherryPlankBookshelf = ThinBookshelfBlock(Blocks.CHERRY_PLANKS).registerAs("cherry_plank_bookshelf")
    val bambooPlankBookshelf = ThinBookshelfBlock(Blocks.BAMBOO_PLANKS).registerAs("bamboo_plank_bookshelf")
    val bambooMosaicBookshelf = ThinBookshelfBlock(Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_bookshelf")
    val crimsonPlankBookshelf = ThinBookshelfBlock(Blocks.CRIMSON_PLANKS).registerAs("crimson_plank_bookshelf")
    val warpedPlankBookshelf = ThinBookshelfBlock(Blocks.WARPED_PLANKS).registerAs("warped_plank_bookshelf")

    // Bracket shelves
    val oakBracketShelf = BracketShelfBlock(Blocks.OAK_PLANKS).registerAs("oak_bracket_shelf")
    val spruceBracketShelf = BracketShelfBlock(Blocks.SPRUCE_PLANKS).registerAs("spruce_bracket_shelf")
    val birchBracketShelf = BracketShelfBlock(Blocks.BIRCH_PLANKS).registerAs("birch_bracket_shelf")
    val jungleBracketShelf = BracketShelfBlock(Blocks.JUNGLE_PLANKS).registerAs("jungle_bracket_shelf")
    val acaciaBracketShelf = BracketShelfBlock(Blocks.ACACIA_PLANKS).registerAs("acacia_bracket_shelf")
    val darkOakBracketShelf = BracketShelfBlock(Blocks.DARK_OAK_PLANKS).registerAs("dark_oak_bracket_shelf")
    val mangroveBracketShelf = BracketShelfBlock(Blocks.MANGROVE_PLANKS).registerAs("mangrove_bracket_shelf")
    val cherryBracketShelf = BracketShelfBlock(Blocks.CHERRY_PLANKS).registerAs("cherry_bracket_shelf")
    val bambooBracketShelf = BracketShelfBlock(Blocks.BAMBOO_PLANKS).registerAs("bamboo_bracket_shelf")
    val bambooMosaicBracketShelf = BracketShelfBlock(Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_bracket_shelf")
    val crimsonBracketShelf = BracketShelfBlock(Blocks.CRIMSON_PLANKS).registerAs("crimson_bracket_shelf")
    val warpedBracketShelf = BracketShelfBlock(Blocks.WARPED_PLANKS).registerAs("warped_bracket_shelf")

    // Plank carpets
    val oakPlankCarpet = CustomCarpetBlock(Blocks.OAK_PLANKS).registerAs("oak_plank_carpet")
    val sprucePlankCarpet = CustomCarpetBlock(Blocks.SPRUCE_PLANKS).registerAs("spruce_plank_carpet")
    val birchPlankCarpet = CustomCarpetBlock(Blocks.BIRCH_PLANKS).registerAs("birch_plank_carpet")
    val junglePlankCarpet = CustomCarpetBlock(Blocks.JUNGLE_PLANKS).registerAs("jungle_plank_carpet")
    val acaciaPlankCarpet = CustomCarpetBlock(Blocks.ACACIA_PLANKS).registerAs("acacia_plank_carpet")
    val darkOakPlankCarpet = CustomCarpetBlock(Blocks.DARK_OAK_PLANKS).registerAs("dark_oak_plank_carpet")
    val mangrovePlankCarpet = CustomCarpetBlock(Blocks.MANGROVE_PLANKS).registerAs("mangrove_plank_carpet")
    val cherryPlankCarpet = CustomCarpetBlock(Blocks.CHERRY_PLANKS).registerAs("cherry_plank_carpet")
    val bambooPlankCarpet = CustomCarpetBlock(Blocks.BAMBOO_PLANKS).registerAs("bamboo_plank_carpet")
    val bambooMosaicCarpet = CustomCarpetBlock(Blocks.BAMBOO_MOSAIC).registerAs("bamboo_mosaic_carpet")
    val crimsonPlankCarpet = CustomCarpetBlock(Blocks.CRIMSON_PLANKS).registerAs("crimson_plank_carpet")
    val warpedPlankCarpet = CustomCarpetBlock(Blocks.WARPED_PLANKS).registerAs("warped_plank_carpet")


    private fun Block.registerAs(id: String, maxStackSize: Int = 64): Block {
        return registerAs(id.toIdentifier(), maxStackSize)
    }

    private fun Block.registerAs(identifier: ResourceLocation, maxStackSize: Int = 64): Block {
        return Registry.register(BuiltInRegistries.BLOCK, identifier, this).also {
            registries += it
            blockItems[identifier] = BlockItemRegistration(it, maxStackSize)
        }
    }
}

inline val Block.itemModelId: ResourceLocation
    get() = id.withPrefix("item/")

inline val Block.id: ResourceLocation
    get() = BuiltInRegistries.BLOCK.getKey(this)

inline val Block.modelId: ResourceLocation
    get() = ModelLocationUtils.getModelLocation(this)

inline val Block.textureId: ResourceLocation
    get() = TextureMapping.getBlockTexture(this)

/**
 * Infers the vanilla wood type from the block ID. Only use this for registered wood variants; IDs without a
 * wood name fail when the inferred type is read.
 */
inline val Block.woodType: WoodType
    get() {
        lateinit var type: WoodType
        WoodType.values().forEach {
            if (this.id.path.contains(it.name())) {
                type = it
                if (!it.name().contains("dark") && !this.id.path.contains("dark")) {
                    return@forEach
                }
            }
        }
        return type
    }

inline val Block.isStripped: Boolean
    get() = id.path.contains("stripped")

inline val Block.isPlank
    get() = id.path.contains("plank")

inline val Block.item: Item
    get() = this.asItem()

inline val Block.defaultItemStack: ItemStack
    get() = this.item.defaultInstance
