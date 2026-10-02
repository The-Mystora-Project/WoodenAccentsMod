package com.github.mystery2099.woodenAccentsMod.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory

/** Mirrors [net.minecraft.client.gui.screens.inventory.StonecutterScreen] and reuses its textures. */
class WoodCutterScreen(menu: WoodCutterMenu, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<WoodCutterMenu>(menu, playerInventory, title) {

    private var scrollOffs = 0.0f
    private var scrolling = false
    private var startIndex = 0
    private var displayRecipes = false

    init {
        menu.registerUpdateListener(::containerChanged)
        titleLabelY--
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(graphics, mouseX, mouseY, partialTick)
        renderTooltip(graphics, mouseX, mouseY)
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        graphics.blit(BG_LOCATION, leftPos, topPos, 0, 0, imageWidth, imageHeight)
        val scrollerOffset = (41.0f * scrollOffs).toInt()
        val scrollerSprite = if (isScrollBarActive()) SCROLLER_SPRITE else SCROLLER_DISABLED_SPRITE
        graphics.blitSprite(scrollerSprite, leftPos + 119, topPos + 15 + scrollerOffset, SCROLLER_WIDTH, SCROLLER_HEIGHT)
        val recipesX = leftPos + RECIPES_X
        val recipesY = topPos + RECIPES_Y
        val endIndex = startIndex + VISIBLE_RECIPES
        renderButtons(graphics, mouseX, mouseY, recipesX, recipesY, endIndex)
        renderRecipes(graphics, recipesX, recipesY, endIndex)
    }

    override fun renderTooltip(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        super.renderTooltip(graphics, mouseX, mouseY)
        if (!displayRecipes) return

        val recipesX = leftPos + RECIPES_X
        val recipesY = topPos + RECIPES_Y
        for (index in startIndex until minOf(startIndex + VISIBLE_RECIPES, menu.numRecipes)) {
            val visibleIndex = index - startIndex
            val x = recipesX + visibleIndex % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH
            val y = recipesY + visibleIndex / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2
            if (mouseX >= x && mouseX < x + RECIPES_IMAGE_SIZE_WIDTH && mouseY >= y && mouseY < y + RECIPES_IMAGE_SIZE_HEIGHT) {
                graphics.renderTooltip(font, menu.getResultItem(index), mouseX, mouseY)
            }
        }
    }

    private fun renderButtons(graphics: GuiGraphics, mouseX: Int, mouseY: Int, recipesX: Int, recipesY: Int, endIndex: Int) {
        for (index in startIndex until minOf(endIndex, menu.numRecipes)) {
            val visibleIndex = index - startIndex
            val x = recipesX + visibleIndex % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH
            val y = recipesY + visibleIndex / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2
            val sprite = when {
                index == menu.selectedRecipeIndex -> RECIPE_SELECTED_SPRITE
                mouseX >= x && mouseY >= y && mouseX < x + RECIPES_IMAGE_SIZE_WIDTH && mouseY < y + RECIPES_IMAGE_SIZE_HEIGHT ->
                    RECIPE_HIGHLIGHTED_SPRITE
                else -> RECIPE_SPRITE
            }
            graphics.blitSprite(sprite, x, y - 1, RECIPES_IMAGE_SIZE_WIDTH, RECIPES_IMAGE_SIZE_HEIGHT)
        }
    }

    private fun renderRecipes(graphics: GuiGraphics, recipesX: Int, recipesY: Int, endIndex: Int) {
        for (index in startIndex until minOf(endIndex, menu.numRecipes)) {
            val visibleIndex = index - startIndex
            val x = recipesX + visibleIndex % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH
            val y = recipesY + visibleIndex / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2
            graphics.renderItem(menu.getResultItem(index), x, y)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        scrolling = false
        val minecraft = minecraft
        if (displayRecipes && minecraft != null) {
            val recipesX = leftPos + RECIPES_X
            val recipesY = topPos + RECIPES_Y
            for (index in startIndex until startIndex + VISIBLE_RECIPES) {
                val visibleIndex = index - startIndex
                val relativeX = mouseX - (recipesX + visibleIndex % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH)
                val relativeY = mouseY - (recipesY + visibleIndex / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT)
                val player = minecraft.player
                if (relativeX >= 0.0 && relativeY >= 0.0 &&
                    relativeX < RECIPES_IMAGE_SIZE_WIDTH && relativeY < RECIPES_IMAGE_SIZE_HEIGHT &&
                    player != null && menu.clickMenuButton(player, index)
                ) {
                    minecraft.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f))
                    minecraft.gameMode?.handleInventoryButtonClick(menu.containerId, index)
                    return true
                }
            }

            val scrollerX = leftPos + 119
            val scrollerY = topPos + 9
            if (mouseX >= scrollerX && mouseX < scrollerX + SCROLLER_WIDTH &&
                mouseY >= scrollerY && mouseY < scrollerY + SCROLLER_FULL_HEIGHT
            ) {
                scrolling = true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (!scrolling || !isScrollBarActive()) return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)

        val top = topPos + RECIPES_Y
        val bottom = top + SCROLLER_FULL_HEIGHT
        scrollOffs = Mth.clamp((mouseY.toFloat() - top - 7.5f) / (bottom - top - 15.0f), 0.0f, 1.0f)
        startIndex = ((scrollOffs * getOffscreenRows()).toDouble() + 0.5).toInt() * RECIPES_COLUMNS
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (isScrollBarActive()) {
            val offscreenRows = getOffscreenRows()
            scrollOffs = Mth.clamp(scrollOffs - scrollY.toFloat() / offscreenRows, 0.0f, 1.0f)
            startIndex = ((scrollOffs * offscreenRows).toDouble() + 0.5).toInt() * RECIPES_COLUMNS
        }
        return true
    }

    private fun isScrollBarActive(): Boolean = displayRecipes && menu.numRecipes > VISIBLE_RECIPES

    private fun getOffscreenRows(): Int = (menu.numRecipes + RECIPES_COLUMNS - 1) / RECIPES_COLUMNS - RECIPES_ROWS

    private fun containerChanged() {
        displayRecipes = menu.hasInputItem()
        if (!displayRecipes) {
            scrollOffs = 0.0f
            startIndex = 0
        }
    }

    companion object {
        private val SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller")
        private val SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller_disabled")
        private val RECIPE_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe_selected")
        private val RECIPE_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe_highlighted")
        private val RECIPE_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe")
        private val BG_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/stonecutter.png")
        private const val SCROLLER_WIDTH = 12
        private const val SCROLLER_HEIGHT = 15
        private const val SCROLLER_FULL_HEIGHT = 54
        private const val RECIPES_COLUMNS = 4
        private const val RECIPES_ROWS = 3
        private const val VISIBLE_RECIPES = RECIPES_COLUMNS * RECIPES_ROWS
        private const val RECIPES_IMAGE_SIZE_WIDTH = 16
        private const val RECIPES_IMAGE_SIZE_HEIGHT = 18
        private const val RECIPES_X = 52
        private const val RECIPES_Y = 14
    }
}
