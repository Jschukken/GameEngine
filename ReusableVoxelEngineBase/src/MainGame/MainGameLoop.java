package MainGame;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;
import Entities.CompoundEntity;
import Map.MapManager;
import Map.LightManager;
import RenderEngine.DisplayManager;
import RenderEngine.MasterRenderer;
import Shaders.StaticShader;
import ToolBox.AssetCreator;

public class MainGameLoop {

	public static StaticShader shader = null;
	public static Camera camera = new Camera(new Vector3f(0, 9, 0), 0, 0, 0, new Vector3f(1, 1, 1));

	public static List<CompoundEntity> entities = new ArrayList<CompoundEntity>();

	static Vector3f cameraPosition = new Vector3f(0, 0, 0);

	public static void main(String[] args) throws Exception {

		DisplayManager.createDisplay();

		MasterRenderer renderer = new MasterRenderer();

		/*
		 * temporary testing stuff
		 */

		//MapManager MapManager = new MapManager();
		shader = new StaticShader();

		//CompoundEntity testEntity = AssetCreator.makeRectangle(1f, 1f, 1f);
		// SingleEntity testEntity2 = AssetCreator.createMesh(testEntity);
		//entities.add(testEntity);

		renderer.prepareRenderer();
		renderer.prepareShader(shader);

		MapManager.loadMap("newMap", camera);

		while (!Display.isCloseRequested()) {

			renderer.prepareFrame();

			camera.move();

			MapManager.updateMap(camera);
			LightManager.update(camera);
			MapManager.renderChunks(renderer, shader);
			for (CompoundEntity entity : entities) {
				renderer.render(entity, shader);
			}
			DisplayManager.updateDisplay();

		}

	}

}
