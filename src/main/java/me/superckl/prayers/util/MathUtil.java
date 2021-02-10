package me.superckl.prayers.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.util.math.BlockPos;

public class MathUtil {

	/**
	 * Performs a depth-first search to enumerate a graph.
	 * @param origin the node of the graph to begin the search
	 * @param hasEdge predicate to determine if two nodes share an edge. These two nodes will be 'potential' neighbors
	 * as supplied by the neighborhood supplier.
	 * @param neighborhoodSupplier supplies all possible neighbors of the given node. These will be verified to have a mutual edge
	 * with the hasEdge predicate
	 */
	public static <T> Set<T> dsf(final T origin, final BiPredicate<T, T> hasEdge, final Function<T, Set<T>> neighborhoodSupplier){
		final Set<T> visited = new HashSet<>();
		MathUtil.recursiveVisit(origin, visited, hasEdge, neighborhoodSupplier);
		return visited;
	}

	private static <T> void recursiveVisit(final T origin, final Set<T> visited, final BiPredicate<T, T> hasEdge, final Function<T, Set<T>> neighborhoodSupplier){
		if(visited.contains(origin))
			return;
		visited.add(origin);
		final Set<T> neighbors = neighborhoodSupplier.apply(origin);
		neighbors.removeIf(neighbor -> !hasEdge.test(origin, neighbor));
		neighbors.forEach(neighbor -> MathUtil.recursiveVisit(neighbor, visited, hasEdge, neighborhoodSupplier));
	}

	public static List<int[]> toIntList(final Set<BlockPos> blocks){
		return blocks.stream().map(pos -> new int[] {pos.getX(), pos.getY(), pos.getZ()}).collect(Collectors.toList());
	}

}
