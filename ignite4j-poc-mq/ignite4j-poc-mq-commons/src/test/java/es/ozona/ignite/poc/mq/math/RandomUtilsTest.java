package es.ozona.ignite.poc.mq.math;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RandomUtilsTest {

	@Test(expected = IllegalArgumentException.class)
	public void randomIntOfZeros() {
		RandomUtils.randomInt(0, 0);
	}
	
	@Test
	public void randomIntOfZeroToOneIsZero() {
		assertTrue("RandomInt of zero to one is zero.", RandomUtils.randomInt(0, 1) == 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void randomIntBoundMustBeGreatestThanMinValue() {
		RandomUtils.randomInt(10, 1);
	}

}
