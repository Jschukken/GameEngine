package Map;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * represents a region of the world
 * Comprised of a series of chunks.
 * @author Jelle Schukken
 *
 */
public class Region {

	public static final int REGION_LENGTH = 3;
	public static final int REGION_WIDTH = 3;

	private HashMap<List<Integer>, int[][][]> regionChunks = new HashMap<List<Integer>, int[][][]>();

	private int regionX, regionY, numLoaded;

	public Region(int regionX, int regionY) {
		this.regionX = regionX;
		this.regionY = regionY;
		numLoaded = 0;
		loadRegion();
	}

	/**
	 * checks if a version of this region exists, if so, loads it from a file
	 * @return
	 */
	private boolean loadRegion() {
		if (new File(MapManager.mapFolder, regionX + "x" + regionY + "y.txt").exists()) {

			Path file = Paths.get(MapManager.mapFolder + "/" + regionX + "x" + regionY + "y.txt");
			try {
				Stream<String> fileStream = Files.lines(file);
				fileStream.forEach(new Consumer<String>() {

					@Override
					public void accept(String line) {
						ArrayList<Integer> key = new ArrayList<Integer>();
						int[][][] loadedConcept = new int[Chunk.CHUNK_LENGTH][Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
						key.add(Integer.parseInt(line.substring(0, line.indexOf(","))));
						line = line.substring(line.indexOf(",") + 1);
						key.add(Integer.parseInt(line.substring(0, line.indexOf(":"))));
						line = line.substring(line.indexOf(":") + 1);
						int x = 0;
						int y = 0;
						int z = 0;
						while (line.length() > 0) {
							if (line.charAt(0) != '[' && line.charAt(0) != ']' && line.charAt(0) != '/') {
								loadedConcept[x][y][z] = Integer.parseInt(line.substring(0, line.indexOf("]")));
								line = line.substring(line.indexOf("]"));
							} else if (line.charAt(0) == '[') {
								line = line.substring(1);
							} else if (line.charAt(0) == ']') {
								line = line.substring(1);
								z++;
								if (line.charAt(0) == ']') {
									line = line.substring(1);
									y++;
									z = 0;
									if (line.charAt(0) == ']') {
										line = line.substring(1);
										x++;
										y = 0;
									}
								}
							} else {
								line = line.substring(1);
							}
						}

						regionChunks.put(key, loadedConcept);

					}

				});
			} catch (IOException e) {
				System.out.println("ChunkManager.loadMap: file not found, or could not be read");
				e.printStackTrace();
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * saves this region to a file
	 */
	public void saveRegion() {
		List<String> chunks = new ArrayList<String>();
		for (List<Integer> key : regionChunks.keySet()) {
			String chunkCode = key.get(0) + "," + key.get(1) + ":";
			for (int x = 0; x < regionChunks.get(key).length; x++) {
				for (int y = 0; y < regionChunks.get(key)[0].length; y++) {
					for (int z = 0; z < regionChunks.get(key)[0][0].length; z++) {
						chunkCode += regionChunks.get(key)[x][y][z];
						chunkCode += "]";
					}
					chunkCode += "]";
				}
				chunkCode += "]";
			}
			chunkCode += "/";

			chunkCode += "/";

			chunks.add(chunkCode);
		}

		Path file = Paths.get(MapManager.mapFolder + "/" + regionX + "x" + regionY + "y.txt");
		try {
			Files.write(file, chunks, Charset.forName("UTF-8"));
		} catch (IOException e) {
			System.err.println("ChunkManager.saveMap: could not save to existing file");
			e.printStackTrace();
		}
	}

	/**
	 * incremements the number of loaded chunks in this region
	 * @return
	 */
	public int incNumLoaded(){
		numLoaded++;
		return numLoaded;
	}
	
	
	/**
	 * decrements the number of loaded chunks in this region
	 * @return
	 */
	public int decNumLoaded(){
		numLoaded--;
		return numLoaded;
	}
	
	public int getNumLoaded(){
		return numLoaded;
	}
	
	public boolean containsKey(List<Integer> key) {
		return regionChunks.containsKey(key);
	}

	public void put(List<Integer> key, int[][][] object) {
		regionChunks.put(key, object);
	}

	public int[][][] get(List<Integer> key) {
		return regionChunks.get(key);
	}

	public int getX() {
		return regionX;
	}

	public int getY() {
		return regionY;
	}

}
