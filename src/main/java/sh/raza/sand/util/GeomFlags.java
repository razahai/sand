package sh.raza.sand.util;

import java.util.HashMap;
import java.util.Map;

// basically a replica of ChunkTypes
public class GeomFlags {
	public static Map<String, Integer> flags = new HashMap<String, Integer>();
	public static final int TRISTRIP = 0x00000001;
	public static final int POSITIONS = 0x00000002;
	public static final int TEXTURED = 0x00000004;
	public static final int PRELIT = 0x00000008;
	public static final int NORMALS = 0x00000010;
	public static final int LIGHT = 0x00000020;
	public static final int MODULATE_MATERIAL_COLOR = 0x00000040;
	public static final int TEXTURED2 = 0x00000080;
	public static final int NATIVE = 0x01000000;

	public static Map<String, Integer> getFlags() {
		if (!flags.isEmpty()) 
			return flags;
		
		flags.put("TRISTRIP", TRISTRIP);
		flags.put("POSITIONS", POSITIONS);
		flags.put("TEXTURED", TEXTURED);
		flags.put("PRELIT", PRELIT);
		flags.put("NORMAL", NORMALS);
		flags.put("LIGHT", LIGHT);
		flags.put("MODULATE_MATERIAL_COLOR", MODULATE_MATERIAL_COLOR);
		flags.put("TEXTURED2", TEXTURED2);
		flags.put("NATIVE", NATIVE);
		
		return flags;
		
	}
}
