package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Config;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class DecreeItem extends Item{

	@Getter
	private final Type type;

	public DecreeItem(final Type type) {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP));
		this.type = type;
	}

	@Override
	public Rarity getRarity(ItemStack p_77613_1_) {
		return Rarity.EPIC;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("decree."+this.type.name().toLowerCase())).withStyle(TextFormatting.GRAY));
		tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("decree.radius"), Config.getInstance().getDecreeRanges().get(this.type).get()).withStyle(TextFormatting.GRAY));
	}
	
	@Override
	public boolean isFoil(final ItemStack stack) {
		return stack.isFramed() && ((ItemFrameEntity)stack.getEntityRepresentation()).getRotation() == 0;
	}

	@RequiredArgsConstructor
	public enum Type{

		FERTILITY(FertilityDecreeData::new),
		SANCTUARY(DecreeData::new),
		INFERTILITY(InfertilityDecreeData::new);

		@Getter
		private final Function<WeakReference<ItemFrameEntity>, DecreeData> dataSupplier;

	}

}
