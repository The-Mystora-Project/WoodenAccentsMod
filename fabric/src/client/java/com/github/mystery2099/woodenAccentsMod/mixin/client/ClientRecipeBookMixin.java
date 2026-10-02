package com.github.mystery2099.woodenAccentsMod.mixin.client;

import com.github.mystery2099.woodenAccentsMod.recipe.ModRecipeTypes;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sorts woodcutting recipes the same way vanilla sorts stonecutting ones. Neither has a recipe book screen,
 * but without this the client logs an "Unknown recipe category" warning for every unlocked woodcutting recipe.
 * NeoForge does the same through {@code RegisterRecipeBookCategoriesEvent}.
 */
@Mixin(ClientRecipeBook.class)
public abstract class ClientRecipeBookMixin {
	@Inject(method = "getCategory", at = @At("HEAD"), cancellable = true)
	private static void woodenAccentsMod$getWoodcuttingCategory(RecipeHolder<?> recipe, CallbackInfoReturnable<RecipeBookCategories> cir) {
		if (recipe.value().getType() == ModRecipeTypes.INSTANCE.getWoodcutting()) {
			cir.setReturnValue(RecipeBookCategories.STONECUTTER);
		}
	}
}
