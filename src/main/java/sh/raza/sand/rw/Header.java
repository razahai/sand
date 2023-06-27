package sh.raza.sand.rw;

import java.util.Map;

import sh.raza.sand.util.ChunkTypes;

public class Header {
	private int type;
	private int length;
	private int libraryID;
	
	public Header(int t, int len, int libID) {
		type = t;
		length = len;
		libraryID = libID;
	}
	
	// based on:
	// https://gtamods.com/wiki/RenderWare -- Versioning
	public int getVersion() {
		if ((libraryID & 0xFFFF0000) != 0)
			return (libraryID >> 14 & 0x3FF00) + 0x30000 | (libraryID >> 16 & 0x3F);
		return libraryID << 8;
	}
	
	public int getType() {
		return type;
	}
	
	public String getChunkName() {
		Map<String, Integer> types = ChunkTypes.getTypes();
		
		for (String name : types.keySet()) {
			if (type == types.get(name)) 
				return name;
		}
		
		return "UNKNOWN";
	}
	
	public int length() {
		return length;
	}
}