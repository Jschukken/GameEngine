package Entities;

import org.lwjgl.util.vector.Vector3f;

/**
 * represents a compound entity in the world
 * models are loaded into the gpu
 * @author Jelle Schukken
 *
 */
public class CompoundEntity extends Shape {
	
	
	SingleEntity[] singleComponents;
	CompoundEntity[] compoundComponents;

	public CompoundEntity(SingleEntity[] singleComponents, CompoundEntity[] compoundComponents, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(position, rotX, rotY, rotZ, scale);
		this.singleComponents = singleComponents;
		this.compoundComponents = compoundComponents;
	}
	
	/**
	 * returns the entity at index index
	 * @param index the index of the entity
	 * @return the transformation matrix
	 */
	public SingleEntity getSingleEntity(int index){
		return singleComponents[index];
	}
	
	/**
	 * returns the entity at index index
	 * @param index the index of the entity
	 * @return the transformation matrix
	 */
	public CompoundEntity getCompoundEntity(int index){
		return compoundComponents[index];
	}
	
	public int getSingleComponentCount(){
		return singleComponents.length;
	}
	
	public int getCompoundComponentCount(){
		return compoundComponents.length;
	}
}
