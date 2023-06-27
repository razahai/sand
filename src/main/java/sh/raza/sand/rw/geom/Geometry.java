package sh.raza.sand.rw.geom;

public class Geometry {
	private int triangles;
	private int vertices;
	private int morphTargets;
	private float ambient;
	private float specular;
	private float diffuse;
	
	public Geometry(int tri, int vert, int morphs) {
		triangles = tri;
		vertices = vert;
		morphTargets = morphs;
		ambient = 0.0f;
		specular = 0.0f;
		diffuse = 0.0f;
	}
	
	public Geometry(int tri, int vert, int morphs, float amb, float spec, float diff) {
		triangles = tri;
		vertices = vert;
		morphTargets = morphs;
		ambient = amb;
		specular = spec;
		diffuse = diff;
	}
	
	public int getTriangles() {
		return triangles;
	}
	
	public int getVertices() {
		return vertices;
	}
	
	public int getMorphTargets() {
		return morphTargets;
	}
	
	public float[] getSurfaceProps() {
		return new float[] {ambient, specular, diffuse};
	}
}
