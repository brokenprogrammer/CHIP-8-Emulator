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

import java.util.Random;

/**
 * 
 * @author Oskar Mendel
 * @version 0.00.00
 * @name Memory.java
 */
public class Memory {

	private int opcode; // Used to store the current opcode.
	private int[] memory = new int[4096]; // Entire memory for the Chip 8.
	private int[] V = new int[16]; // The 15 CPU registers for the Chip 8.
	private int I; // Index register.
	private int pc; // Program counter which can contain a value from 0x000 to
					// 0xFFF.

	private int[] stack = new int[16]; // Remembers location between jumps.
	private int sp; // In order to remember which level of the stack was used we
					// use the stack pointer.
	
	private int delayTimer; // Timer registers that counts at 60Hz. When set
	// above zero they will count down to zero.
	private int soundTimer; // Make a sound whenever the sound timer reaches
		// zero.

	private static final Random RANDOM = new Random();
	
	private Screen screen;
	private Keyboard keyboard;
	
	/**
	 * Initializes the memory.
	 * 
	 * @param s - Screen to communicate with.
	 * @param k - Keyboard to read keys from.
	 */
	public Memory(Screen s, Keyboard k) {
		this.screen = s;
		this.keyboard = k;
		
		pc = 0x200; // Program counter always starts at 0x200.
		opcode = 0; // Reset current opcode.
		I = 0; // Reset the index register.
		sp = 0; // Reset the stack pointer.
		
		//Reset memory
		for(int i = 0; i < memory.length; i++) {
			memory[i] = 0;
		}
		
		// Reset stack and the V registers.
		for (int i = 0; i < 16; i++) {
			stack[i] = 0;
			V[i] = 0;
		}
		
		//Load font into memory
		for (int i = 0; i < 80; i++) {
			memory[i] = Keyboard.FONT[i];
		}
		
		this.delayTimer = 0;
		this.soundTimer = 0;
	}

