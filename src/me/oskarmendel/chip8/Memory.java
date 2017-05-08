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
		
		//Reset memory
		for(int i = 0; i < memory.length; ++i) {
			memory[i] = 0;
		}
		
		// Reset stack and the V registers.
		for (int i = 0; i < 16; ++i) {
			stack[i] = 0;
			V[i] = 0;
		}
	}

	/**
	 * Loads the program into the memory which is placing the program into the memory
	 * starting from the memory location 0x200 (512).
	 * 
	 * @param b - Byte array containing the bytes read from the program file.
	 */
	public void loadProgram(byte[] b) {
		for (int i = 0; i < b.length; ++i) {
			memory[i + 512] = (b[i] & 0xFF);
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
		int x = 0;
		
		switch (opcode) {
		case 0x00E0:
			// Clear display
			return;
		case 0x00EE:
			// Returns from a subroutine
			pc = stack[sp--];
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
			// 7XNN - Set Vx = Vx + kk.
			V[(opcode & 0x0F00) >>> 8] += (opcode & 0x00FF);
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
			
			V[x] = V[x] - V[(opcode & 0x00F0) >>> 4];
			
			pc += 2;
			return;
		case 0x8006:
			// 8XY6 - Set Vx = Vx SHR 1.
			// Shift Vx right by 1. Sets VF to the least significant bit of Vx before shift.
			x = (opcode & 0x0F00) >>> 8;
			V[0xF] = V[x] & 0x1;
			
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
			
			V[x] = V[(opcode & 0x00F0) >>> 4] - V[x];
			
			pc += 2;
			return;
		case 0x800E:
			// 8XYE - Set Vx = Vx SHL 1.
			// Shift Vx left by 1. Sets VF to the value of the most significant bit of Vx before the shift.
			x = (opcode & 0x0F00) >>> 8;
			
			V[0xF] = (V[x] >>> 7) == 1 ? 1 : 0;
			
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
	}
}
