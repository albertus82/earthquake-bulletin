package io.github.albertus82.eqbulletin.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DepthTest {

	@Test
	void testValueOf_withCachedValue() {
		Depth depth1 = Depth.valueOf(50);
		Depth depth2 = Depth.valueOf(50);
		assertSame(depth1, depth2);
	}

	@Test
	void testValueOf_withUncachedValue() {
		Depth depth1 = Depth.valueOf(50);
		Depth depth2 = Depth.valueOf(51);
		assertNotSame(depth1, depth2);
	}

	@Test
	void testValueOf_withInvalidValue() {
		assertThrows(IllegalArgumentException.class, () -> Depth.valueOf(-1));
	}

	@Test
	void testToString() {
		Depth depth = Depth.valueOf(50);
		String expected = "50 km";
		String result = depth.toString();
		assertEquals(expected, result);
		assertEquals("5 km", Depth.valueOf(5).toString());
	}

	@Test
	void testValueOf() {
		assertThrows(IllegalArgumentException.class, () -> Depth.valueOf(-1));
		assertThrows(IllegalArgumentException.class, () -> Depth.valueOf(6371 + 1));
		assertEquals(Depth.valueOf(5), Depth.valueOf(5));
	}

	@Test
	void testCompareTo() {
		assertEquals(-1, Depth.valueOf(5).compareTo(Depth.valueOf(6)));
		assertEquals(0, Depth.valueOf(5).compareTo(Depth.valueOf(5)));
		assertEquals(1, Depth.valueOf(5).compareTo(Depth.valueOf(4)));
	}

}
