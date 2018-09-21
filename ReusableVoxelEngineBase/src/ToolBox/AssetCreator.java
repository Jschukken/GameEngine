package ToolBox;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Entities.CompoundEntity;
import Entities.SingleEntity;
import Models.CompoundShape;
import Models.Quad;
import Models.RawModel;
import RenderEngine.Loader;

public class AssetCreator {

	public static Loader loader = new Loader();

	static float[] vertices = {

			-.5f, .5f, 0, -0.5f, -.5f, 0, 0.5f, -0.5f, 0, 0.5f, 0.5f, 0,

	};

	static int[] indices = { 0, 1, 2, 2, 3, 0 };

	static float[] normals = { 0, 0, 1f, 0, 0, 1f, 0, 0, 1f };
	static float[] colors = {

			-.5f, .5f, 0, -0.5f, -.5f, 0, 0.5f, -0.5f, 0, 0.5f, 0.5f, 0,

	};

	final static RawModel quad = loader.loadToVao(vertices, normals, colors, indices);

	/**
	 * makes a CompoundEntity representing a rectangle
	 * 
	 * @param length
	 *            of rectangle (ie size in x direction)
	 * @param width
	 *            width of rectangle (ie size in z direction)
	 * @param height
	 *            height of rectangle (ie size in y direction)
	 * @return
	 */
	public static CompoundEntity makeRectangle(float length, float height, float width) {

		SingleEntity[] components = new SingleEntity[6];

		SingleEntity entity = new SingleEntity(quad, new Vector3f(0, 0, .5f), 0, 0, 0, new Vector3f(1, 1, 1));// front
																												// face
																												// xy
		components[0] = entity;

		entity = new SingleEntity(quad, new Vector3f(0, 0, -.5f), 0, (float) Math.PI, 0, new Vector3f(1, 1, 1));// back
																												// face
																												// xy
		components[1] = entity;

		entity = new SingleEntity(quad, new Vector3f(-.5f, 0, 0), 0, (float) Math.toRadians(-90), 0,
				new Vector3f(1, 1, 1));// left face yz
		components[2] = entity;

		entity = new SingleEntity(quad, new Vector3f(0.5f, 0, 0), 0, (float) Math.toRadians(90), 0,
				new Vector3f(1, 1, 1));// right face yz
		components[3] = entity;

		entity = new SingleEntity(quad, new Vector3f(0, 0.5f, 0), (float) Math.toRadians(90), 0, 0,
				new Vector3f(1, 1, 1));// top face xz
		components[4] = entity;

		entity = new SingleEntity(quad, new Vector3f(0, -0.5f, 0), (float) Math.toRadians(-90), 0, 0,
				new Vector3f(1, 1, 1));// bottom face xz
		components[5] = entity;

		CompoundEntity[] empty = new CompoundEntity[0];
		return new CompoundEntity(components, empty, new Vector3f(), 0, 0, 0, new Vector3f(length, height, width));

	}

	/**
	 * creates a static mesh out of a CompoundShape
	 * 
	 * @param shape
	 *            the entity
	 * @return the SingleEntity in the shape of the entire compound entity
	 */
	public static SingleEntity createMesh(CompoundShape shape) {
		
		HashMap<ArrayList<Float>, ArrayList<Integer>> data = new HashMap<ArrayList<Float>, ArrayList<Integer>>();
		int indices = 0;

		for (int i = 0; i < shape.getQuadCount(); i++) {
			indices = createMesh(new Matrix4f(), data, indices, shape.getQuad(i));
		}
		for (int i = 0; i < shape.getCompoundComponentCount(); i++) {
			indices = createMesh(new Matrix4f(), data, indices, shape.getCompoundShape(i));
		}

		int[] indicesArray = new int[indices];
		float[] vertexArray = new float[data.size()*3];
		float[] normalArray = new float[data.size()*3];
		float[] colorArray = new float[data.size()*3];
		int i = 0;
		for (ArrayList<Float> key : data.keySet()) {
			vertexArray[i*3] = key.get(0);
			vertexArray[i*3+1] = key.get(1);
			vertexArray[i*3+2] = key.get(2);
			normalArray[i*3] = key.get(3);
			normalArray[i*3+1] = key.get(4);
			normalArray[i*3+2] = key.get(5);
			colorArray[i*3] = key.get(6);
			colorArray[i*3+1] = key.get(7);
			colorArray[i*3+2] = key.get(8);
			for(Integer index : data.get(key)){
				indicesArray[index] = i;
			}
			i++;
		}
//		for(i = 0 ;i < colorArray.length; i+=3){
//			System.out.println("( " + colorArray[i] + ", " + colorArray[i+1] + ", " + colorArray[i+2] + ")");
//		}

		RawModel mesh = loader.loadToVao(vertexArray, normalArray, colorArray, indicesArray);

		// System.out.println("mesh creation time: " + (System.nanoTime() -
		// time));
		return new SingleEntity(mesh, shape.getPosition(), shape.getRotX(), shape.getRotY(), shape.getRotZ(),
				shape.getScale());
	}

