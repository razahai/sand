package sh.raza.sand.rw;

public class Atomic {
	private int frameIdx;
	private int geomIdx;
	private int flags;
	
	public Atomic(int frame, int geom, int flag) {
		frameIdx = frame;
		geomIdx = geom;
		flags = flag;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public int getFrameIndex() {
		return frameIdx;
	}
	
	public int getGeomIndex() {
		return geomIdx;
	}
}
