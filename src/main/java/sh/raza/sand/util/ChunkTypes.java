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
	public static final int BINMESH = 0x50E;
	public static final int SKIN = 0x116;
	public static final int RIGHT_TO_RENDER = 0x1F;
	public static final int MATERIAL_EFFECTS = 0x120;
	// R* custom sections
	public static final int FRAME = 0x253F2FE;
	public static final int BREAKABLE = 0x253F2FD;
	public static final int REFLECTION_MATERIAL = 0x253F2FC;
	public static final int SPECULAR_MATERIAL = 0x253F2F6;

	// txd
	public static final int TEXTURE_DICTIONARY = 0x16;
	public static final int RASTER = 0x15;
	
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
		types.put("BINMESH", BINMESH);
		types.put("SKIN", SKIN);
		types.put("BREAKABLE", BREAKABLE);
		types.put("RIGHT_TO_RENDER", RIGHT_TO_RENDER);
		types.put("MATERIAL_EFFECTS", MATERIAL_EFFECTS);
		types.put("REFLECTION_MATERIAL", REFLECTION_MATERIAL);
		types.put("SPECULAR_MATERIAL", SPECULAR_MATERIAL);
		types.put("TEXTURE_DICTIONARY", TEXTURE_DICTIONARY);
		types.put("RASTER", RASTER);
		
		return types;
		
	}
}
