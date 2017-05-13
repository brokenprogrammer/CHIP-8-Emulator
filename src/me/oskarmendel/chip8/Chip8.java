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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main entry point of the application.
 * 
 * @author Oskar Mendel
 * @version 0.00.00
 * @name Chip8.java
 */
public class Chip8 extends Application {

	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 450;

	private Stage mainStage;

	Timeline gameLoop;
	
	private Memory memory;
	private Screen screen;
	private Keyboard keyboard;
	
	/**
	 * Setup the graphics and input systen and clear the memory and screen.
	 */
	private void initialize() {
		mainStage.setTitle("CHIP-8-Emulator");

		screen = new Screen();
		keyboard = new Keyboard();
		
		// Initialize menu that contains buttons for exiting and switching applications to run.
		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("File");
		
		MenuItem loadRomItem = new MenuItem("Load ROM");
		loadRomItem.setOnAction(e -> {
			// Open file choose to let the user select a ROM.
			FileChooser f = new FileChooser();
			f.setTitle("Open ROM File");
			File file = f.showOpenDialog(mainStage);
			
			if (file != null) {
				loadProgram(file.getPath());
			}
		});
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> {
			System.exit(0);
		});
		
		menuFile.getItems().add(loadRomItem);
		menuFile.getItems().add(exitItem);
		
		menuBar.getMenus().add(menuFile);
		
		// Initial render of the screen.
		screen.render();

		// Place all elements into the main window.
		VBox root = new VBox();
		root.getChildren().add(menuBar);
		root.getChildren().add(screen);

		
		Scene mainScene = new Scene(root);

		// Handle key presses.
		mainScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				keyboard.setKeyDown(e.getCode());
			}
		});

		// Handle key releases.
		mainScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				keyboard.setKeyUp(e.getCode());
			}
		});

		
		// Set up the main window for show.
		mainStage.setScene(mainScene);
		mainStage.setMaxWidth(SCREEN_WIDTH);
		mainStage.setMaxHeight(SCREEN_HEIGHT);
		mainStage.setMinWidth(SCREEN_WIDTH);
		mainStage.setMinHeight(SCREEN_HEIGHT);
		mainStage.setResizable(false);
		
		gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);
		
        // Construct the keyframe telling the application what to happen inside the game loop.
        KeyFrame kf = new KeyFrame(
                Duration.seconds(0.003),
                actionEvent -> {
                    try {
                    	// Fetch opcode
    					memory.fetchOpcode();
    					// Decode & Execute opcode
    					memory.decodeOpcode();
                    } catch (RuntimeException e) {
                        gameLoop.stop();
                    }
                    
                    // Render
                    if (memory.isDrawFlag()) {
                        screen.render();
                        memory.setDrawFlag(false);
                    }

	                 // Update Timers
					if (memory.getDelayTimer() > 0) {
						memory.setDelayTimer(memory.getDelayTimer() - 1);
					}
	
					if (memory.getSoundTimer() > 0) {
						if (memory.getSoundTimer() == 1) {
							System.out.println("Make Sound!");
						}
						memory.setSoundTimer(memory.getSoundTimer() - 1);
					}
                });

        gameLoop.getKeyFrames().add(kf);
		loadProgram("roms/INVADERS");
        
		mainStage.show();
	}

	/**
	 * Copy the program to run into the memory
	 * 
	 * @param program
	 *            - The program to copy into memory.
	 */
	private void loadProgram(String program) {
		gameLoop.stop();
		
		screen.clear();
		memory = new Memory(screen, keyboard);
		
		// Load binary and pass it to memory
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
		
		gameLoop.play();
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
