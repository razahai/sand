package sh.raza.sand.sa.rw;

import java.util.Map;

import sh.raza.sand.util.ChunkTypes;

public class Header {
	private int type;
	private int length;
	private int libID;
	
	public Header(int t, int len, int id) {
		type = t;
		length = len;
		libID = id;
	}
	
	// based on:
	// https://gtamods.com/wiki/RenderWare#Versioning
	public int getVersion() {
		if ((libID & 0xFFFF0000) != 0)
			return (libID >> 14 & 0x3FF00) + 0x30000 | (libID >> 16 & 0x3F);
		return libID << 8;
	}
	
	public int getType() {
		return type;
	}
	
	public int getLibID() {
		return libID;
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