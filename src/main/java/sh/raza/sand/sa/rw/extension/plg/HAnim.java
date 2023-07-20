package sh.raza.sand.sa.rw.extension.plg;

import sh.raza.sand.sa.rw.extension.Extension;

public class HAnim extends Extension {
	private int version;
	private int id;
	private int flags;
	private int keyframeSize;
	private int[][] bones;
	
	public HAnim(int animVersion, int nodeID, int numNodes) {
		version = animVersion;
		id = nodeID;
		// there are num nodes which have
		// the format:
		// -- UInt32 - nodeId
		// -- UInt32 - nodeIdx
		// -- UInt32 - flags
		bones = new int[numNodes][3];
	}
	
	public String getExtensionType() {
		return "HANIM";
	}
	
	public int getVersion() {
		return version;
	}
	
	public int getID() {
		return id;
	}
	
	public int[][] getBones() {
		return bones;
	}
	
	public int getKfSize() {
		return keyframeSize;
	}
	
	public void setFlags(int flag) {
		flags = flag;
	}
	
	public void setKfSize(int size) {
		keyframeSize = size;
	}
	
	public void addBone(int idx, int id, int boneIdx, int flags) {
		bones[idx][0] = id;
		bones[idx][1] = boneIdx;
		bones[idx][2] = flags;
	}
	
	public String getFlag() {
		switch (flags) {
			case 1:
				return "SUBHIERARCHY";
			case 2:
				return "NOMATRICES";
			case 4096:
				return "UPDATEMODELLINGMATRICES";
			case 8192:
				return "UPDATELTMS";
			case 16384:
				return "LOCALSPACEMATRICES";
			default:
				return "UNKNOWN";
		}
	}
}
