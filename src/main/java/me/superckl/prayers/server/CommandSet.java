package me.superckl.prayers.server;

import java.util.Collection;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.superckl.prayers.network.packet.PacketSetPrayerLevel;
import me.superckl.prayers.network.packet.PacketSetPrayerPoints;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.user.IPrayerUser;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CommandSet {

	public static int prayerPoints(final CommandContext<CommandSource> context) throws CommandSyntaxException {
		final Collection<? extends Entity> targets = EntityArgument.getEntitiesAllowingNone(context, "targets");
		if(targets.isEmpty()) {
			context.getSource().sendFeedback(new StringTextComponent("No entities targeted."), true);
			return 0;
		}
		final float points = FloatArgumentType.getFloat(context, "amount");
		int pointsSet = 0;
		for(final Entity e:targets) {
			if (!(e instanceof LivingEntity))
				continue;
			final IPrayerUser user = IPrayerUser.getUser(e);
			user.setCurrentPrayerPoints(points);
			pointsSet++;
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e),
					PacketSetPrayerPoints.builder().entityID(e.getEntityId()).amount(points).build());
		}
		context.getSource().sendFeedback(new StringTextComponent(String.format("%d entities targeted, set points for %d living entities", targets.size(), pointsSet)), true);
		return pointsSet;
	}

	public static int prayerLevel(final CommandContext<CommandSource> context) throws CommandSyntaxException {
		final Collection<? extends Entity> targets = EntityArgument.getEntitiesAllowingNone(context, "targets");
		if(targets.isEmpty()) {
			context.getSource().sendFeedback(new StringTextComponent("No entities targeted."), true);
			return 0;
		}
		final int level = IntegerArgumentType.getInteger(context, "level");
		int levelsSet = 0;
		for(final Entity e:targets) {
			if (!(e instanceof LivingEntity))
				continue;
			final IPrayerUser user = IPrayerUser.getUser(e);
			user.setPrayerLevel(level);
			levelsSet++;
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e),
					PacketSetPrayerLevel.builder().entityID(e.getEntityId()).level(level).build());
		}
		context.getSource().sendFeedback(new StringTextComponent(String.format("%d entities targeted, set level for %d living entities", targets.size(), levelsSet)), true);
		return levelsSet;
	}

}