	/**
	 * Loads the program into the memory which is placing the program into the memory
	 * starting from the memory location 0x200 (512).
	 * 
	 * @param b - Byte array containing the bytes read from the program file.
	 */
	public void loadProgram(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			memory[i + 512] = (b[i] & 0xFF);
		}
	}

	/**
	 * Fetches a single opcode.
	 */
	public void fetchOpcode() {
		opcode = ((memory[pc] << 8) | (memory[pc + 1]));
	}

	/**
	 * Decodes an opcode and performs target action.
	 */
	public void decodeOpcode() {
		int x = 0;
		
		switch (opcode) {
		case 0x00E0:
			// Clear display
			screen.clear();
			
			pc += 2;
			return;
		case 0x00EE:
			// Returns from a subroutine
			pc = stack[sp--];
			
			pc += 2;
			return;
		}

		switch (opcode & 0xF000) {
		case 0x1000:
			// 1NNN - Jump to address NNN
			pc = opcode & 0x0FFF;
			
			return;
		case 0x2000:
			// 2NNN - Call subroutine at nnn.
			stack[++sp] = pc;
			
			pc = opcode & 0x0FFF;
			return;
		case 0x3000:
			// 3XNN - Skip next instruction if Vx = kk.
			if (V[(opcode & 0x0F00) >>> 8] == (opcode & 0x00FF)) {
				pc += 4;
			} else {
				pc += 2;
			}
			
			return;
		case 0x4000:
			// 4XNN - Skip next instruction if Vx != kk.
			if (V[(opcode & 0x0F00) >>> 8] != (opcode & 0x00FF)) {
				pc += 4;
			} else {
				pc += 2;
			}
			return;
		case 0x5000:
			// 5XY0 - Skip next instruction if Vx = Vy.
			if (V[(opcode & 0x0F00) >>> 8] == V[(opcode & 0x00F0) >>> 4]) {
				pc += 4;
			} else {
				pc += 2;
			}
			return;
		case 0x6000:
			// 6XNN - Set Vx = kk.
			V[(opcode & 0x0F00) >>> 8] = (opcode & 0x00FF);
			
			pc += 2;
			return;
		case 0x7000:
			// 7XNN - Adds NN to VX.
			x = (opcode & 0x0F00) >>> 8;
			//V[x] = ((V[x] + (opcode & 0x00FF)) & 0xFF);
			int NN = (opcode & 0x00FF);
			 int result = V[x] + NN;
	        // resolve overflow
	        if (result >= 256) {
	            V[x] = result - 256;
	        } else {
	            V[x] = result;
	        }	
			
			pc += 2;
			return;
		}

		switch (opcode & 0xF00F) {
		case 0x8000:
			// 8XY0 - Set Vx = Vy.
			V[(opcode & 0x0F00) >>> 8] = V[(opcode & 0x00F0) >>> 4];
			pc += 2;
			return;
		case 0x8001:
			// 8XY1 - Set Vx = (Vx OR Vy).
			x = (opcode & 0x0F00) >>> 8;
			V[x] = (V[x] | V[(opcode & 0x00F0) >>> 4]);
			pc += 2;
			return;
		case 0x8002:
			// 8XY2 - Set Vx = (Vx AND Vy).
			x = (opcode & 0x0F00) >>> 8;
			V[x] = (V[x] & V[(opcode & 0x00F0) >>> 4]);
			pc += 2;
			return;
		case 0x8003:
			// 8XY3 - Set Vx = Vx XOR Vy.
			x = (opcode & 0x0F00) >>> 8;
			V[x] = (V[x] ^ V[(opcode & 0x00F0) >>> 4]);
			
			pc += 2;
			return;
		case 0x8004:
			// 8XY4 - Set Vx = Vx + Vy, set VF = carry.
			x = (opcode & 0x0F00) >>> 8;
			int sum = V[x] + V[(opcode & 0x00F0) >>> 4];
			
			V[0xF] = sum > 0xFF ? 1 : 0;
			V[x] = (sum & 0xFF);
			
			pc += 2;
			return;
		case 0x8005:
			// 8XY5 - Set Vx = Vx - Vy, set VF = NOT borrow.
			x = (opcode & 0x0F00) >>> 8;
			
			if(V[(opcode & 0x00F0) >>> 4] > V[x]) {
				V[0xF] = 0; //There is a borrow.
			} else {
				V[0xF] = 1;
			}
			
			V[x] = (V[x] - V[(opcode & 0x00F0) >>> 4]) & 0xFF;
			
			pc += 2;
			return;
		case 0x8006:
			// 8XY6 - Set Vx = Vx SHR 1.
			// Shift Vx right by 1. Sets VF to the least significant bit of Vx before shift.
			x = (opcode & 0x0F00) >>> 8;
			
			V[0xF] = (V[x] & 0x1) == 1 ? 1 : 0;
			
			V[x] = (V[x] >>> 1);
			
			pc += 2;
			return;
		case 0x8007:
			// 8XY7 - Set Vx = Vy - Vx, set VF = NOT borrow.
			// VF is set to 0 when there is a borrow and 1 otherwise.
			x = (opcode & 0x0F00) >>> 8;
			
			if (V[(opcode & 0x00F0) >>> 4] > V[x]) {
				V[0xF] = 1;
			} else {
				V[0xF] = 0;
			}
			
			V[x] = ((V[(opcode & 0x00F0) >>> 4] - V[x]) & 0xFF);
			
			pc += 2;
			return;
		case 0x800E:
			// 8XYE - Set Vx = Vx SHL 1.
			// Shift Vx left by 1. Sets VF to the value of the most significant bit of Vx before the shift.
			x = (opcode & 0x0F00) >>> 8;
			
			V[0xF] = (V[x] >>> 7) == 0x1 ? 1 : 0;
			
			V[x] = ((V[x] << 1) & 0xFF);
			
			pc += 2;
			return;
		case 0x9000:
			// 9XY0 - Skip next instruction if Vx != Vy.
			x = (opcode & 0x0F00) >>> 8;
			
			if (V[x] != V[(opcode & 0x00F0) >>> 4]) {
				pc += 4;
			} else {
				pc += 2;
			}
			
			return;
		}
		
		switch (opcode & 0xF000) {
		case 0xA000:
			// ANNN - Set I = nnn.
			I = (opcode & 0x0FFF);
			
			pc += 2;
			return;
		case 0xB000:
			// BNNN - Jump to location nnn + V0.
			pc = (opcode & 0x0FFF) + V[0];
			
			return;
		case 0xC000:
			// CXNN - Set Vx = random byte AND NN.
			x = (opcode & 0x0F00) >>> 8;
			
			V[x] = ((RANDOM.nextInt(256)) & (opcode & 0x00FF));
			
			pc += 2;
			return;
		case 0xD000:
			// DXYN - Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
			x = V[(opcode & 0x0F00) >> 8];
	        int y = V[(opcode & 0x00F0) >> 4];
	        int height = opcode & 0x000F;
	        V[0xF] = 0;
	        for (int yLine = 0; yLine < height; yLine++) {
	            int pixel = memory[I + yLine];

	            for (int xLine = 0; xLine < 8; xLine++) {
	                // check each bit (pixel) in the 8 bit row
	                if ((pixel & (0x80 >> xLine)) != 0) {

	                    // wrap pixels if they're drawn off screen
	                    int xCoord = x+xLine;
	                    int yCoord = y+yLine; 
	                    
	                    if (xCoord < 64 && yCoord < 32) {
	                        // if pixel already exists, set carry (collision)
	                        if (screen.getPixel(xCoord, yCoord) == 1) {
	                            V[0xF] = 1;
	                        }
	                        // draw via xor
	                        screen.setPixel(xCoord,yCoord);
	                    }
	                }
	            }
	        }  
			
			pc += 2;
			return;
		}
		
		switch (opcode & 0xF0FF) {
		case 0xE09E:
			// EX9E - Skip next instruction if key with the value of Vx is pressed.
			if(keyboard.isPressed(V[(opcode & 0x0F00) >>> 8])) {
				pc += 4;
			} else {
				pc += 2;
			}
			
			return;
		case 0xE0A1:
			// EXA1 - Skip next instruction if key with the value of Vx is not pressed.
			if(!keyboard.isPressed(V[(opcode & 0x0F00) >>> 8])) {
				pc += 4;
			} else {
				pc += 2;
			}
			
			return;
		case 0xF007:
			// FX07 - Set Vx = delay timer value.
			x = (opcode & 0x0F00) >>> 8;
			V[x] = (this.delayTimer & 0xFF);
			
			pc += 2;
			return;
		case 0xF00A:
			// FX0A - Wait for a key press, store the value of the key in Vx.
			x = (opcode & 0x0F00) >>> 8;
			
			for (int j = 0; j <= 0xF; j++) {
				if (keyboard.isPressed(j)) {
					V[x] = j;
					pc += 2;
					return;
				}
			}
			
			//If no key was pressed return, try again.
			return;
		case 0xF015:
			// FX15 - Set delay timer = Vx.
			x = (opcode & 0x0F00) >>> 8;
			
			this.delayTimer = V[x];
			
			pc += 2;
			return;
		case 0xF018:
			// FX18 - Set sound timer = Vx.
			x = (opcode & 0x0F00) >>> 8;
			
			this.soundTimer = V[x];
			
			pc += 2;
			return;
		case 0xF01E:
			// FX1E - Set I = I + Vx.
			x = (opcode & 0x0F00) >>> 8;
			
			//Setting VF to 1 when range overflow.
			if(I + V[x] > 0xFFF) {
				V[0xF] = 1;
			} else {
				V[0xF] = 0;
			}
			
			I = ((I + V[x]) & 0xFFF);
			
			pc += 2;
			return;
		case 0xF029:
			// FX29 - Set I = location of sprite for digit Vx.
			x = (opcode & 0x0F00) >>> 8;
			
			I = V[x] * 5;
			
			pc += 2;
			return;
		case 0xF033:
			// FX33 - Store binary coded decimal representation of Vx 
			// in memory locations I, I+1, and I+2.
			x = (opcode & 0x0F00) >>> 8;
			
			memory[I] = (V[x] / 100);
			memory[I + 1] = ((V[x] % 100) / 10);
			memory[I + 2] = ((V[x] % 100) % 10);
			
			pc += 2;
			return;
		case 0xF055:
			// FX55 - Store registers V0 through Vx in memory starting at location I.
			x = (opcode & 0x0F00) >>> 8;
			
			for (int j = 0; j <= x; j++) {
				memory[I + j] = V[j];
			}
			
			pc += 2;
			return;
		case 0xF065:
			// FX65 - Read registers V0 through Vx from memory starting at location I.
			x = (opcode & 0x0F00) >>> 8;
			
			for (int j = 0; j <= x; j++) {
				V[j] = memory[I + j] & 0xFF;
			}
			
			pc += 2;
			return;
		}
	}
	
	/**
	 * Getter for the delay timer.
	 * 
	 * @return The delay timer.
	 */
	public int getDelayTimer() {
		return this.delayTimer;
	}
	
	/**
	 * Setter for the delay timer.
	 * 
	 * @param d - Delay timer value.
	 */
	public void setDelayTimer(int d) {
		this.delayTimer = d;
	}
	
	/**
	 * Getter for the sound timer.
	 * 
	 * @return The delay timer.
	 */
	public int getSoundTimer() {
		return this.soundTimer;
	}
	
	/**
	 * Setter for the sound timer.
	 * 
	 * @param s - Sound timer value.
	 */
	public void setSoundTimer(int s) {
		this.soundTimer = s;
	}
}
