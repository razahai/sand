package sh.raza.sand.sa.rw.extension;

public class NodeName extends Extension {
	private String name;
	
	public NodeName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public String getExtensionType() {
		return "FRAME";
	}
}
