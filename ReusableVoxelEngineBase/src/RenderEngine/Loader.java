package RenderEngine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import Models.RawModel;

public class Loader {
	
	static List<Integer> vaos = new ArrayList<Integer>();
	static List<Integer> vbos = new ArrayList<Integer>();
	ArrayList<Integer> modelVboIDs;
	
	/**
	 * takes a list of vertices and creates a RawModel
	 * @param vertices
	 * @return
	 */
	public RawModel loadToVao(float[] vertices, float[] normals, float[] colors, int[] indices){
		
		int vaoID = createVAO();
		modelVboIDs = new ArrayList<Integer>();
		storeDataInAttributeList(vertices, 0, 3);
		storeDataInAttributeList(normals, 1, 3);
		storeDataInAttributeList(colors, 2, 3);
		bindIndicesBuffer(indices);
		GL30.glBindVertexArray(0);
		
		return new RawModel(vaoID, modelVboIDs, indices.length);

	}
	
	/**
	 * sets up a vertex array object
	 * @return the created object's ID
	 */
	private int createVAO(){
		
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		vaos.add(vaoID);
		return vaoID;
		
	}
	
	/**
	 * creates a vertex Buffer Object (vbo) and stores the RawModels vertices into it
	 */
	private void storeDataInAttributeList(float[] data, int attributeNumber, int dimensions){
		
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		modelVboIDs.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);//might not be static TODO: possible change
		GL20.glVertexAttribPointer(attributeNumber, dimensions, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	/**
	 * creates a vertex buffer and loads the RawModels indices into it
	 * @param indices the indices to load
	 */
	private void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		modelVboIDs.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	/**
	 * stores data in a IntBuffer
	 * @param data the data to store
	 * @return the buffer
	 */
	private IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	/**
	 * stores data in a FloatBuffer
	 * @param data the data to store
	 * @return the buffer
	 */
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public void unloadFromVao(RawModel model){
		
		GL30.glDeleteVertexArrays(model.getVaoID());
		vaos.remove(new Integer(model.getVaoID()));
		
		for(Integer i : model.getVboIDs()){
			GL15.glDeleteBuffers(i);
			vbos.remove(new Integer(i));
		}
	}
	
	/**
	 * cleans up all VAO's and VBO's
	 */
	public void cleanUp(){
		
		for(Integer i : vaos){
			GL30.glDeleteVertexArrays(i);
		}
		
		for(Integer i : vbos){
			GL15.glDeleteBuffers(i);
		}
	}
}
