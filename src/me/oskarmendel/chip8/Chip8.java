package me.oskarmendel.chip8;

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
	
	
	
	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("CHIP-8-Emulator");
		
		
		
		//TEMP
		primaryStage.setMaxWidth(SCREEN_WIDTH);
		primaryStage.setMaxHeight(SCREEN_HEIGHT);
		primaryStage.setMinWidth(SCREEN_WIDTH);
		primaryStage.setMinHeight(SCREEN_HEIGHT);
		primaryStage.show();
	}
}
