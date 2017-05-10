package me.oskarmendel.chip8;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 
 * @author Oskar Mendel
 * @version 0.00.00
 * @name Screen.java
 */
public class Screen extends Canvas{

	private static final int WIDTH = 64;
	private static final int HEIGHT = 32;
	
	private int scale = 12;
	
	private GraphicsContext gc;
	
	private int[][] graphic = new int[WIDTH][HEIGHT]; // Graphics in Chip 8 is a black
	// and white screen of 2048
	// pixels (62*32).
	
	public Screen() {
		super(800, 600);
		
		gc = this.getGraphicsContext2D();
		
		clear();
		
		gc.setFill(Color.WHITE);
	}
	
	/**
	 * Clears the display setting all the pixels to zero.
	 */
	public void clear() {
		for(int y = 0; y < HEIGHT; y++) {
			for(int x = 0; x < WIDTH; x++) {
				graphic[x][y] = 0;
			}
		}
		
		gc.clearRect(0, 0, 800, 600);
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 800, 600);
	}
	
	/**
	 * Renders the display.
	 */
	public void render() {
		for(int y = 0; y < HEIGHT; y++) {
			for(int x = 0; x < WIDTH; x++) {
				if(graphic[x][y] == 1) {
					gc.fillRect(x*scale, (y*scale)+100, scale, scale);
				}
			}
		}
	}
	
	/**
	 * Draws the given sprite at the specified coordinates.
	 * 
	 * @param x - X coordinate.
	 * @param y - Y coordinate.
	 * @param sprite - The sprite to draw.
	 * @return True of a pixel was removed, false otherwise.
	 */
	public boolean draw(int x, int y, int[] sprite) {
		boolean removed = false;
		
		for (int i = 0; i < sprite.length; i++) {
			for (int xLine = 7; xLine >= 0; xLine--) {
				if(((sprite[i] >> xLine) & 1) == 1) {
					int indy = (y + i) % HEIGHT;
					int indx = (x + 8 - xLine) % WIDTH;
					graphic[indx][indy] ^= 1;
					
					if (graphic[indx][indy] == 0) {
						removed = true;
					}
				}
			}
		}
		
		return removed;
	}
}
