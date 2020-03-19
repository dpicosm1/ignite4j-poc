package es.ozona.ignite.poc.mq.math;

import java.util.Random;

public class RandomUtils {
	private static final Random rand = new Random();

	public static int randomInt(int minValue, int maxValue) {

		return rand.nextInt(maxValue - minValue) + minValue;

	}

}
