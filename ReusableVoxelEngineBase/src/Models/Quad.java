package Models;

import org.lwjgl.util.vector.Vector3f;

import Entities.Shape;

/**
 * represents a quad without loading one to the gpu
 * @author Jelle Schukken
 *
 */
public class Quad extends Shape {
	float[] vertices = { -0.5f, .5f, 0, -0.5f, -.5f, 0, 0.5f, -0.5f, 0, 0.5f, 0.5f, 0, };
	float[] normals = { 0, 0, 1f, 0, 0, 1f, 0, 0, 1f, 0, 0, 1f };;
	int[] indices = {0, 1, 2, 2, 3, 0};
	Vector3f color;

	public Quad(Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale, Vector3f color) {
		super(position, rotX, rotY, rotZ, scale);
		this.color = color;
	}
	
	public float[] getVertices(){
		return vertices;
	}
	
	public float[] getNormals(){
		return normals;
	}
	
	public int[] getIndices(){
		return indices;
	}
	
	public Vector3f getColor(){
		return color;
	}

}
