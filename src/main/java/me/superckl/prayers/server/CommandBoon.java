package me.superckl.prayers.server;

import java.util.Collection;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandBoon {

	public static int applyBoon(final CommandContext<CommandSource> context) throws CommandSyntaxException{
		final Collection<? extends Entity> targets = EntityArgument.getOptionalEntities(context, "targets");
		if(targets.isEmpty()) {
			context.getSource().sendSuccess(new TranslationTextComponent(LangUtil.buildTextLoc("command.no_entities")), true);
			return 0;
		}
		final ItemBoon boon = context.getArgument("boon", ItemBoon.class);
		int targeted = 0;
		for(final Entity e:targets)
			if(e instanceof LivingEntity) {
				final LivingEntity entity = (LivingEntity) e;
				final ItemStack stack = entity.getMainHandItem();
				if(!stack.isEmpty() && ItemBoon.getBoons(stack).isEmpty()) {
					boon.addTo(stack);
					targeted++;
				}
			}
		context.getSource().sendSuccess(new TranslationTextComponent(LangUtil.buildTextLoc("command.boon.entities"), boon.getName(), targeted), true);
		return targeted;
	}

}
