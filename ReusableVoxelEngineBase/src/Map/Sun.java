package Map;

import org.lwjgl.util.vector.Vector3f;

public class Sun extends Light{
	
	private float time = 0;
	private float speed = 0.0001f;
	private Vector3f direction;

	public Sun(){
		super(new Vector3f(0.001f,.001f,0.001f),new Vector3f(.1f,.1f,.1f),new Vector3f(.1f,.1f,.1f), new Vector3f(0,10f,0));
		direction = new Vector3f((float)Math.cos(time), (float)Math.sin(time), 0);
		
	}
	
	/**
	 * returns the direction of the sun.
	 */
	public Vector3f getDirection() {
		return direction;
	}
	
	/**
	 * moves the sun "around" the world
	 */
	public void move(){
		time = (float)(time+speed % (2*Math.PI));
		direction.setX((float)Math.cos(time));
		direction.setY((float)Math.sin(time));
	}
	
	/**
	 * changes diffuse to simulate a sunset
	 */
	@Override
	public Vector3f getDiffuse(){
		float height = direction.getY();
		if(height < .4 && height >= .1){
			return new Vector3f(diffuse.x * (1 + 8*(.4f-height)), diffuse.y * height/.4f, diffuse.z * (float)Math.pow(height/.4f, 2));
		}else if (height < .1 && height >= 0){
			return new Vector3f(diffuse.x * (1 + 6*(.4f-height)) * height/.1f, diffuse.y * height/.4f, diffuse.z * (float)Math.pow(height/.4f, 2));
		}else if (height < 0){
			return new Vector3f(0, 0, 0);
		}
		
		return diffuse;
	}
	
}
