package me.superckl.prayers.server;

import java.util.Collection;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.capability.TickablePrayerProvider;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketSetPrayerLevel;
import me.superckl.prayers.network.packet.user.PacketSetPrayerPoints;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CommandSet {

	public static int prayerPoints(final CommandContext<CommandSource> context) throws CommandSyntaxException {
		final Collection<? extends Entity> targets = EntityArgument.getOptionalEntities(context, "targets");
		if(targets.isEmpty()) {
			context.getSource().sendSuccess(new TranslationTextComponent(LangUtil.buildTextLoc("command.no_entities")), true);
			return 0;
		}
		final float points = FloatArgumentType.getFloat(context, "amount");
		int pointsSet = 0;
		for(final Entity e:targets) {
			if (!(e instanceof LivingEntity))
				continue;
			final TickablePrayerProvider<?> user = CapabilityHandler.getPrayerCapability((LivingEntity) e);
			user.setCurrentPrayerPoints(points);
			pointsSet++;
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e),
					PacketSetPrayerPoints.builder().entityID(e.getId()).amount(points).build());
		}
		context.getSource().sendSuccess(new TranslationTextComponent(LangUtil.buildTextLoc("command.level.entities"), targets.size(), pointsSet), true);
		return pointsSet;
	}

	public static int prayerLevel(final CommandContext<CommandSource> context) throws CommandSyntaxException {
		final Collection<ServerPlayerEntity> targets = EntityArgument.getOptionalPlayers(context, "targets");
		if(targets.isEmpty()) {
			context.getSource().sendSuccess(new TranslationTextComponent(LangUtil.buildTextLoc("command.no_entities")), true);
			return 0;
		}
		final int level = IntegerArgumentType.getInteger(context, "level");
		for(final ServerPlayerEntity e:targets) {
			final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(e);
			user.setPrayerLevel(level);
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e),
					PacketSetPrayerLevel.builder().entityID(e.getId()).level(level).build());
		}
		context.getSource().sendSuccess(new TranslationTextComponent(LangUtil.buildTextLoc("command.level.players"), targets.size()), true);
		return targets.size();
	}

}
