package Map;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import Entities.SingleEntity;
import ToolBox.AssetCreator;
import ToolBox.Optimizer;

/**
 * represents a chunk in the world
 * @author Jelle Schukken
 *
 */
public class Chunk {
	
	public static final int CHUNK_LENGTH = 10;
	public static final int CHUNK_HEIGHT = 10;
	public static final int CHUNK_WIDTH = 10;

	int[][][] concept;
	ArrayList<Light> lights = new ArrayList<Light>();
	SingleEntity mesh;
	List<Integer> chunkPosition;
	boolean loaded;
	
	/**
	 * take an unoptimized input, optimizes it, and 
	 * @param concept
	 */
	public Chunk(int[][][] concept, List<Integer> chunkPosition){
		this.chunkPosition = chunkPosition;
		this.concept = concept;
		loaded = false;
	}
	
	/**
	 * takes a preoptimised input and creates a chunk with it
	 * @param concept
	 * @param mesh
	 */
	public Chunk(int[][][] concept, SingleEntity mesh, List<Integer> chunkPosition){
		this.concept = Optimizer.removeHiddenVoxels(concept);
		this.mesh = mesh;
		this.chunkPosition = chunkPosition;
		this.mesh.setPosition(new Vector3f(chunkPosition.get(0) * CHUNK_LENGTH, 0f, chunkPosition.get(1) * CHUNK_WIDTH));
		loaded = true;
	}

	public int[][][] getConcept() {
		return concept;
	}

	public void setConcept(int[][][] concept) {
		this.concept = concept;
	}

	public SingleEntity getMesh() {
		if(loaded){
			return mesh;
		}else{
			System.err.println("Chunk.getMesh: cannot get mesh of unloaded chunk");
			System.exit(-1);
			return null;
		}
	}

	public void setMesh(SingleEntity mesh) {
		this.mesh = mesh;
	}

	public List<Integer> getChunkPosition() {
		return chunkPosition;
	}
	
	public void load(){
		//float time = System.nanoTime();
		getLights();
		for(Light l : lights){
			LightManager.loadLight(l);
		}
		mesh = Optimizer.instantiateConcept(concept);
		mesh.setPosition(new Vector3f(chunkPosition.get(0) * CHUNK_LENGTH, 0f, chunkPosition.get(1) * CHUNK_WIDTH));
		loaded = true;
		//System.out.println("load time: " + (System.nanoTime() - time));
	}
	
	public void unLoad(){
		AssetCreator.unLoad(mesh);
		mesh = null;
		for(Light l : lights){
			LightManager.UnloadLight(l);
		}
		loaded = false;
	}
	
	/**
	 * returns all lights in concept map 
	 * @return the arraylist of lights in concept map
	 */
	private ArrayList<Light> getLights(){
		if(lights.size() > 0){
			return lights;
		}else{
			for(int x = 0; x < concept.length; x++){
				for(int y = 0; y < concept[0].length; y++){
					for(int z = 0; z < concept[0][0].length; z++){
						if(concept[x][y][z] == 2){
							lights.add(
									new Light(new Vector3f(0,0f,0f),
											new Vector3f(.07f,.07f,.07f),
											new Vector3f(.1f,.1f,.1f),
											new Vector3f(chunkPosition.get(0) * CHUNK_LENGTH + x, y, chunkPosition.get(1) * CHUNK_WIDTH + z)));
						}
					}
				}
			}
			return lights;
		}
		
	}
	
}
