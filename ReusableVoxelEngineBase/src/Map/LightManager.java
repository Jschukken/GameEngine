package Map;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;

/**
 * manages all lights in loaded chunks
 * @author Jelle Schukken
 *
 */
public class LightManager {
	
	public static Sun sun = new Sun();
	private static ArrayList<Light> allLights = new ArrayList<Light>();
	public static Light[] renderedLights = new Light[10];
	
	/**
	 * updates which lights are rendered based on distance to the camera
	 * @param camera the camera
	 */
	public static void update(Camera camera){
		updateRenderedLights(camera);
		sun.move();
	}
	
	public static void loadLight(Light light){
		allLights.add(light);
	}
	
	public static void UnloadLight(Light light){
		allLights.remove(light);
	}
	
	/**
	 * updates which lights are rendered based on distance to the camera
	 * @param camera the camera
	 */
	private static void updateRenderedLights(Camera camera){
		bubbleSortAllLights(camera);
		int i = 0;
		for(;i < allLights.size() && i < 10; i++){
			renderedLights[i] = allLights.get(i);
			//System.out.println(renderedLights[i].getPosition() + " : " + i);
		}
		for(;i<10;i++){
			renderedLights[i] = new Light();
		}
	}
	
	/**
	 * bubble sorts the allAllLights list
	 * @param camera
	 */
	private static void bubbleSortAllLights(Camera camera){
		for(int i = 1; i < allLights.size(); i++){
			float iDist = getDistance(allLights.get(i).getPosition(), camera.getPosition());
			if(getDistance(allLights.get(i-1).getPosition(), camera.getPosition()) > iDist){
				bubbleDownAllLights(i, iDist, camera);
			}
		}
	}
	
	/**
	 * bubble sort helper method
	 * @param index in dex to bubble down
	 * @param indexDistance distance to camera of that index
	 * @param camera the camera
	 */
	private static void bubbleDownAllLights(int index, float indexDistance, Camera camera){
		int i = index-1;
		for(; i >= 0 && indexDistance <= getDistance(allLights.get(i).getPosition(), camera.getPosition()); i--){
			
		}
		allLights.add(i+1, allLights.remove(index));
	}
	
	/**
	 * returns distance of light to camera
	 * @param lightPos the light
	 * @param cameraPos the camera
	 * @return distance of light to camera
	 */
	private static float getDistance(Vector3f lightPos, Vector3f cameraPos){
		float distance = (float)Math.sqrt(Math.pow(lightPos.x-cameraPos.x, 2) + Math.pow(lightPos.y-cameraPos.y, 2) + Math.pow(lightPos.z-cameraPos.z, 2));
		return distance;
	}
	
}
