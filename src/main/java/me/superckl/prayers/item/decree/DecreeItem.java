package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;
import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.init.ModItems;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DecreeItem extends Item{

	@Getter
	private final Type type;

	public DecreeItem(final Type type) {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP));
		this.type = type;
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		return stack.isFramed();
	}

	@RequiredArgsConstructor
	public enum Type{

		FERTILITY(FertilityDecreeData::new),
		SANCTUARY(DecreeData::new),
		INFERTILITY(DecreeData::new);

		@Getter
		private final Function<WeakReference<ItemFrameEntity>, DecreeData> dataSupplier;

	}

}
