package me.superckl.prayers.integration.curios;

import java.util.Optional;

import me.superckl.prayers.capability.TickablePrayerProvider;
import me.superckl.prayers.inventory.SlotAwareIterator;
import me.superckl.prayers.inventory.SlotHelper;
import me.superckl.prayers.item.PrayerInventoryItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CuriosIntegration {

	public static void sendSlotIMC(final InterModEnqueueEvent e) {
		InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, SlotTypePreset.CHARM.getMessageBuilder()::build);
	}

	public static void commonSetup(final FMLCommonSetupEvent e) {
		SlotHelper.registerHelper("curios", CurioSlotHelper.class, CurioSlotHelper::deserialize);
	}

	public static void attachCapability(final AttachCapabilitiesEvent<ItemStack> e) {
		if(e.getObject().getItem() instanceof PrayerInventoryItem<?>) {
			final TickablePrayerProvider.Provider<ICurio> provider = new TickablePrayerProvider.Provider<>(new PrayerItemCurio(), () -> CuriosCapability.ITEM);
			e.addCapability(CuriosCapability.ID_ITEM, provider);
			e.addListener(provider::invalidate);
		}
	}

	public static Optional<SlotAwareIterator<?>> getCurios(final LivingEntity entity){
		return CuriosApi.getCuriosHelper().getCuriosHandler(entity).map(CurioSlotAwareIterator::new);
	}

	public static class PrayerItemCurio implements ICurio{

		@Override
		public void curioTick(final String identifier, final int index, final LivingEntity livingEntity) {
			if(livingEntity instanceof PlayerEntity) {
				final PlayerEntity player = (PlayerEntity) livingEntity;
				final SlotHelper helper = new CurioSlotHelper(new SlotContext(identifier, null, index));
				final ItemStack stack = helper.getStack(player).orElseThrow(() -> new IllegalStateException("Curio ticked but is not worn by player!"));
				if(stack.getItem() instanceof PrayerInventoryItem<?>)
					PrayerInventoryItem.onInventoryTick(stack, player, helper);
			}

		}

	}

}
