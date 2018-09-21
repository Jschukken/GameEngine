package ToolBox;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import Entities.SingleEntity;
import Models.CompoundShape;
import Models.Quad;
import Models.Rect;

public class Optimizer {

	/**
	 * removes hidden blocks from a concept map
	 * 
	 * @param concept
	 * @return
	 */
	public static int[][][] removeHiddenVoxels(int[][][] concept) {
		for (int x = 1; x < concept.length - 1; x++) {
			for (int y = 0; y < concept[0].length - 1; y++) {
				for (int z = 1; z < concept[0][0].length - 1; z++) {
					if (isBlockHidden(concept, x, y, z)) {
						concept[x][y][z] *= -1;
					}
				}
			}
		}
		return concept;
	}

	/**
	 * creates a CompoundEntity out of a concept map
	 * 
	 * @param concept
	 *            the concept map
	 * @return the Entity
	 */
	public static SingleEntity instantiateConcept(int[][][] concept) {
		// float time = System.nanoTime();
		Rect[][][] groupingArray = new Rect[concept.length][concept[0].length][concept[0][0].length];

		for (int x = 0; x < concept.length; x++) {
			for (int y = 0; y < concept[0].length; y++) {
				for (int z = 0; z < concept[0][0].length; z++) {
					if (concept[x][y][z] > 0) {
						groupingArray[x][y][z] = new Rect(new Vector3f(x, y, z), 0, 0, 0, new Vector3f(1, 1, 1), new Vector3f(.3f, 1, .3f));
					} else {
						groupingArray[x][y][z] = null;
					}
				}
			}
		}

		boolean changed = true;

		while (changed) {
			changed = combineLeft(groupingArray) || combineFront(groupingArray) || combineBelow(groupingArray);
		}

		// System.out.println("combine time: " + (System.nanoTime() - time));
		return AssetCreator.createMesh(createShapeFromConcept(groupingArray));
	}

	/**
	 * creates a single CompoundEntity from a concept map of rectangles
	 * 
	 * @param concept
	 *            the map of rectangles
	 * @return the compoundEntity
	 */
	public static CompoundShape createShapeFromConcept(Rect[][][] concept) {

		Set<Rect> rectSet = new HashSet<Rect>();

		for (int x = 0; x < concept.length; x++) {
			for (int y = 0; y < concept[0].length; y++) {
				for (int z = 0; z < concept[0][0].length; z++) {
					if (concept[x][y][z] != null) {
						rectSet.add(concept[x][y][z]);
						z += concept[x][y][z].getScale().getZ();
						// System.out.println(concept[x][y][z].getScale());
					}
				}
			}
		}
		// System.out.println(rectSet.size());
		CompoundShape[] a = new CompoundShape[rectSet.size()];
		Quad[] empty = new Quad[0];
		return new CompoundShape(empty, rectSet.toArray(a), new Vector3f(), 0, 0, 0, new Vector3f(1, 1, 1));
	}

	/**
	 * returns if a block in a concept map is hidden or not
	 */
	private static boolean isBlockHidden(int[][][] concept, int x, int y, int z) {
		if (y != 0 && concept[x + 1][y][z] != 0 && concept[x - 1][y][z] != 0 && concept[x][y + 1][z] != 0
				&& concept[x][y - 1][z] != 0 && concept[x][y][z + 1] != 0 && concept[x][y][z - 1] != 0) {
			return true;
		} else if (y == 0 && concept[x + 1][y][z] != 0 && concept[x - 1][y][z] != 0 && concept[x][y + 1][z] != 0
				&& concept[x][y][z + 1] != 0 && concept[x][y][z - 1] != 0) {
			return true;
		}
		return false;
	}

	/**
	 * iterates through a rect concept map trying to combine rectangles and the
	 * rectangle to the left of it
	 * 
	 * @param groupingArray
	 *            the rectangle concept map
	 * @return true if it combined any rectangles
	 */
	private static boolean combineLeft(Rect[][][] groupingArray) {
		boolean changed = false;

		for (int y = 0; y < groupingArray[0].length; y++) {
			for (int z = 0; z < groupingArray[0][0].length; z++) {
				for (int x = 0; x < groupingArray.length - 1; x++) {
					if (groupingArray[x][y][z] != null && groupingArray[x + 1][y][z] != null
							&& groupingArray[x][y][z] != groupingArray[x + 1][y][z]
							&& groupingArray[x][y][z].addRectLeft(groupingArray[x + 1][y][z])) {
						updateGroupingArray(groupingArray, groupingArray[x + 1][y][z], groupingArray[x][y][z]);
						changed = true;
						x += groupingArray[x][y][z].getScale().getX();
					}
				}
			}
		}

		return changed;
	}

	/**
	 * iterates through a rect concept map trying to combine rectangles and the
	 * rectangle in front of it
	 * 
	 * @param groupingArray
	 *            the rectangle concept map
	 * @return true if it combined any rectangles
	 */
	private static boolean combineFront(Rect[][][] groupingArray) {
		boolean changed = false;
		for (int y = 0; y < groupingArray[0].length; y++) {
			for (int x = 0; x < groupingArray.length; x++) {
				for (int z = 0; z < groupingArray[0][0].length - 1; z++) {
					if (groupingArray[x][y][z] != null && groupingArray[x][y][z + 1] != null
							&& groupingArray[x][y][z] != groupingArray[x][y][z + 1]
							&& groupingArray[x][y][z].addRectFront(groupingArray[x][y][z + 1])) {
						updateGroupingArray(groupingArray, groupingArray[x][y][z + 1], groupingArray[x][y][z]);
						changed = true;
						z += groupingArray[x][y][z].getScale().getZ();
					}
				}
			}
		}

		return changed;
	}

	/**
	 * iterates through a rect concept map trying to combine rectangles and the
	 * rectangle below of it
	 * 
	 * @param groupingArray
	 *            the rectangle concept map
	 * @return true if it combined any rectangles
	 */
	private static boolean combineBelow(Rect[][][] groupingArray) {
		boolean changed = false;

		for (int x = 0; x < groupingArray.length; x++) {
			for (int z = 0; z < groupingArray[0][0].length; z++) {
				for (int y = groupingArray[0].length - 1; y >= 1; y--) {
					if (groupingArray[x][y][z] != null && groupingArray[x][y - 1][z] != null
							&& groupingArray[x][y][z] != groupingArray[x][y - 1][z]
							&& groupingArray[x][y][z].addRectBelow(groupingArray[x][y - 1][z])) {
						updateGroupingArray(groupingArray, groupingArray[x][y - 1][z], groupingArray[x][y][z]);
						changed = true;
						y -= groupingArray[x][y][z].getScale().getY();
					}
				}
			}
		}

		return changed;
	}

	/**
	 * updates groupingArray when two rectangles are combined.
	 * 
	 * @param groupingArray
	 * @param oldRect
	 * @param newRect
	 */
	private static void updateGroupingArray(Rect[][][] groupingArray, Rect oldRect, Rect newRect) {
		for (int x = (int) oldRect.getPosition().getX(); x < oldRect.getPosition().getX()
				+ oldRect.getScale().getX(); x++) {
			for (int y = (int) oldRect.getPosition().getY(); y > oldRect.getPosition().getY()
					- oldRect.getScale().getY(); y--) {
				for (int z = (int) oldRect.getPosition().getZ(); z < oldRect.getPosition().getZ()
						+ oldRect.getScale().getZ(); z++) {
					groupingArray[x][y][z] = newRect;
				}
			}
		}
	}

}
