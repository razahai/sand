package sh.raza.sand.sa.rw;

import java.util.ArrayList;
import java.util.List;

import sh.raza.sand.sa.rw.extension.Extension;

public class ExtChunk {
	private List<Extension> exts = new ArrayList<Extension>();
	
	public List<Extension> getExtensions() {
		return exts;
	}
	
	public Extension getExtension(String name) {
		for (Extension ext : exts) {
			if (ext.getExtensionType().equals(name)) 
				return ext;
		}
		
		return null;
	}
	
	public void addExtension(Extension e) {
		exts.add(e);
	}
}
