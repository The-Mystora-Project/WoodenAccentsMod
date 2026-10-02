package com.github.mystery2099.woodenAccentsMod.rei

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.network.chat.Component

/** Mirrors REI's `DefaultStoneCuttingCategory` layout. */
class WoodcuttingCategory : DisplayCategory<WoodcuttingDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<out WoodcuttingDisplay> = WoodcuttingDisplay.categoryId

    override fun getIcon(): Renderer = EntryStacks.of(ModBlocks.woodCutter)

    override fun getTitle(): Component = Component.translatable("category.wooden_accents_mod.woodcutting")

    override fun setupDisplay(display: WoodcuttingDisplay, bounds: Rectangle): List<Widget> {
        val start = Point(bounds.centerX - 41, bounds.centerY - 13)
        return listOf(
            Widgets.createRecipeBase(bounds),
            Widgets.createArrow(Point(start.x + 27, start.y + 4)),
            Widgets.createResultSlotBackground(Point(start.x + 61, start.y + 5)),
            Widgets.createSlot(Point(start.x + 61, start.y + 5))
                .entries(display.outputEntries.first())
                .disableBackground()
                .markOutput(),
            Widgets.createSlot(Point(start.x + 4, start.y + 5))
                .entries(display.inputEntries.first())
                .markInput()
        )
    }

    override fun getDisplayHeight(): Int = 36
}
