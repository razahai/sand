package sh.raza.sand.sa.rw.extension.plg;

import java.util.HashMap;
import java.util.Map;

import sh.raza.sand.sa.rw.extension.Extension;

public class BinMesh extends Extension {
	private int flags;
	private int totalIndices;
	private Map<Integer, int[]> meshes;
	
	public BinMesh(int flag, int indices) {
		flags = flag;
		totalIndices = indices;
		meshes = new HashMap<Integer, int[]>();
	}
	
	public String getFlagType() {
		return (flags == 0 ? "TRILIST" : "TRISTRIP");
	}
	
	public int getTotalIndices() {
		return totalIndices;
	}
	
	public Map<Integer, int[]> getMeshes() {
		return meshes;
	}
	
	public void addMesh(int matIndex) {
		meshes.put(matIndex, null);
	}
	
	public void addMesh(int matIndex, int[] indices) {
		meshes.put(matIndex, indices);
	}
	
	public String getExtensionType() {
		return "BINMESH";
	}
}
