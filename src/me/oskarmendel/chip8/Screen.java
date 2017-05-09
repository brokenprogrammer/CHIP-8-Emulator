package me.oskarmendel.chip8;

/**
 * 
 * @author Oskar Mendel
 * @version 0.00.00
 * @name Screen.java
 */
public class Screen {

	private static final int WIDTH = 64;
	private static final int HEIGHT = 32;
	
	private int[] graphic = new int[WIDTH * HEIGHT]; // Graphics in Chip 8 is a black
	// and white screen of 2048
	// pixels (62*32).
	
	public Screen() {
		
	}
	
	public void clear() {
		
	}
	
	public boolean draw() {
		boolean removed = false;
		
		return removed;
	}
}
