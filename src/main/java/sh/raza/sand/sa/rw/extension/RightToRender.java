package sh.raza.sand.sa.rw.extension;

public class RightToRender extends Extension {
	private int identifier;
	private int extra;
	
	public RightToRender(int id, int ex) {
		identifier = id;
		extra = ex;
	}
	
	public int getIdentifier() {
		return identifier;
	}
	
	public int getExtra() { 
		return extra;
	}
	
	public String getExtensionType() {
		return "RIGHT_TO_RENDER";
	}
}
