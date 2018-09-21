package RenderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import Entities.CompoundEntity;
import Entities.SingleEntity;
import MainGame.MainGameLoop;
import Map.LightManager;
import Shaders.StaticShader;
import ToolBox.MatrixMath;

/**
 * renders and entity
 * 
 * @author Jelle Schukken
 *
 */
public class EntityRenderer {

	/**
	 * renders a SingleEntity
	 * 
	 * @param entity
	 *            the entity to render
	 * @param shader
	 *            the shader to use
	 */
	public static void renderEntity(SingleEntity entity, StaticShader shader) {
		shader.start();
		shader.loadViewMatrix(MainGameLoop.camera);
		GL30.glBindVertexArray(entity.getModel().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		Matrix4f transformationMatrix = MatrixMath.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());

		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadSun(LightManager.sun);
		shader.loadLights(LightManager.renderedLights);

		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	/**
	 * renders a compoundEntity
	 * 
	 * @param entity
	 *            the entity to render
	 * @param shader
	 *            the shader to use
	 */
	public static void renderEntity(CompoundEntity entity, StaticShader shader) {

		for (int i = 0; i < entity.getCompoundComponentCount(); i++) {
			renderComponent(entity.getTransformationMatrix(), entity.getCompoundEntity(i), shader);
		}
		for (int i = 0; i < entity.getSingleComponentCount(); i++) {
			renderComponent(entity.getTransformationMatrix(), entity.getSingleEntity(i), shader);
		}

	}

	/**
	 * renders a SingleEntity component of a CompoundEntity
	 * 
	 * @param transformationMatrix
	 *            the position of the component
	 * @param entity
	 *            the entity to render
	 * @param shader
	 *            the shader to use
	 */
	private static void renderComponent(Matrix4f transformationMatrix, SingleEntity entity, StaticShader shader) {
		shader.start();
		shader.loadViewMatrix(MainGameLoop.camera);
		GL30.glBindVertexArray(entity.getModel().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		Matrix4f newTransformationMatrix = new Matrix4f();
		Matrix4f.mul(transformationMatrix, entity.getTransformationMatrix(), newTransformationMatrix);

		shader.loadTransformationMatrix(newTransformationMatrix);

		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	/**
	 * renders a CompoundEntity component of a CompoundEntity
	 * 
	 * @param transformationMatrix
	 *            the location of the entity
	 * @param entity
	 *            the entity
	 * @param shader
	 *            the shader to use
	 */
	private static void renderComponent(Matrix4f transformationMatrix, CompoundEntity entity, StaticShader shader) {

		Matrix4f newTransformationMatrix = new Matrix4f();
		Matrix4f.mul(transformationMatrix, entity.getTransformationMatrix(), newTransformationMatrix);
		for (int i = 0; i < entity.getCompoundComponentCount(); i++) {
			renderComponent(newTransformationMatrix, entity.getCompoundEntity(i), shader);
		}
		for (int i = 0; i < entity.getSingleComponentCount(); i++) {
			renderComponent(newTransformationMatrix, entity.getSingleEntity(i), shader);
		}

	}
}
