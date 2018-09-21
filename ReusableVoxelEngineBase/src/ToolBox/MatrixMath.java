package ToolBox;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;

/**
 * helper class for math functions
 * 
 * @author Jelle Schukken
 *
 */
public class MatrixMath {

	/**
	 * creates a transformation matrix for input transformation scales then
	 * rotates then translates
	 * 
	 * @param translation
	 *            vec3 representing translation
	 * @param rotX
	 *            amount rotated over x axis in radians
	 * @param rotY
	 *            amount rotated over y axis in radians
	 * @param rotZ
	 *            amount rotated over z axis in radians
	 * @param scale
	 *            amount to scale
	 * @return the total transformation matrix
	 */
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rotX, float rotY, float rotZ,
			Vector3f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.translate(translation, matrix, matrix);

		Matrix4f.rotate(rotX, new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate(rotY, new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(rotZ, new Vector3f(0, 0, 1), matrix, matrix);

		Matrix4f.scale(scale, matrix, matrix);

		return matrix;

	}

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createViewMatrix(Vector3f cameraPosition, Vector3f targetPosition) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Vector3f up = new Vector3f(0, 0, 1);

		Vector3f zAxis = new Vector3f();
		Vector3f.sub(targetPosition, cameraPosition, zAxis);
		zAxis.normalise();

		Vector3f xAxis = new Vector3f();
		Vector3f.cross(up, zAxis, xAxis);
		xAxis.normalise();

		Vector3f yAxis = new Vector3f();
		Vector3f.cross(zAxis, xAxis, yAxis);

		float[] load = {

				xAxis.x, yAxis.x, zAxis.x, 0, xAxis.y, yAxis.y, zAxis.y, 0, xAxis.z, yAxis.z, zAxis.z, 0,

				-Vector3f.dot(xAxis, cameraPosition), -Vector3f.dot(yAxis, cameraPosition),
				-Vector3f.dot(zAxis, cameraPosition), 1

		};

		FloatBuffer buf = BufferUtils.createFloatBuffer(load.length);
		;
		buf.put(load);
		matrix.load(buf);
		return matrix;
	}

	/**
	 * creates a view transformation matrix given camera info
	 * 
	 * @param camera
	 *            the camera for which to create a view matrix
	 * @return the view matrix
	 */
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		
		Matrix4f.translate(new Vector3f(0, 0, 1), matrix, matrix);

		Matrix4f.rotate((float) Math.toRadians(camera.getRotX()), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getRotY()), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getRotZ()), new Vector3f(0, 0, 1), matrix, matrix);

		Matrix4f.translate(new Vector3f(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z),
				matrix, matrix);

		return matrix;

	}

	/**
	 * takes vector of rotations and returns a vector
	 * 
	 * @param vec
	 *            the vector of rotations
	 * @return the direction vector
	 */
	public static Vector3f rotateVector(Vector3f vec) {
		return new Vector3f((float) (Math.cos(Math.toRadians(vec.y + 90)) * Math.cos(Math.toRadians(vec.x))),
				(float) (Math.sin(Math.toRadians(vec.x))),
				(float) (Math.cos(Math.toRadians(vec.y)) * Math.cos(Math.toRadians(vec.x))));
	}

	/**
	 * Creates the normals on an object
	 * 
	 * @param vertices
	 * @param indices
	 * @return
	 */
	public static float[] CreateNormals(float[] vertices, int[] indices) {
		List<Vector3f> v_vertices = new ArrayList<Vector3f>();
		for (int i = 0; i < vertices.length; i += 3)
			v_vertices.add(new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]));

		List<Vector3f> v_normals = new ArrayList<Vector3f>();
		for (int i = 0; i < indices.length; i += 3) {
			Vector3f A = v_vertices.get(indices[i]);
			Vector3f B = v_vertices.get(indices[i + 1]);
			Vector3f C = v_vertices.get(indices[i + 2]);

			v_normals.add(normal(A, B, C));

			v_normals.add(normal(A, C, B));

			v_normals.add(normal(B, A, C));

			v_normals.add(normal(B, C, A));

			v_normals.add(normal(C, A, B));

			v_normals.add(normal(C, B, A));
		}

		float[] normals = new float[v_normals.size() * 3];
		for (int i = 0; i < v_normals.size(); i += 3) {
			normals[i] = v_normals.get(i).x;
			normals[i + 1] = v_normals.get(i).y;
			normals[i + 2] = v_normals.get(i).z;
		}
		return normals;
	}

	/**
	 * Lars please add comments
	 */
	private static Vector3f normal(Vector3f A, Vector3f B, Vector3f C) {
		Vector3f AB = Vector3f.sub(B, A, null);
		Vector3f AC = Vector3f.sub(C, A, null);
		Vector3f normal = Vector3f.cross(AB, AC, null);
		Vector3f inverseNormal = new Vector3f(-1 * normal.x, -1 * normal.y, -1 * normal.z);
		if ((normal.x > 0 && A.x < 0) || (normal.x < 0 && A.x > 0)) {
			return inverseNormal;
		} else if ((normal.y > 0 && A.y < 0) || (normal.y < 0 && A.y > 0)) {
			return inverseNormal;
		} else if ((normal.z > 0 && A.z < 0) || (normal.z < 0 && A.z > 0)) {
			return inverseNormal;
		} else {
			return normal;
		}
	}
}
