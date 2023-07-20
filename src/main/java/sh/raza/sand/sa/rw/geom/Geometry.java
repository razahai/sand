package sh.raza.sand.sa.rw.geom;

import sh.raza.sand.sa.rw.ExtChunk;

public class Geometry extends ExtChunk {
	private int numTriangles;
	private int numVertices;
	private int numMorphTargets;
	
	private float ambient;
	private float specular;
	private float diffuse;
	
	private int[][] prelit;
	private float[][][] texCoords;
	private int[][] triangles;
	private MorphTarget[] morphTargets;

	private RpMaterial[] mats;
	
	public Geometry(int tri, int vert, int morphs) {
		numTriangles = tri;
		numVertices = vert;
		numMorphTargets = morphs;
		ambient = 0.0f;
		specular = 0.0f;
		diffuse = 0.0f;
		prelit = new int[vert][4];
	}
	
	public Geometry(int tri, int vert, int morphs, float amb, float spec, float diff) {
		numTriangles = tri;
		numVertices = vert;
		numMorphTargets = morphs;
		ambient = amb;
		specular = spec;
		diffuse = diff;
		prelit = new int[vert][4];
	}
	
	public int getNumTriangles() {
		return numTriangles;
	}
	
	public int getNumVertices() {
		return numVertices;
	}
	
	public int getNumMorphTargets() {
		return numMorphTargets;
	}
	
	public int[][] getPrelitColor() {
		return prelit;
	}
	
	public float[][][] getTextureCoords() {
		return texCoords;
	}
	public int[][] getTriangles() {
		return triangles;
	}
	
	public MorphTarget[] getMorphTargets() {
		return morphTargets;
	}
	
	public RpMaterial[] getMaterialList() {
		return mats;
	}
	
	public float[] getSurfaceProps() {
		return new float[] {ambient, specular, diffuse};
	}

	public void setPrelitColor(int[][] color) {
		prelit = color;
	}
	
	public void setTextureCoords(float[][][] tc) {
		texCoords = tc;
	}
	
	public void setTriangles(int[][] tri) {
		triangles = tri;
	}
	
	public void setMorphTargets(MorphTarget[] mt) {
		morphTargets = mt;
	}
	
	public void setMaterialList(RpMaterial[] matList) {
		mats = matList;
	}
}
