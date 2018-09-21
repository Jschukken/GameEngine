package RenderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import MainGame.MainGameLoop;
import Map.MapManager;
import ToolBox.AssetCreator;

public class DisplayManager {

	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	private static final int FPS_CAP = 60;

	private static final boolean FULLSCREEN = false;

	/**
	 * creates the display window and connects openGL to it
	 * @throws Exception if there is an error creating window
	 */
	public static void createDisplay() throws Exception {

		ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);

		try {
			if (!FULLSCREEN) {
				Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			}
			Display.setVSyncEnabled(true);
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("YOUR TITLE");
			Display.setFullscreen(true);
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());

		} catch (LWJGLException e) {
			e.printStackTrace();
			throw new Exception("Display.createDisplay: error setting display mode");
		}
		
		Mouse.setGrabbed(true);
	}

	/**
	 * updates display every frame, limited by FPS_CAP
	 */
	public static void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();

		/*
		 * Temporary mouse and window shit
		 * TODO: move this to mouse manager
		 */
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && Mouse.isGrabbed()) {
					closeDisplay();
				}
				
				if (Keyboard.isKeyDown(Keyboard.KEY_E) && Mouse.isGrabbed()) {
					Mouse.setGrabbed(false);
				} else if (Keyboard.isKeyDown(Keyboard.KEY_E) && !Mouse.isGrabbed()) {
					Mouse.setGrabbed(true);
				}
			}
		}
	}

	/**
	 * closes display, cleaning up data
	 */
	public static void closeDisplay() {
		MapManager.saveMap();
		AssetCreator.loader.cleanUp();
		MainGameLoop.shader.cleanUp();
		
		Display.destroy();
		System.exit(0);
	}

}
