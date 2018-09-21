package Models;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * an object representing a rectangle without loading one to the gpu
 * @author Jelle Schukken
 *
 */
public class Rect extends CompoundShape{


	Vector3f color;
	

	public Rect(Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale, Vector3f color) {
		super(null, null, position, rotX, rotY, rotZ, scale);
		Quad[] quads = new Quad[6];
		this.color = new Vector3f(.5f,1f,.5f);
		Quad quad = new Quad(new Vector3f(0, 0, .5f), 0, 0, 0, new Vector3f(1, 1, 1), color);
		quads[0] = quad;
		quad = new Quad(new Vector3f(0, 0, -.5f), 0, (float)Math.PI, 0, new Vector3f(1, 1, 1), color);
		quads[1] = quad;
		quad = new Quad(new Vector3f(-.5f, 0, 0), 0, (float)Math.toRadians(-90), 0, new Vector3f(1, 1, 1), color);
		quads[2] = quad;
		quad = new Quad(new Vector3f(0.5f, 0, 0), 0, (float)Math.toRadians(90), 0, new Vector3f(1, 1, 1), color);
		quads[3] = quad;
		quad = new Quad(new Vector3f(0, 0.5f, 0), (float)Math.toRadians(-90), 0, 0, new Vector3f(1, 1, 1), color);
		quads[4] = quad;
		quad = new Quad(new Vector3f(0, -0.5f, 0), (float)Math.toRadians(90), 0, 0, new Vector3f(1, 1, 1), color);
		quads[5] = quad;
		
		setQuads(quads);
		
		CompoundShape[] empty = new CompoundShape[0];
		setCompoundComponents(empty);

	}

	@Override
	public Matrix4f getTransformationMatrix(){
		return ToolBox.MatrixMath.createTransformationMatrix(getCenter(), getRotX(), getRotY(), getRotZ(), getScale());
	}
	
	/**
	 * tries to combine this rectangle with a rectangle in front of it
	 * @param rect the rectangle to combine
	 * @return true if a combination happened
	 */
	public boolean addRectFront(Rect rect) {
		if (rect != null && color.equals(rect.getColor())) {
			if (getPosition().getX() == rect.getPosition().getX() && getPosition().getY() == rect.getPosition().getY()
					&& getScale().getX() == rect.getScale().getX() && getScale().getY() == rect.getScale().getY()) {
				getScale().setZ(getScale().getZ() + rect.getScale().getZ());
				return true;
			}
		}
		return false;
	}

	/**
	 * tries to combine this rectangle with a rectangle to the left of it
	 * @param rect the rectangle to combine
	 * @return true if a combination happened
	 */
	public boolean addRectLeft(Rect rect) {
		if (rect != null && color.equals(rect.getColor())) {
			if (getPosition().getZ() == rect.getPosition().getZ() && getPosition().getY() == rect.getPosition().getY()
					&& getScale().getZ() == rect.getScale().getZ() && getScale().getY() == rect.getScale().getY()) {
				getScale().setX(getScale().getX() + rect.getScale().getX());
				return true;
			}
		}
		return false;
	}

	/**
	 * tries to combine this rectangle with a rectangle below of it
	 * @param rect the rectangle to combine
	 * @return true if a combination happened
	 */
	public boolean addRectBelow(Rect rect) {
		if (rect != null && color.equals(rect.getColor())) {
			if (getPosition().getX() == rect.getPosition().getX() && getPosition().getZ() == rect.getPosition().getZ()
					&& getScale().getX() == rect.getScale().getX() && getScale().getZ() == rect.getScale().getZ()) {
				getScale().setY(getScale().getY() + rect.getScale().getY());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * returns the center of the rectangle
	 * @return the center of the rectangle
	 */
	public Vector3f getCenter(){
		return new Vector3f(getPosition().getX() + getScale().getX()/2f, getPosition().getY() - getScale().getY()/2f + 1, getPosition().getZ() + getScale().getZ()/2f);
	}
	
	public Vector3f getColor(){
		return color;
	}
}
