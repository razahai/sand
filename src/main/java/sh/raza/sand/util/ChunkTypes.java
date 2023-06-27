package sh.raza.sand.util;

import java.util.Map;
import java.util.HashMap;

public final class ChunkTypes {	
	public static Map<String, Integer> types = new HashMap<String, Integer>();
	public static final int CLUMP = 0x10;
	public static final int STRUCT = 0x1;
	public static final int FRAME_LIST = 0xE;
	public static final int EXTENSION = 0x3;
	public static final int GEOMETRY_LIST = 0x1A;
	public static final int GEOMETRY = 0xF;
	public static final int MATERIAL_LIST = 0x8;
	public static final int MATERIAL = 0x7;
	public static final int TEXTURE = 0x6;
	public static final int ATOMIC = 0x14;
	public static final int STRING = 0x2;
	public static final int HANIM = 0x11E;
	// R* custom sections/
	public static final int FRAME = 0x253F2FE;
	
	// sub-optimal but i couldn't be bothered
	// to think of a better way of doing this
	public static Map<String, Integer> getTypes() {
		if (!types.isEmpty()) 
			return types;
		
		types.put("CLUMP", CLUMP);
		types.put("STRUCT", STRUCT);
		types.put("FRAME_LIST", FRAME_LIST);
		types.put("EXTENSION", EXTENSION);
		types.put("GEOMETRY_LIST", GEOMETRY_LIST);
		types.put("GEOMETRY", GEOMETRY);
		types.put("MATERIAL_LIST", MATERIAL_LIST);
		types.put("MATERIAL", MATERIAL);
		types.put("TEXTURE", TEXTURE);
		types.put("ATOMIC", ATOMIC);
		types.put("STRING", STRING);
		types.put("HANIM", HANIM);
		types.put("FRAME", FRAME);
		
		return types;
		
	}
}
