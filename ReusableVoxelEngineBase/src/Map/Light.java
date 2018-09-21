package Map;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Vector3f;

/**
 * represents a light in the world
 * @author Jelle Schukken
 *
 */
public class Light {

	Vector3f specular;
	Vector3f ambient;
	Vector3f diffuse;
	Vector3f position;
	
	public Light(){
		specular = new Vector3f(0f,0f,0f);
		ambient = new Vector3f(0,0,0f);
		diffuse = new Vector3f(0f,0f,0f);
		position = new Vector3f(0,0f,0);
	}
	
	public Light(Vector3f ambient, Vector3f diffuse, Vector3f specular, Vector3f position) {
		super();
		this.specular = specular;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.position = position;
	}


	public Vector3f getSpecular() {
		return specular;
	}

	public Vector3f getAmbient() {
		return ambient;
	}

	public Vector3f getDiffuse() {
		return diffuse;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public void setSpecular(Vector3f specular) {
		this.specular = specular;
	}

	public void setAmbient(Vector3f ambient) {
		this.ambient = ambient;
	}

	public void setDiffuse(Vector3f diffuse) {
		this.diffuse = diffuse;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

}
