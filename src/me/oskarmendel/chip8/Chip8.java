package me.oskarmendel.chip8;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
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
	private Screen screen;
	private Keyboard keyboard;

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
		
		screen = new Screen();
		keyboard = new Keyboard();
		memory = new Memory(screen, keyboard);
		
		//Load fontset
		screen.render();

		Group root = new Group();
		root.getChildren().add(screen);
		
		Scene mainScene = new Scene(root);
		
		mainStage.setScene(mainScene);
		mainStage.setMaxWidth(SCREEN_WIDTH);
		mainStage.setMaxHeight(SCREEN_HEIGHT);
		mainStage.setMinWidth(SCREEN_WIDTH);
		mainStage.setMinHeight(SCREEN_HEIGHT);
		
		loadProgram("roms/PONG");
		
		new AnimationTimer() {
			@Override
			public void handle(long currentNanoTime) {
				//Fetch opcode
				memory.fetchOpcode();
				
				//Decode & Execute opcode
				memory.decodeOpcode();
				
				screen.render();
				
				//Update Timers
				if (memory.getDelayTimer() > 0) {
					memory.setDelayTimer(memory.getDelayTimer() - 1);
				}
				
				if (memory.getSoundTimer() > 0) {
					if (memory.getSoundTimer() == 1) {
						System.out.println("Make Sound!");
					}
					memory.setSoundTimer(memory.getSoundTimer() - 1);
				}
			}
		}.start();
		
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
		
		System.out.println("Delay timer: " + memory.getDelayTimer());
		//Update Timers
		if (memory.getDelayTimer() > 0) {
			memory.setDelayTimer(memory.getDelayTimer() - 1);
		}
		
		if (memory.getSoundTimer() > 0) {
			if (memory.getSoundTimer() == 1) {
				System.out.println("Make Sound!");
			}
			memory.setSoundTimer(memory.getSoundTimer() - 1);
		}
		
		screen.render();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		initialize();
	}
}
