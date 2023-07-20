package sh.raza.sand.sa.rw.extension;

public class ReflectionMaterial extends Extension {
	private float sx;
	private float sy;
	private float ox;
	private float oy;
	private float intensity;
	
	public ReflectionMaterial(float envMapSX, float envMapSY, float envMapOX, float envMapOY, float inte) {
		sx = envMapSX;
		sy = envMapSY;
		ox = envMapOX;
		oy = envMapOY;
		intensity = inte;
	}
	
	public float getEnvMapScaleX() {
		return sx;
	}
	
	public float getEnvMapScaleY() {
		return sy;
	}
	
	public float getEnvMapOffsetX() {
		return ox;
	}
	
	public float getEnvMapOffsetY() {
		return oy;
	}
	
	public float getIntensity() {
		return intensity;
	}
	
	public String getExtensionType() {
		return "REFLECTION_MATERIAL";
	}
}
