package mavenmcserver.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FieldPointTest {

	@Test
	public void testEquals() {
		assertEquals(new FieldPoint(0, 0, 0), new FieldPoint(0, 0, 0));
		assertEquals(new FieldPoint(0, 1, 2), new FieldPoint(0, 1, 2));
		assertEquals(new FieldPoint(-58, 12, 48), new FieldPoint(-58, 12, 48));
		assertEquals(new FieldPoint(-12, 3, 4), new FieldPoint(-12, 3, 4));
		assertEquals(new FieldPoint(0, -4, 1), new FieldPoint(0, -4, 1));
		assertEquals(new FieldPoint(3, -2, -1), new FieldPoint(3, -2, -1));
	}
	
	@Test
	public void testOffsetBy() {
		assertEquals(new FieldPoint(1, 2, 4), new FieldPoint(0, 0, 0).offsetBy(1, 2, 4));
		assertEquals(new FieldPoint(1, 2, 4), new FieldPoint(-1, 3, 2).offsetBy(2, -1, 2));
		assertEquals(new FieldPoint(0, 0, 0), new FieldPoint(3, -2, 5).offsetBy(-3, 2, -5));
		assertEquals(new FieldPoint(-100, -200, 301), new FieldPoint(-152, 168, 2).offsetBy(52, -368, 299));
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new FieldPoint(0, 1, 2).hashCode(), new FieldPoint(0, 1, 2).hashCode());
		assertEquals(new FieldPoint(2, 1, 3).hashCode(), new FieldPoint(2, 1, 3).hashCode());
		assertEquals(new FieldPoint(-4, -3, 5).hashCode(), new FieldPoint(-4, -3, 5).hashCode());
	}
	
	@Test
	public void testCopy() {
		FieldPoint point = new FieldPoint(1, 2, 3);
		assertEquals(point, point.copy());
		assertEquals(point, point.copy());
		
		point = new FieldPoint(-2, 3, 65);
		assertEquals(point, point.copy());
		assertEquals(point, point.copy());
	}
	
}
