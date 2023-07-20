package sh.raza.sand.sa.rw.geom;

import sh.raza.sand.sa.rw.ExtChunk;

public class RpMaterial extends ExtChunk {
	private int flags;
	private int[] color;
	private float ambient;
	private float specular;
	private float diffuse;
	private Texture tex;
	
	public RpMaterial(int flag, int[] col) {
		flags = flag;
		color = col;
		ambient = 0.0f;
		specular = 0.0f;
		diffuse = 0.0f;
	}
	
	public RpMaterial(int flag, int[] col, float amb, float spec, float diff) {
		flags = flag;
		color = col;
		ambient = amb;
		specular = spec;
		diffuse = diff;
	}
	
	public void setTexture(Texture t) {
		tex = t;
	}
	
	public Texture getTexture() {
		return tex;
	}
	
	public int[] getColor() {
		return color;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public float[] getSurfaceProps() {
		return new float[] {ambient, specular, diffuse};
	}
}
