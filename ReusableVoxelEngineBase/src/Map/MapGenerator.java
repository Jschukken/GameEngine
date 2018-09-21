package Map;

import java.util.List;

public class MapGenerator {

	static boolean first = true;

	/**
	 * temp chunkGenerator for testing
	 * 
	 * @param position
	 * @return
	 */
	public static int[][][] generateConcept(List<Integer> position) {
		int[][][] concept = new int[Chunk.CHUNK_LENGTH][Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
		if (first) {
			for (int x = 0; x < concept.length; x++) {
				for (int y = 0; y < concept[0].length; y++) {
					for (int z = 0; z < concept[0][0].length; z++) {
						if (Math.random() > .5 && y < Chunk.CHUNK_HEIGHT / 2) {
							concept[x][y][z] = 1;
						}else if(Math.random() > 0.999 && y == Chunk.CHUNK_HEIGHT-1){
							concept[x][y][z] = 2;
						}
					}
				}
			}
			first = false;
			return concept;
		} else {
			for (int x = 0; x < concept.length; x++) {
				for (int y = 0; y < concept[0].length; y++) {
					for (int z = 0; z < concept[0][0].length; z++) {
						if (x == 0 || y == 0 || z == 0) {
							concept[x][y][z] = 1;
						}
						if (Math.random() > 0.5) {
							concept[x][y][z] = 1;
						}
						concept[x][y][z] = 1;
					}
				}
			}
			return concept;
		}
	}
}
