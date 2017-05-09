package me.oskarmendel.chip8;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point of the application.
 * 
 * @author Oskar Mendel
 * @version 0.00.00
 * @name Chip8.java
 */
public class Chip8 extends Application {

	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 600;
	
	private Stage mainStage;
	
	private Memory memory;

	/*
	 * Chip 8 Memory map 0x000-0x1FF - Chip 8 interpreter (contains font set in
	 * emu) 0x050-0x0A0 - Used for the built in 4x5 pixel font set (0-F)
	 * 0x200-0xFFF - Program ROM and work RAM
	 */

	/**
	 * Setup the graphics and input systen and clear the memory and screen.
	 */
	private void initialize() {
		mainStage.setTitle("CHIP-8-Emulator");
		
		Screen screen = new Screen();
		Keyboard keyboard = new Keyboard();
		
		memory = new Memory(screen, keyboard);
		
		//Clear display.
		
		//Load fontset

		// TEMP
		mainStage.setMaxWidth(SCREEN_WIDTH);
		mainStage.setMaxHeight(SCREEN_HEIGHT);
		mainStage.setMinWidth(SCREEN_WIDTH);
		mainStage.setMinHeight(SCREEN_HEIGHT);
		mainStage.show();
	}
	
	/**
	 * Copy the program to run into the memory
	 * 
	 * @param program - The program to copy into memory.
	 */
	private void loadProgram(String program) {
		//Load binary and pass it to memory
		try {
			File f = new File(program);
			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
			
			byte[] b = new byte[(int) f.length()];
			in.read(b);
			
			memory.loadProgram(b);
			in.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The main emulation loop.
	 */
	private void emulationLoop() {
		//Fetch opcode
		memory.fetchOpcode();
		
		//Decode & Execute opcode
		memory.decodeOpcode();
		
		//Update Timers
		if (memory.getDelayTimer() > 0) {
			//delayTimer--;
		}
		
		if (memory.getSoundTimer() > 0) {
			if (memory.getSoundTimer() == 1) {
				System.out.println("Make Sound!");
			}
			//soundTimer--;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		
		
		initialize();
		loadProgram("");
		
		emulationLoop();
	}
}
