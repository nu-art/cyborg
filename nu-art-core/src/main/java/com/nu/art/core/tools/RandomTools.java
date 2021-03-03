package com.nu.art.core.tools;

import java.util.Random;

public class RandomTools {

	private static Random random = new Random();

	public static void updateSeed(long seed) {
		random = new Random(seed);
	}

	public static void updateSeed() {
		random = new Random();
	}

	@SafeVarargs
	public static <T> T nextRandom(T... values) {
		return values[random.nextInt(values.length)];
	}

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> T nextRandom(T[] values, T... ignore) {
		values = (T[]) ArrayTools.removeElement(values, ignore);
		return values[random.nextInt(values.length)];
	}
}
