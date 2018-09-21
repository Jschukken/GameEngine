package Models;

import org.lwjgl.util.vector.Vector3f;

import Entities.Shape;

/**
 * represents a compound shape in the world
 * does not load anything to the gpu
 * @author Jelle Schukken
 *
 */
public class CompoundShape extends Shape{
	
	Quad[] quads;
	CompoundShape[] compoundComponents;

	public CompoundShape(Quad[] quads, CompoundShape[] compoundComponents, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(position, rotX, rotY, rotZ, scale);
		this.quads = quads;
		this.compoundComponents = compoundComponents;
	}
	
	/**
	 * returns the model of the entity at index index
	 * @param index the index of the entity
	 * @return the transformation matrix
	 */
	public Quad getQuad(int index){
		return quads[index];
	}
	
	/**
	 * returns the model of the entity at index index
	 * @param index the index of the entity
	 * @return the transformation matrix
	 */
	public CompoundShape getCompoundShape(int index){
		return compoundComponents[index];
	}
	
	public int getQuadCount(){
		return quads.length;
	}
	
	public int getCompoundComponentCount(){
		return compoundComponents.length;
	}

	public void setQuads(Quad[] quads) {
		this.quads = quads;
	}

	public void setCompoundComponents(CompoundShape[] compoundComponents) {
		this.compoundComponents = compoundComponents;
	}
	
	

	
}
