package sh.raza.sand.sa.rw.geom;

import sh.raza.sand.sa.rw.ExtChunk;

public class Texture extends ExtChunk {
	// this class name is misleading -- it's
	// actually the texture in dff files, NOT
	// the txd texture -- for txd files, go to
	// the rw/tex folder (also, look at: 
	// loaders/TXDLoader.java for txd loading)
	private int flags;
	private String texName;
	private String layerName;
	
	public Texture(int flag, String name, String mask) {
		flags = flag;
		texName = name;
		layerName = mask;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public String getTextureName() {
		return texName;
	}
	
	public String getLayerName() {
		return layerName;
	}
}
