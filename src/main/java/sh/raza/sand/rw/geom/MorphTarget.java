package sh.raza.sand.rw.geom;

public class MorphTarget {
	// RwSphere    boundingSphere    (RwSphere: float32 x, y, z, radius)
	private float[] sphere;
	private float[][] vertices;
	private float[][] normals;
	
	public MorphTarget(float x, float y, float z, float rad, int v) {
		sphere = new float[] {x, y, z, rad};
		vertices = new float[v][3];
		normals = new float[v][3];
	}
	
	public float[] getSphere() {
		return sphere;
	}
	
	public float[][] getVertices() {
		return vertices;
	}
	
	public float[][] getNormals() {
		return normals;
	}
	
	public void addVertices(int idx, float x, float y, float z) {
		vertices[idx][0] = x;
		vertices[idx][1] = y;
		vertices[idx][2] = z;
	}
	
	public void addNormals(int idx, float x, float y, float z) {
		normals[idx][0] = x;
		normals[idx][1] = y;
		normals[idx][2] = z;
	}
}
