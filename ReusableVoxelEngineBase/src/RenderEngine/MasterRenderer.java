package RenderEngine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import Entities.CompoundEntity;
import Entities.Shape;
import Entities.SingleEntity;
import Shaders.ShaderProgram;
import Shaders.StaticShader;

public class MasterRenderer {
	
	Matrix4f projectionMatrix;
	
	private static final float FOV = 70f;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 10000f;
	
	public void prepareRenderer(){
		createProjectionMatrix();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public void prepareFrame(){
		GL11.glClearColor(0.4f, 0.7f, 1.0f, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	/**
	 * prepares each shader for rendering
	 */
	public void prepareShader(ShaderProgram shader){
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	/**
	 * creates projection matrix to convert models to camera view
	 */
	public void createProjectionMatrix() {

		projectionMatrix = new Matrix4f();

		float aspect = (float) Display.getWidth() / (float) Display.getHeight();
		float yScale = 1f / (float) Math.tan(Math.toRadians(FOV / 2f));
		float xScale = yScale / aspect;
		float zp = NEAR_PLANE + FAR_PLANE;
		float zm = NEAR_PLANE - FAR_PLANE;

		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = zp / zm;
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = (2 * FAR_PLANE * NEAR_PLANE) / zm;
	}
	
	/**
	 * render an entity with a StaticShader
	 * @param model RawModel to render
	 */
	public void render(SingleEntity entity, StaticShader shader){
			EntityRenderer.renderEntity(entity, shader);
	}
	
	/**
	 * render an entity with a StaticShader
	 * @param model RawModel to render
	 */
	public void render(CompoundEntity entity, StaticShader shader){
		EntityRenderer.renderEntity(entity, shader);
	}

}
