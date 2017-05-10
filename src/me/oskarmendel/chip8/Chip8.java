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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
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
	
	private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(2); 
	private ScheduledFuture<?> cpuThread;
    private ScheduledFuture<?> displayThread;
	
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
	
		screen.render();

		Group root = new Group();
		root.getChildren().add(screen);
		
		Scene mainScene = new Scene(root);
		
		mainScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				keyboard.setKeyDown(e.getCode());
			}
		});
		
		mainScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				keyboard.setKeyUp(e.getCode());
			}
		});
		
		mainStage.setScene(mainScene);
		mainStage.setMaxWidth(SCREEN_WIDTH);
		mainStage.setMaxHeight(SCREEN_HEIGHT);
		mainStage.setMinWidth(SCREEN_WIDTH);
		mainStage.setMinHeight(SCREEN_HEIGHT);
		
		loadProgram("roms/TETRIS");
		
		emulationLoop();
		
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
		 // 500 operations/s
        cpuThread = threadPool.scheduleWithFixedDelay(() -> {
        	//Fetch opcode
			memory.fetchOpcode();
			
			//Decode & Execute opcode
			memory.decodeOpcode();
        }, 2, 2, TimeUnit.MILLISECONDS);
        
        // ~60Hz
        displayThread = threadPool.scheduleWithFixedDelay(() -> {
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
        }, 17, 17, TimeUnit.MILLISECONDS);
	}
	
	public void stopThreadPool() {
        if (cpuThread != null) {
            cpuThread.cancel(true);
            displayThread.cancel(true);
        }
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
