package me.superckl.prayers.integration.jei.wither;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;

@RequiredArgsConstructor
@Getter
public class WitherUpgradeRecipe {

	private final ItemStack input;
	private final ItemStack output;

}
