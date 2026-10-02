package com.github.mystery2099.woodenAccentsMod.shulkerBoxTooltip

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting


/** Matches the crate's 3x3 layout without revealing unresolved loot-table contents. */
class CratePreviewProvider : BlockEntityPreviewProvider(9, true, 3) {
    override fun showTooltipHints(context: PreviewContext): Boolean = true

    override fun addTooltip(context: PreviewContext): List<Component> {
        // Hide contents until the loot table in DataComponents.CONTAINER_LOOT resolves.
        if (canUseLootTables() && context.stack().has(DataComponents.CONTAINER_LOOT)) {
            val style = Style.EMPTY.withColor(ChatFormatting.GRAY)
            return listOf(Component.literal("???????").setStyle(style))
        }
        return super.addTooltip(context)
    }
}
