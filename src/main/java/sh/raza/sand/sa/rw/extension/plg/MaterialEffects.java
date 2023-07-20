package sh.raza.sand.sa.rw.extension.plg;

import sh.raza.sand.sa.rw.extension.Extension;

public class MaterialEffects extends Extension {
	private int matfx;
	private Effect first;
	private Effect second;
	
	public MaterialEffects(int matfxEnabled) {
		matfx = matfxEnabled;
	}
	
	public MaterialEffects(Effect f) {
		first = f;
	}
	
	public MaterialEffects(Effect f, Effect s) {
		first = f;
		second = s;
	}
	
	public Effect[] getEffects() {
		return new Effect[] {first, second};
	}
	
	public Effect getFirstEffect() {
		return first;
	}
	
	public Effect getSecondEffect() {
		return second;
	}
	
	public boolean isMatFXEnabled() {
		return (matfx != 0 ? true : false);
	}
	
	public String getExtensionType() {
		return "MATERIAL_EFFECTS";
	}
}
