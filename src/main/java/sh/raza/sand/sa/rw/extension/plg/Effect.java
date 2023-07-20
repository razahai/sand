package sh.raza.sand.sa.rw.extension.plg;

import sh.raza.sand.sa.rw.geom.Texture;

public class Effect {
	private int type;
	
	// rwMATFXEFFECTBUMPMAP
	private float intensity;
	private Texture bumpMap;
	private Texture heightMap;
	
	// rwMATFXENVMAP
	private float refCoeff;
	private int useAlphaChannel;
	private Texture envMap;
	
	// rwMATFXEFFECTDUAL
	private int srcBlendMode;
	private int destBlendMode;
	private Texture dualTex;
	
	public Effect(int t) {
		type = t;
	}
	
	public Effect(int t, float intens, Texture bump, Texture height) {
		type = t;
		intensity = intens;
		bumpMap = bump;
		heightMap = height;
	}
	
	public Effect(int t, float reflect, int alpha) {
		type = t;
		refCoeff = reflect;
		useAlphaChannel = alpha;
	}
	
	public Effect(int t, float reflect, int alpha, Texture env) {
		type = t;
		refCoeff = reflect;
		useAlphaChannel = alpha;
		envMap = env;
	}
	
	public Effect(int t, int src, int dest) {
		type = t;
		srcBlendMode = src;
		destBlendMode = dest;
	}
	
	public Effect(int t, int src, int dest, Texture tex) {
		type = t;
		srcBlendMode = src;
		destBlendMode = dest;
		dualTex = tex;
	}
	
	public int getType() {
		return type;
	}
	
	public float getIntensity() {
		return intensity;
	}
	
	public Texture[] getBhMaps() {
		return new Texture[] {bumpMap, heightMap};
	}
	
	public float getReflectCoefficient() {
		return refCoeff;
	}
	
	public boolean canUseAlphaChannel() {
		return (useAlphaChannel == 1 ? true : false);
	}
	
	public Texture getEnvironmentMap() {
		return envMap;
	}
	
	public int getSrcBlendMode() {
		return srcBlendMode;
	}
	
	public int getDestBlendMode() {
		return destBlendMode;
	}
	
	public Texture getDualTexture() {
		return dualTex;
	}
}