	public static void unLoad(SingleEntity entity) {
		loader.unloadFromVao(entity.getModel());
	}

	public static void unLoad(CompoundEntity entity) {
		for (int i = 0; i < entity.getCompoundComponentCount(); i++) {
			unLoad(entity.getCompoundEntity(i));
		}
		for (int i = 0; i < entity.getSingleComponentCount(); i++) {
			unLoad(entity.getSingleEntity(i));
		}
	}

	/**
	 * adds each component to the mesh
	 */
	private static int createMesh(Matrix4f transformationMatrix, HashMap<ArrayList<Float>, ArrayList<Integer>> vertices, int indices, Quad shape) {
		int[] newIndices = shape.getIndices();
		float[] newVertices = shape.getVertices();
		float[] newNormals = shape.getNormals();


		Matrix4f newTransformationMatrix = new Matrix4f();
		Matrix4f.mul(transformationMatrix, shape.getTransformationMatrix(), newTransformationMatrix);

		for (int i = 0; i < newVertices.length; i += 3) {
			Vector4f vertex = new Vector4f(newVertices[i], newVertices[i + 1], newVertices[i + 2], 1.0f);

			Vector4f normal = new Vector4f(newNormals[i]+vertex.x, newNormals[i + 1] + vertex.y, newNormals[i + 2] + vertex.z, 1.0f);
			Matrix4f.transform(newTransformationMatrix, vertex, vertex);
			Matrix4f.transform(newTransformationMatrix, normal, normal);
			
			ArrayList<Integer> dataIndices = new ArrayList<Integer>();
			for(int k = 0; k < newIndices.length; k++){
				if(newIndices[k] == i/3){
					dataIndices.add(indices+k);
				}
			}
			
			ArrayList<Float> data6f = new ArrayList<Float>(); 
			data6f.add(round(vertex.x));
			data6f.add(round(vertex.y));
			data6f.add(round(vertex.z));
			data6f.add(round(normal.x - vertex.x));
			data6f.add(round(normal.y - vertex.y));
			data6f.add(round(normal.z - vertex.z));
			data6f.add(shape.getColor().x);
			data6f.add(shape.getColor().y);
			data6f.add(shape.getColor().z);
			if(vertices.containsKey(data6f)){
				dataIndices.addAll(vertices.get(data6f));
			}
			vertices.put(data6f, dataIndices);
		}
		return (indices + newIndices.length);

	}

	/**
	 * adds a compound component to the mesh
	 * 
	 */
	private static int createMesh(Matrix4f transformationMatrix, HashMap<ArrayList<Float>, ArrayList<Integer>> data, int indices, CompoundShape shape) {

		Matrix4f newTransformationMatrix = new Matrix4f();
		Matrix4f.mul(transformationMatrix, shape.getTransformationMatrix(), newTransformationMatrix);

		for (int i = 0; i < shape.getQuadCount(); i++) {
			indices = createMesh(newTransformationMatrix, data, indices, shape.getQuad(i));
		}
		for (int i = 0; i < shape.getCompoundComponentCount(); i++) {
			indices = createMesh(newTransformationMatrix, data, indices, shape.getCompoundShape(i));
		}
		return indices;

	}

	
	private static float round(float num){
		return Math.round(num*10.0f)/10f;
	}
}
