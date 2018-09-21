package Map;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;
import RenderEngine.MasterRenderer;
import Shaders.StaticShader;

public class MapManager {

	/*
	 * number of chunks loaded in any direction
	 */
	public static int VIEW_DISTANCE = 10;
	public static int CHUNKS_LOADED_PER_FRAME = 5;
	public static int REGIONS_SAVED_PER_FRAME = 1;

	public static HashMap<List<Integer>, Region> REGIONS = new HashMap<List<Integer>, Region>();

	private static HashMap<List<Integer>, Region> toBeSaved = new HashMap<List<Integer>, Region>();

	static HashMap<List<Integer>, Chunk> UNLOADEDCHUNKS = new HashMap<List<Integer>, Chunk>();

	static HashMap<List<Integer>, Chunk> LOADEDCHUNKS = new HashMap<List<Integer>, Chunk>();

	static String mapFolder = null;

	/**
	 * specifies folder containing map, or creates it
	 * 
	 * @param File
	 *            the file from which to load a map
	 */
	public static void loadMap(String loadFolder, Camera camera) {

		mapFolder = loadFolder;
		Path path = Paths.get(loadFolder);
		if (!Files.exists(path)) {
			if (!(new File(loadFolder).mkdirs())) {
				System.err.println("MapManager.loadMap: could not create directory");
				System.exit(-1);
			}
		}
	}

	/**
	 * saves active regions to respective files
	 */
	public static void saveMap() {

		for (List<Integer> key : REGIONS.keySet()) {
			REGIONS.get(key).saveRegion();
		}
	}

	/**
	 * updates chunk information: loads new chunks, unloads old chunks
	 * 
	 * @param camera
	 *            the camera around which to update chunks
	 */
	public static void updateMap(Camera camera) {
		// updateRegions(camera);
		unloadOldChunks(camera);
		findNewChunks(camera);
		loadNewChunks();
		saveOldRegions();

	}

	/**
	 * renders all loaded chunks
	 */
	public static void renderChunks(MasterRenderer renderer, StaticShader shader) {

		Chunk chunk;
		for (List<Integer> key : LOADEDCHUNKS.keySet()) {
			chunk = LOADEDCHUNKS.get(key);
			renderer.render(chunk.getMesh(), shader);
		}

	}

	/**
	 * all chunks not within range are unloaded and removed from load list
	 * removed regions with no loaded chunks from REGIONS
	 * 
	 * @param camerathe
	 *            camera for which to perform this action
	 */
	private static void unloadOldChunks(Camera camera) {
		Chunk chunk;
		ArrayList<List<Integer>> keysToBeRemoved = new ArrayList<List<Integer>>();
		for (List<Integer> key : UNLOADEDCHUNKS.keySet()) {
			chunk = UNLOADEDCHUNKS.get(key);
			if (!inRange(chunk.getChunkPosition(), getChunkPosition(camera.getPosition()))) {
				keysToBeRemoved.add(key);
			}
		}
		for (List<Integer> key : keysToBeRemoved) {
			List<Integer> regionPos = getChunkRegionPosition(key);
			UNLOADEDCHUNKS.remove(key);
			if (REGIONS.get(regionPos).decNumLoaded() == 0) {
				toBeSaved.put(regionPos, REGIONS.get(regionPos));
				REGIONS.remove(regionPos);
			}
		}

		keysToBeRemoved.clear();
		for (List<Integer> key : LOADEDCHUNKS.keySet()) {
			chunk = LOADEDCHUNKS.get(key);
			if (!inRange(chunk.getChunkPosition(), getChunkPosition(camera.getPosition()))) {
				keysToBeRemoved.add(key);
			}
		}
		for (List<Integer> key : keysToBeRemoved) {
			List<Integer> regionPos = getChunkRegionPosition(key);
			LOADEDCHUNKS.remove(key);
			if (REGIONS.get(regionPos).decNumLoaded() == 0) {
				toBeSaved.put(regionPos, REGIONS.get(regionPos));
				REGIONS.remove(regionPos);
			}
		}
	}
	
	/**
	 * saves regions that no longer have loaded chunks
	 */
	private static void saveOldRegions(){
		if(!toBeSaved.isEmpty()){
			int numRemoved = 0;
			for(List<Integer> key : toBeSaved.keySet()){
				toBeSaved.remove(key).saveRegion();
				numRemoved++;
				if(numRemoved >= REGIONS_SAVED_PER_FRAME){
					return;
				}
			}
		}
	}

	/**
	 * adds all chunks within range that are not currently loaded or in the load
	 * list, to the load list
	 * 
	 * @param camera
	 *            the camera for which to perform this action
	 */
	private static void findNewChunks(Camera camera) {
		int[] cameraPosition = getChunkPosition(camera.getPosition());
		for (int x = cameraPosition[0] - VIEW_DISTANCE; x <= cameraPosition[0] + VIEW_DISTANCE; x++) {
			for (int z = cameraPosition[1] - VIEW_DISTANCE; z <= cameraPosition[1] + VIEW_DISTANCE; z++) {
				List<Integer> key = new ArrayList<Integer>();
				key.add(x);
				key.add(z);
				if (!UNLOADEDCHUNKS.containsKey(key) && !LOADEDCHUNKS.containsKey(key)) {
					UNLOADEDCHUNKS.put(key, getChunk(key));
				}
			}
		}
	}

