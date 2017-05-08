package me.oskarmendel.chip8;

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

	/**
	 * Initializes the memory.
	 */
	public Memory() {
		pc = 0x200; // Program counter always starts at 0x200.
		opcode = 0; // Reset current opcode.
		I = 0; // Reset the index register.
		sp = 0; // Reset the stack pointer.
		
		//Reset stack and the V registers.
		for (int i = 0; i < 16; ++i) {
			stack[i] = 0;
			V[i] = 0;
		}
	}

	/**
	 * 
	 */
	public void loadProgram() {
		for (int i = 0; i < 10; ++i) {

		}
	}

	/**
	 * Fetches a single opcode.
	 */
	public void fetchOpcode() {
		opcode = (memory[pc] << 8 | memory[pc + 1]);
	}

	/**
	 * Decodes an opcode and performs target action.
	 */
	public void decodeOpcode() {
		switch (opcode) {
		case 0x00E0:
			// Clear display
			return;
		case 0x00EE:
			//Returns from a subroutine
			pc = stack[sp--];
			return;
		}
		
		switch(opcode & 0xF000) {
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
			if (V[(opcode & 0x0F00) >> 8] == (opcode & 0x00FF)) {
				pc += 4;
			} else {
				pc += 2;
			}
			return;
		case 0x4000:
			// 4XNN - Skip next instruction if Vx != kk.
			if (V[(opcode & 0x0F00) >> 8] != (opcode & 0x00FF)) {
				pc += 4;
			} else {
				pc += 2;
			}
			return;
		case 0x5000:
			// 5XY0 - Skip next instruction if Vx = Vy.
			// 
			if (V[(opcode & 0x0F00) >> 8] == V[(opcode & 0x00F0) >> 4]) {
				pc += 4;
			} else {
				pc += 2;
			}
			return;
		case 0x6000:
			// 6XNN - Set Vx = kk.
			V[(opcode & 0x0F00) >> 8] = (opcode & 0x00FF);
			pc += 2;
			return;
		}
	}
}
