package Models;

import java.util.ArrayList;

/**
 * basic data object, stores data that describes a raw model
 * @author Jelle Schukken
 *
 */
public class RawModel {
	int vaoID;
	int indexCount;
	ArrayList<Integer> vboIDs;
	
	public RawModel(int vaoID, ArrayList<Integer> vboIDs, int indexCount){
		
		this.vaoID = vaoID;
		this.vboIDs = vboIDs;
		this.indexCount = indexCount;
		
	}
	
	public ArrayList<Integer> getVboIDs(){
		return vboIDs;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getIndexCount() {
		return indexCount;
	}

}
