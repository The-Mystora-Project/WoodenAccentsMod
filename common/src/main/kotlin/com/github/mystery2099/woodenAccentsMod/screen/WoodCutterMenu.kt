package com.github.mystery2099.woodenAccentsMod.screen

import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.recipe.ModRecipeTypes
import com.github.mystery2099.woodenAccentsMod.recipe.WoodcuttingRecipe
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.inventory.ResultContainer
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level

/** Mirrors [net.minecraft.world.inventory.StonecutterMenu], but lists woodcutting recipes. */
class WoodCutterMenu(containerId: Int, playerInventory: Inventory, private val access: ContainerLevelAccess) :
    AbstractContainerMenu(ModMenuTypes.woodCutter, containerId) {

    constructor(containerId: Int, playerInventory: Inventory) :
        this(containerId, playerInventory, ContainerLevelAccess.NULL)

    private val selectedRecipeIndexData = DataSlot.standalone()
    private val level: Level = playerInventory.player.level()
    var recipes: List<RecipeHolder<WoodcuttingRecipe>> = emptyList()
        private set
    private var input = ItemStack.EMPTY
    private var lastSoundTime = 0L
    private var slotUpdateListener = Runnable {}
    val container: Container = object : SimpleContainer(1) {
        override fun setChanged() {
            super.setChanged()
            this@WoodCutterMenu.slotsChanged(this)
            slotUpdateListener.run()
        }
    }
    private val resultContainer = ResultContainer()
    private val inputSlot: Slot = addSlot(Slot(container, INPUT_SLOT, 20, 33))
    private val resultSlot: Slot = addSlot(object : Slot(resultContainer, RESULT_SLOT, 143, 33) {
        override fun mayPlace(stack: ItemStack): Boolean = false

        override fun onTake(player: Player, stack: ItemStack) {
            stack.onCraftedBy(player.level(), player, stack.count)
            resultContainer.awardUsedRecipes(player, listOf(inputSlot.item))
            val consumed = inputSlot.remove(1)
            if (!consumed.isEmpty) {
                setupResultSlot()
            }

            access.execute { level, pos ->
                val gameTime = level.gameTime
                if (lastSoundTime != gameTime) {
                    level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f)
                    lastSoundTime = gameTime
                }
            }
            super.onTake(player, stack)
        }
    })

    val selectedRecipeIndex: Int
        get() = selectedRecipeIndexData.get()

    val numRecipes: Int
        get() = recipes.size

    init {
        for (row in 0 until 3) {
            for (column in 0 until 9) {
                addSlot(Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18))
            }
        }
        for (column in 0 until 9) {
            addSlot(Slot(playerInventory, column, 8 + column * 18, 142))
        }
        addDataSlot(selectedRecipeIndexData)
    }

    fun hasInputItem(): Boolean = inputSlot.hasItem() && recipes.isNotEmpty()

    fun getResultItem(recipeIndex: Int): ItemStack =
        recipes[recipeIndex].value().getResultItem(level.registryAccess())

    fun registerUpdateListener(listener: Runnable) {
        slotUpdateListener = listener
    }

    override fun stillValid(player: Player): Boolean = stillValid(access, player, ModBlocks.woodCutter)

    override fun clickMenuButton(player: Player, id: Int): Boolean {
        if (isValidRecipeIndex(id)) {
            selectedRecipeIndexData.set(id)
            setupResultSlot()
        }
        return true
    }

    private fun isValidRecipeIndex(index: Int): Boolean = index in recipes.indices

    override fun slotsChanged(container: Container) {
        val stack = inputSlot.item
        if (!stack.`is`(input.item)) {
            input = stack.copy()
            setupRecipeList(container, stack)
        }
    }

    private fun setupRecipeList(container: Container, stack: ItemStack) {
        recipes = emptyList()
        selectedRecipeIndexData.set(-1)
        resultSlot.set(ItemStack.EMPTY)
        if (!stack.isEmpty) {
            recipes = level.recipeManager.getRecipesFor(ModRecipeTypes.woodcutting, createRecipeInput(container), level)
        }
    }

    private fun setupResultSlot() {
        if (recipes.isNotEmpty() && isValidRecipeIndex(selectedRecipeIndex)) {
            val recipe = recipes[selectedRecipeIndex]
            val result = recipe.value().assemble(createRecipeInput(container), level.registryAccess())
            if (result.isItemEnabled(level.enabledFeatures())) {
                resultContainer.setRecipeUsed(recipe)
                resultSlot.set(result)
            } else {
                resultSlot.set(ItemStack.EMPTY)
            }
        } else {
            resultSlot.set(ItemStack.EMPTY)
        }
        broadcastChanges()
    }

    override fun canTakeItemForPickAll(stack: ItemStack, slot: Slot): Boolean =
        slot.container !== resultContainer && super.canTakeItemForPickAll(stack, slot)

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        val slot = slots[index]
        if (!slot.hasItem()) return ItemStack.EMPTY

        val stack = slot.item
        val original = stack.copy()
        when {
            index == RESULT_SLOT -> {
                if (!inventoryCanHold(stack)) return ItemStack.EMPTY
                stack.item.onCraftedBy(stack, player.level(), player)
                if (!moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true)) return ItemStack.EMPTY
                slot.onQuickCraft(stack, original)
            }
            index == INPUT_SLOT -> {
                if (!moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false)) return ItemStack.EMPTY
            }
            level.recipeManager.getRecipeFor(ModRecipeTypes.woodcutting, SingleRecipeInput(stack), level).isPresent -> {
                if (!moveItemStackTo(stack, INPUT_SLOT, RESULT_SLOT, false)) return ItemStack.EMPTY
            }
            index in INV_SLOT_START until INV_SLOT_END -> {
                if (!moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) return ItemStack.EMPTY
            }
            index in USE_ROW_SLOT_START until USE_ROW_SLOT_END -> {
                if (!moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false)) return ItemStack.EMPTY
            }
        }

        if (stack.isEmpty) {
            slot.setByPlayer(ItemStack.EMPTY)
        }
        slot.setChanged()
        if (stack.count == original.count) return ItemStack.EMPTY

        slot.onTake(player, stack)
        broadcastChanges()
        return original
    }

    /**
     * Checks the player's inventory has room for the whole result stack. Stonecutting recipes can
     * output more than one item, and a partial shift-click move would lose the remainder: taking
     * part of a result consumes an input and refreshes the preview, discarding what didn't fit.
     */
    private fun inventoryCanHold(stack: ItemStack): Boolean {
        var room = stack.count
        for (index in INV_SLOT_START until USE_ROW_SLOT_END) {
            val slotStack = slots[index].item
            when {
                slotStack.isEmpty -> room -= stack.maxStackSize
                ItemStack.isSameItemSameComponents(slotStack, stack) -> room -= slotStack.maxStackSize - slotStack.count
            }
            if (room <= 0) return true
        }
        return false
    }

    override fun removed(player: Player) {
        super.removed(player)
        resultContainer.removeItemNoUpdate(RESULT_SLOT)
        access.execute { _, _ -> clearContainer(player, container) }
    }

    companion object {
        const val INPUT_SLOT = 0
        const val RESULT_SLOT = 1
        const val INV_SLOT_START = 2
        private const val INV_SLOT_END = 29
        private const val USE_ROW_SLOT_START = 29
        private const val USE_ROW_SLOT_END = 38

        private fun createRecipeInput(container: Container) = SingleRecipeInput(container.getItem(0))
    }
}
