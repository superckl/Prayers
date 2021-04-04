package me.superckl.prayers.server;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import me.superckl.prayers.boon.ItemBoon;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

public class BoonArgument implements ArgumentType<ItemBoon>{

	public static final SimpleCommandExceptionType NO_BOON_ERROR = new SimpleCommandExceptionType(new TranslationTextComponent("argument.boon.not_exist"));

	@Override
	public ItemBoon parse(final StringReader reader) throws CommandSyntaxException {
		try {
			return ItemBoon.valueOf(reader.readString().toUpperCase());
		} catch (final IllegalArgumentException e) {
			throw BoonArgument.NO_BOON_ERROR.createWithContext(reader);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(Stream.of(ItemBoon.values()).map(ItemBoon::name).map(String::toLowerCase), builder);
	}

}
