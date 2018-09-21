package Entities;

import org.lwjgl.util.vector.Vector3f;

import Map.MapManager;
import Models.RawModel;

/**
 * represents a single Entity in the world model is loaded into gpu
 * 
 * @author Jelle Schukken
 *
 */
public class SingleEntity extends Shape {

	RawModel model;

	public SingleEntity(RawModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(position, rotX, rotY, rotZ, scale);
		this.model = model;
	}

	public RawModel getModel() {
		return model;
	}

	public void setModel(RawModel model) {
		this.model = model;
	}

}
