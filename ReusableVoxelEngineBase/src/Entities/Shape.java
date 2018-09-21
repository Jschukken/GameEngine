package Entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Map.Chunk;
import Map.MapManager;
import Models.RawModel;

/**
 * represents a shape in the world does not load anything to the gpu
 * 
 * @author Jelle Schukken
 *
 */
public abstract class Shape {

	Vector3f position;
	float rotX, rotY, rotZ;
	Vector3f scale;

	public Shape(Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super();
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public void increasePosition(float dx, float dy, float dz) {

		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;

	}

	public void increaseRotation(float dx, float dy, float dz) {

		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;

	}

	public void increaseScale(float dx, float dy, float dz) {

		this.scale.x += dx;
		this.scale.y += dy;
		this.scale.z += dz;

	}

	/**
	 * determines if this single entity would collide with a voxel if the given
	 * movement is applied
	 * 
	 * @return a suggested movement similar to the given movement that does not collide
	 *         with a voxel
	 */
	public Vector3f detectVoxelCollision(Vector3f movement) {
		movement = new Vector3f(((int) (movement.x * 1000)) / 1000f, ((int) (movement.y * 1000)) / 1000f,
				((int) (movement.z * 1000)) / 1000f);
		Vector3f newPosition = new Vector3f();
		Vector3f.add(this.getPosition(), movement, newPosition);
		int[] chunkPosition = MapManager.getChunkPosition(newPosition);
		int[][][] chunkMap = MapManager.getChunkMap(newPosition);
		try {
			if (chunkMap[(int) (newPosition.getX()
					- chunkPosition[0] * Chunk.CHUNK_LENGTH)][(int) (newPosition.y)][(int) (newPosition.z
							- chunkPosition[1] * Chunk.CHUNK_WIDTH)] != 0) {
				float dx = this.getPosition().getX();
				float dy = this.getPosition().getY();
				float dz = this.getPosition().getZ();
				for (float x = newPosition.x; Math.abs(x - this.getPosition().x) > .015; x -= 0.01f
						* Math.signum(movement.x)) {
					if ((int) Math.floor(x - chunkPosition[0] * Chunk.CHUNK_LENGTH) < 0
							|| (int) Math.floor(x - chunkPosition[0] * Chunk.CHUNK_LENGTH) >= Chunk.CHUNK_LENGTH
							|| (int) Math.floor(dz - chunkPosition[1] * Chunk.CHUNK_WIDTH) < 0
							|| (int) Math.floor(dz - chunkPosition[1] * Chunk.CHUNK_WIDTH) >= Chunk.CHUNK_WIDTH) {
						chunkPosition = MapManager
								.getChunkPosition(new Vector3f(x, dy, dz));
						chunkMap = MapManager.getChunkMap(new Vector3f(x, dy, dz));
					}

					if (dy >= Chunk.CHUNK_HEIGHT || dy < 0 || chunkMap[(int) (x - chunkPosition[0] * Chunk.CHUNK_LENGTH)][(int) (dy)][(int) (dz
							- chunkPosition[1] * Chunk.CHUNK_WIDTH)] == 0) {

						dx = x;
						break;

					}
				}

				for (float z = newPosition.z; Math.abs(z - this.getPosition().z) > .015; z -= 0.01f
						* Math.signum(movement.z)) {
					if ((int)Math.floor(dx - chunkPosition[0] * Chunk.CHUNK_LENGTH) < 0
							|| (int) Math.floor(dx - chunkPosition[0] * Chunk.CHUNK_LENGTH) >= Chunk.CHUNK_LENGTH
							|| (int) Math.floor(z - chunkPosition[1] * Chunk.CHUNK_WIDTH) < 0
							|| (int) Math.floor(z - chunkPosition[1] * Chunk.CHUNK_WIDTH) >= Chunk.CHUNK_WIDTH) {
						chunkPosition = MapManager
								.getChunkPosition(new Vector3f(dx, dy, z));
						chunkMap = MapManager.getChunkMap(new Vector3f(dx, dy, z));
					}

					if (dy >= Chunk.CHUNK_HEIGHT || dy < 0 || chunkMap[(int) (dx - chunkPosition[0] * Chunk.CHUNK_LENGTH)][(int) (dy)][(int) (z
							- chunkPosition[1] * Chunk.CHUNK_WIDTH)] == 0) {

						dz = z;
						break;

					}
				}

				if ((int) Math.floor(dx - chunkPosition[0] * Chunk.CHUNK_LENGTH) < 0
						|| (int) Math.floor(dx - chunkPosition[0] * Chunk.CHUNK_LENGTH) >= Chunk.CHUNK_LENGTH
						|| (int) Math.floor(dz - chunkPosition[1] * Chunk.CHUNK_WIDTH) < 0
						|| (int) Math.floor(dz - chunkPosition[1] * Chunk.CHUNK_WIDTH) >= Chunk.CHUNK_WIDTH) {
					chunkPosition = MapManager.getChunkPosition(new Vector3f(dx, dy, dz));
					chunkMap = MapManager.getChunkMap(new Vector3f(dx, dy, dz));
				}
				
				for (float y = newPosition.y; Math.abs(y - this.getPosition().y) > .0015; y -= 0.001f
						* Math.signum(newPosition.y - this.getPosition().y)) {

					if (y >= 0 && y < Chunk.CHUNK_HEIGHT
							&& chunkMap[(int) (dx - chunkPosition[0] * Chunk.CHUNK_LENGTH)][(int) (y)][(int) (dz
									- chunkPosition[1] * Chunk.CHUNK_WIDTH)] == 0) {

						dy = y;
						break;

					}
				}

				return new Vector3f(dx - this.getPosition().x, dy - this.getPosition().y, dz - this.getPosition().z);

			} else {
				return movement;
			}

		} catch (NullPointerException e) {
			return movement;
		} catch (ArrayIndexOutOfBoundsException e) {
			return movement;
		}
	}

	// _____________GETTERS AND SETTERS_________________

	public Matrix4f getTransformationMatrix() {
		return ToolBox.MatrixMath.createTransformationMatrix(position, rotX, rotY, rotZ, scale);
	}

	public Vector3f getPosition() {
		return position;
//		return new Vector3f(((int) (position.x * 1000)) / 1000f, ((int) (position.y * 1000)) / 1000f,
//				((int) (position.z * 1000)) / 1000f);
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotx) {
		this.rotX = rotx;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(float x, float y, float z) {

		scale = new Vector3f(x, y, z);

	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

}
