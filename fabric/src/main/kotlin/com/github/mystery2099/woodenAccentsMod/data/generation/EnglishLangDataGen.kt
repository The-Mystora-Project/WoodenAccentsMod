package com.github.mystery2099.woodenAccentsMod.data.generation

import com.github.mystery2099.woodenAccentsMod.WoodenAccentsMod.toIdentifier
import com.github.mystery2099.woodenAccentsMod.block.ModBlocks
import com.github.mystery2099.woodenAccentsMod.block.id
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import com.github.mystery2099.woodenAccentsMod.stat.ModStats
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class EnglishLangDataGen(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricLanguageProvider(dataOutput, registryLookup) {
    override fun generateTranslations(registryLookup: HolderLookup.Provider, translationBuilder: TranslationBuilder) {
        translationBuilder.run {
            ModBlocks.blocks.forEach {
                translationBuilder.add(it, it.id.path.toDisplayName())
            }
            ModItemGroup.entries.forEach { group ->
                val name = when (group.path) {
                    "decorations" -> "Furniture"
                    "miscellaneous" -> "Storage"
                    else -> "Building"
                }
                add(group.path.toIdentifier().toLanguageKey(), "Wooden Accents: $name")
            }
            add("container.crate.more", "and %s more...")
            add("container.wooden_accents_mod.wood_cutter", "Woodcutter")
            add("category.wooden_accents_mod.woodcutting", "Woodcutting")
            add(ModStats.interactWithWoodCutter.toLanguageKey("stat"), "Interactions with Woodcutter")
        }
    }

    private fun String.toDisplayName(): String {
        val displayPath = when {
            this == "wood_cutter" -> "woodcutter"
            startsWith("modern_") -> removePrefix("modern_").replace("fence", "picket_fence")
            endsWith("_plank_carpet") -> replace("_plank_carpet", "_plank_flooring")
            endsWith("_carpet") -> replace("_carpet", "_flooring")
            endsWith("_bookshelf") -> replace("_bookshelf", "_narrow_bookshelf")
            else -> this
        }
        return displayPath.toName()
    }

    private fun String?.toName(): String {
        return if (isNullOrEmpty()) ""
        else lowercase().split("_").joinToString(" ") {
            if (it != "of") {
                it.replaceFirstChar(Char::uppercase)
            } else it
        }
    }

}
