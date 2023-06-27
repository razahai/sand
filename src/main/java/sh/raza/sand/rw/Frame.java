package sh.raza.sand.rw;

public class Frame {
	private float[][] matrix;
	private float[] position;
	private int frameIndex;
	private int flags;
	
	public Frame() {
		matrix = new float[3][3];
		position = new float[3];
		frameIndex = -2;
		flags = -1;
	}
	
	public float[][] getMatrix() {
		return matrix;
	}
	
	public float[] getPosition() {
		return position;
	}
	
	public int getFrameIndex() {
		return frameIndex;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public void addVector(int idx, float x, float y, float z) {
		matrix[idx][0] = x;
		matrix[idx][1] = y;
		matrix[idx][2] = z;
	}
	
	public void setPosition(float x, float y, float z) { 
		position[0] = x;
		position[1] = y;
		position[2] = z;
	}
	
	public void setFlags(int flag) {
		flags = flag;
	}
	
	public void setFrameIndex(int idx) {
		frameIndex = idx;
	}
}
