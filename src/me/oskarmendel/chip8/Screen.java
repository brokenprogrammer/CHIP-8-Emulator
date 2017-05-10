/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Brokenprogrammer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
	
	public int[][] graphic = new int[WIDTH][HEIGHT]; // Graphics in Chip 8 is a black
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
					gc.setFill(Color.WHITE);
					gc.fillRect(x*scale, (y*scale)+100, scale, scale);
				} else {
					gc.setFill(Color.BLACK);
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