	/**
	 * loads new chunks from unloadedchunk List
	 */
	private static void loadNewChunks() {
		int chunksLoadedThisFrame = 0;
		Chunk chunk;
		ArrayList<List<Integer>> keysLoaded = new ArrayList<List<Integer>>();
		for (List<Integer> key : UNLOADEDCHUNKS.keySet()) {
			keysLoaded.add(key);
			chunk = UNLOADEDCHUNKS.get(key);
			chunk.load();
			chunksLoadedThisFrame++;
			if (chunksLoadedThisFrame >= CHUNKS_LOADED_PER_FRAME) {
				break;
			}
		}

		for (List<Integer> key : keysLoaded) {
			LOADEDCHUNKS.put(key, UNLOADEDCHUNKS.get(key));
			UNLOADEDCHUNKS.remove(key);
		}
	}

	/**
	 * gets chunk data from the active regions or the unsaved regions if it exists, or generates
	 * a new chunk and updates the corisponding regions
	 * 
	 * @param position
	 *            the position of the chunk
	 * @return the chunk at position position
	 */
	private static Chunk getChunk(List<Integer> position) {
		List<Integer> chunkRegionPos = getChunkRegionPosition(position);
		boolean loaded = false;
		if (REGIONS.containsKey(chunkRegionPos)) {
			loaded = true;
			if (REGIONS.get(chunkRegionPos).containsKey(position)) {
				REGIONS.get(chunkRegionPos).incNumLoaded();
				return new Chunk(REGIONS.get(chunkRegionPos).get(position), position);
			} else {
				Chunk chunk = new Chunk(MapGenerator.generateConcept(position), position);
				REGIONS.get(chunkRegionPos).put(position, chunk.getConcept());
				REGIONS.get(chunkRegionPos).incNumLoaded();
				return chunk;
			}
		}else if (toBeSaved.containsKey(chunkRegionPos)){
			REGIONS.put(chunkRegionPos, toBeSaved.get(chunkRegionPos));
			toBeSaved.remove(chunkRegionPos);
			loaded = true;
			if (REGIONS.get(chunkRegionPos).containsKey(position)) {
				REGIONS.get(chunkRegionPos).incNumLoaded();
				return new Chunk(REGIONS.get(chunkRegionPos).get(position), position);
			} else {
				Chunk chunk = new Chunk(MapGenerator.generateConcept(position), position);
				REGIONS.get(chunkRegionPos).put(position, chunk.getConcept());
				REGIONS.get(chunkRegionPos).incNumLoaded();
				return chunk;
			}
		}
		
		if (!loaded) {
			Region newRegion = new Region(chunkRegionPos.get(0), chunkRegionPos.get(1));
			REGIONS.put(chunkRegionPos, newRegion);
			if (newRegion.containsKey(position)) {
				newRegion.incNumLoaded();
				return new Chunk(newRegion.get(position), position);
			} else {
				Chunk chunk = new Chunk(MapGenerator.generateConcept(position), position);
				newRegion.put(position, chunk.getConcept());
				newRegion.incNumLoaded();
				return chunk;
			}

		}
		return null;
	}

	/**
	 * returns if a chunk is within range. range is slightly larger then view
	 * distance to avoid chunks loading and unloading in quick succession
	 * 
	 * @param position
	 *            the position of the chunk
	 * @param cameraPosition
	 *            the position of the camera
	 * @return true if the chunk is in range, false otherwise
	 */
	private static boolean inRange(List<Integer> position, int[] cameraPosition) {
		if (Math.abs((position.get(0) - cameraPosition[0])) > VIEW_DISTANCE * 1.5
				|| Math.abs((position.get(1) - cameraPosition[1])) > VIEW_DISTANCE * 1.5) {
			return false;
		}
		return true;
	}

	/**
	 * returns the cameras position in chunk space
	 * 
	 * @param camera
	 *            the camera
	 * @return the camera position in chunk space
	 */
	public static int[] getChunkPosition(Vector3f position) {
		int[] chunkPosition = { (int) Math.floor(position.getX() / (double) Chunk.CHUNK_LENGTH),
				(int) Math.floor(position.getZ() / (double) Chunk.CHUNK_WIDTH) };
		return chunkPosition;
	}

	/**
	 * returns the chunks position in region space
	 * 
	 * @param camera
	 *            the camera
	 * @return the camera position in chunk space
	 */
	private static List<Integer> getChunkRegionPosition(List<Integer> chunk) {
		int[] position = { (int) Math.floor(chunk.get(0) / (double) Region.REGION_LENGTH),
				(int) Math.floor(chunk.get(1) / (double) Region.REGION_WIDTH) };
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(position[0]);
		list.add(position[1]);
		// System.out.println(position[0] + " : " + position[1]);
		return list;
	}
	
	/**
	 * returns the concept map of a chunk given a position in world space
	 * @param position the position in world space
	 * @return the concept
	 */
	public static int[][][] getChunkMap(Vector3f position){
		int[] intChunkPos = getChunkPosition(position);
		ArrayList<Integer> chunkPos = new ArrayList<Integer>();
		chunkPos.add(intChunkPos[0]);
		chunkPos.add(intChunkPos[1]);
		//System.out.println(chunkPos);
		List<Integer> chunkRegion = getChunkRegionPosition(chunkPos);
		if(REGIONS.get(chunkRegion)==null){	
			return null;
		}
		//System.out.println(chunkRegion);
		return REGIONS.get(chunkRegion).get(chunkPos);
	}
}
