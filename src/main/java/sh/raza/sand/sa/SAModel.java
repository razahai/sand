package sh.raza.sand.sa;

import sh.raza.sand.sa.rw.Atomic;
import sh.raza.sand.sa.rw.Frame;
import sh.raza.sand.sa.rw.geom.Geometry;

public class SAModel {
	private Frame[] frameList;
	private Geometry[] geomList;
	private Atomic[] atomList;
	
	public Frame[] getFrameList() {
		return frameList;
	}
	
	public Geometry[] getGeometryList() {
		return geomList;
	}
	
	public Atomic[] getAtomicList() {
		return atomList;
	}
	
	public void setFrameList(Frame[] frames) {
		frameList = frames;
	}
	
	public void setGeometryList(Geometry[] geoms) {
		geomList = geoms;
	}
	
	public void setAtomicList(Atomic[] atoms) { 
		atomList = atoms;
	}
}