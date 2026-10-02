package com.github.mystery2099.woodenAccentsMod.block.custom

import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.requires
import com.github.mystery2099.woodenAccentsMod.data.generation.RecipeUtil.offerWoodcuttingRecipe
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomBlockStateProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomItemGroupProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomRecipeProvider
import com.github.mystery2099.woodenAccentsMod.data.generation.interfaces.CustomTagProvider
import com.github.mystery2099.woodenAccentsMod.item.group.ModItemGroup
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LadderBlock
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.level.ItemLike
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import java.util.function.Consumer
abstract class AbstractCustomLadderBlock(settings: Properties) : LadderBlock(settings.noOcclusion()), CustomItemGroupProvider,
    CustomRecipeProvider,
    CustomTagProvider<Block>, CustomBlockStateProvider {
    override val tag: TagKey<Block> = BlockTags.CLIMBABLE
    override val itemGroup: ModItemGroup = ModItemGroup.BUILDING

    fun offerRecipe(recipeExporter: RecipeOutput, input: ItemLike, outputNum: Int, group: String) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, outputNum).apply {
            define('#', input)
            pattern("# #")
            pattern("###")
            pattern("# #")
            group(group)
            requires(input)
            save(recipeExporter)
        }
        offerWoodcuttingRecipe(recipeExporter, input, this)

    }
}
