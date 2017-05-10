package me.oskarmendel.chip8;

import javafx.scene.input.KeyCode;

/**
 * 
 * @author Oskar Mendel
 * @version 0.00.00
 * @name Keyboard.java
 */
public class Keyboard {

	private boolean[] keys = new boolean[16]; // Chip 8 uses a HEX based keypad (0x0 -
	// 0xF), This array stores the state of
	// each key.
	
	public static final int[] FONT = {
			0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x50, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
	};
	
	public Keyboard() {
		for (int i = 0; i < 15; i++) {
			keys[i] = false;
		}
	}

	
	public boolean setKeyDown(KeyCode k) {
		switch (k) {
		case DIGIT1:
			keys[0] = true;
			break;
		case DIGIT2:
			keys[1] = true;
			break;
		case DIGIT3:
			keys[2] = true;
			break;
		case DIGIT4:
			keys[3] = true;
			break;
		case Q:
			keys[4] = true;
			break;
		case W:
			keys[5] = true;
			break;
		case E:
			keys[6] = true;
			break;
		case R:
			keys[7] = true;
			break;
		case A:
			keys[8] = true;
			break;
		case S:
			keys[9] = true;
			break;
		case D:
			keys[10] = true;
			break;
		case F:
			keys[11] = true;
			break;
		case Z:
			keys[12] = true;
			break;
		case X:
			keys[13] = true;
			break;
		case C:
			keys[14] = true;
			break;
		case V:
			keys[15] = true;
			break;
		default:
			break;
		}
		
        return true;
	}
	
	public boolean setKeyUp(KeyCode k) {
		switch (k) {
		case DIGIT1:
			keys[0] = false;
			break;
		case DIGIT2:
			keys[1] = false;
			break;
		case DIGIT3:
			keys[2] = false;
			break;
		case DIGIT4:
			keys[3] = false;
			break;
		case Q:
			keys[4] = false;
			break;
		case W:
			keys[5] = false;
			break;
		case E:
			keys[6] = false;
			break;
		case R:
			keys[7] = false;
			break;
		case A:
			keys[8] = false;
			break;
		case S:
			keys[9] = false;
			break;
		case D:
			keys[10] = false;
			break;
		case F:
			keys[11] = false;
			break;
		case Z:
			keys[12] = false;
			break;
		case X:
			keys[13] = false;
			break;
		case C:
			keys[14] = false;
			break;
		case V:
			keys[15] = false;
			break;
		default:
			break;
		}
		
        return true;
	}

	public boolean isPressed(int j) {
		return keys[j];
	}
}
