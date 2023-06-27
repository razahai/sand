package sh.raza.sand.rw.geom;

public class RpMaterial {
	private int flags;
	private int[] color;
	private float ambient;
	private float specular;
	private float diffuse;
	private int texFlags;
	private String texName;
	private String texLayer;
	
	public RpMaterial(int flag, int[] col) {
		flags = flag;
		color = col;
		ambient = 0.0f;
		specular = 0.0f;
		diffuse = 0.0f;
		texFlags = 0;
		texName = "";
		texLayer = "";
	}
	
	public RpMaterial(int flag, int[] col, float amb, float spec, float diff) {
		flags = flag;
		color = col;
		ambient = amb;
		specular = spec;
		diffuse = diff;
		texFlags = 0;
		texName = "";
		texLayer = "";
	}
	
	public void setTexture(int flags, String tex, String mask) {
		texFlags = flags;
		texName = tex;
		texLayer = mask;
	}
	
	public String getTexture() {
		return texFlags + "/" + texName + "/" + texLayer;
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
