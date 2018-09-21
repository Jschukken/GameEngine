package Shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;

/**
 * Creates a shader for use in rendering
 * @author Jelle Schukken
 *
 */
public abstract class ShaderProgram {

	int programID;
	int vertexShaderID;
	int fragmentShaderID;
	
	private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public ShaderProgram(String vertexFile, String fragmentFile) {

		programID = GL20.glCreateProgram();
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);

		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);

		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		
		getAllUniformLocations();
		
	}
	
	public abstract void loadProjectionMatrix(Matrix4f matrix);
	
	public abstract void loadViewMatrix(Camera camera);

	protected abstract void getAllUniformLocations();
	
	protected int getUniformLocation(String varName){
		
		return GL20.glGetUniformLocation(programID, varName);
		
	}
	
	/**
	 * binds values for the inputs to the shader
	 */
	protected abstract void bindAttributes();
	
	//----------------- uniform loaders-------------------
	protected void loadFloat(int location, float value){
		
		GL20.glUniform1f(location, value);
		
	}
	
	protected void loadInt(int location, int value){
		
		GL20.glUniform1i(location, value);
		
	}
	
	protected void load2DVector(int location, Vector2f vec){
		
		GL20.glUniform2f(location, vec.x, vec.y);
		
	}
	
	protected void load3DVector(int location, Vector3f vec){
		
		GL20.glUniform3f(location, vec.x, vec.y, vec.z);
		
	}
	
	protected void loadMatrix(int location, Matrix4f matrix){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location,false,matrixBuffer);
		
	}
	
	protected void loadBool(int location, boolean bool){
		
		if(bool){
			loadFloat(location,1f);
		}else{
			loadFloat(location,0f);
		}
	}
	//------------end uniform loaders----------------

	/**
	 * binds an attribute in the vao to a specific input variable
	 * @param variableName the variable in the shader
	 * @param attribute the index in the vao
	 */
	protected void bindAttribute(String variableName, int attribute) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	public void start() {
		GL20.glUseProgram(programID);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	/**
	 * loads a shader of type type from file file
	 * @param file the file
	 * @param type the type of shader I.E fragment/ vertex
	 * @return
	 */
	private int loadShader(String file, int type) {

		StringBuilder shaderSource = new StringBuilder();

		InputStream in = Class.class.getResourceAsStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("//\n");
			}
		} catch (IOException e) {
			System.err.println("ShaderProgram.loadShader: Error reading shader");
			e.printStackTrace();
			System.exit(-1);
		}

		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);

		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 1000));
			System.err.println("ShaderProgram.loadShader: Error compiling shader");
			System.exit(-1);
		}

		return shaderID;

	}

	/**
	 * unbinds and deletes shader to prevent memory leak.
	 */
	public void cleanUp() {
		
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
		
	}

}
