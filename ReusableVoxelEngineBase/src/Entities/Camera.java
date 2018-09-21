package Entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera extends Shape{

	public static final boolean DEBUG_MODE = true;

	float speed = 0.5f;

	public Camera(Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(position, rotX, rotY, rotZ, scale);
	}

	public void move() {

		if (DEBUG_MODE) {

			if (Mouse.isGrabbed()) {
				rotX += -Mouse.getDY() * speed;
				rotY += Mouse.getDX() * speed;
			}

			float dx = 0;
			float dy = 0;
			float dz = 0;

			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				dx += (float) (speed * Math.sin(Math.toRadians(rotY)));
				dy += (float) (-speed * Math.sin(Math.toRadians(rotX)));
				dz += (float) (-speed * Math.cos(Math.toRadians(rotY)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				dx -= (float) (speed * Math.sin(Math.toRadians(rotY)));
				dy -= (float) (-speed * Math.sin(Math.toRadians(rotX)));
				dz -= (float) (-speed * Math.cos(Math.toRadians(rotY)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				dx += (float) (speed * Math.sin(Math.toRadians(rotY - 90)));
				dz += (float) (-speed * Math.cos(Math.toRadians(rotY - 90)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				dx += (float) (speed * Math.sin(Math.toRadians(rotY + 90)));
				dz += (float) (-speed * Math.cos(Math.toRadians(rotY + 90)));
			}

			float total = Math.abs(dx) + Math.abs(dz);

			if (total > 0) {
				dx = (dx * speed) / total;
				dz = (dz * speed) / total;
			}
			
			Vector3f movement = this.detectVoxelCollision(new Vector3f(dx, dy, dz));

			position.x += movement.x;
			position.y += movement.y;
			position.z += movement.z;

		} else {
			// camera moves with player and from mouse
		}

	}

}
