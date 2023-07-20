package sh.raza.sand.sa.rw.extension;

public class SpecularMaterial extends Extension {
	private float level;
	private String texName;
	
	public SpecularMaterial(float l, String t) {
		level = l;
		texName = t;
	}
	
	public float getLevel() {
		return level;
	}
	
	public String getTextureName() {
		return texName;
	}
	
	public String getExtensionType() {
		return "SPECULAR_MATERIAL";
	}
}
