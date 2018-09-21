package Shaders;

import org.lwjgl.util.vector.Matrix4f;

import Entities.Camera;
import Map.Light;
import Map.Sun;

/**
 * a basic shader
 * @author Jelle Schukken
 *
 */
public class StaticShader extends ShaderProgram{

	private static final String vertexFile = "/Shaders/vertexShader.txt";
	private static final String fragmentFile = "/Shaders/fragmentShader.txt";
	
	int pointer_transformationMatrix;
	int pointer_viewMatrix;
	int pointer_projectionMatrix;
	int pointer_cameraPosition;
	
	int pointer_sunAmbient;
	int pointer_sunDiffuse;
	int pointer_sunSpecular;
	int pointer_sunDirection;
	
	public StaticShader() {
		super(vertexFile, fragmentFile);
	}

	@Override
	protected void bindAttributes() {
		
		super.bindAttribute("position", 0);
		super.bindAttribute("normal", 1);
		super.bindAttribute("vcolor", 2);
		
		
	}

	@Override
	protected void getAllUniformLocations() {
		
		pointer_transformationMatrix = super.getUniformLocation("transformationMatrix");
		pointer_projectionMatrix = super.getUniformLocation("projectionMatrix");
		pointer_viewMatrix = super.getUniformLocation("viewMatrix");
		pointer_cameraPosition = super.getUniformLocation("cameraPosition");
		
		pointer_sunAmbient = super.getUniformLocation("sunAmbient");
		pointer_sunDiffuse = super.getUniformLocation("sunDiffuse");
		pointer_sunSpecular = super.getUniformLocation("sunSpecular");
		pointer_sunDirection = super.getUniformLocation("sunDirection");
	}
	
	@Override
	public void loadProjectionMatrix(Matrix4f matrix) {
		
		super.loadMatrix(pointer_projectionMatrix, matrix);
		
	}
	
	@Override
	public void loadViewMatrix(Camera camera) {

		if(Camera.DEBUG_MODE){
			super.loadMatrix(pointer_viewMatrix, ToolBox.MatrixMath.createViewMatrix(camera));
			super.load3DVector(pointer_cameraPosition, camera.getPosition());
		}else{
			
		}
		
	}
	
	public void loadLights(Light[] lights){
		for(int i = 0; i < lights.length && i < 10; i++){
			String uniformLocation = "lights[" + i + "]";
			super.load3DVector(super.getUniformLocation(uniformLocation + ".diffuse"), lights[i].getDiffuse());
			super.load3DVector(super.getUniformLocation(uniformLocation + ".specular"), lights[i].getSpecular());
			super.load3DVector(super.getUniformLocation(uniformLocation + ".lightPosition"), lights[i].getPosition());
		}
	}
	
	public void loadSun(Sun sun){
		super.load3DVector(pointer_sunAmbient, sun.getAmbient());
		super.load3DVector(pointer_sunDiffuse, sun.getDiffuse());
		super.load3DVector(pointer_sunSpecular, sun.getSpecular());
		super.load3DVector(pointer_sunDirection, sun.getDirection());
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		
		super.loadMatrix(pointer_transformationMatrix, matrix);
		
	}
	
}
