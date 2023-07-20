package sh.raza.sand.sa.rw.extension.plg;

import sh.raza.sand.sa.rw.extension.Extension;

public class Skin extends Extension {
	private int numBones;
	private int numUsedBones;
	private int maxVertexWeights;
	
	private int[] usedBones;
	private int[][] vertexBoneIndices;
	private float[][] vertexBoneWeights;
	
	private float[][][] boneTransformations;
	
	private int boneLimit;
	private int numGroups;
	private int numRemaps;
	
	private int[] boneRemapIndices;
	private int[][] boneGroup;
	private int[][] boneRemap;
	
	public Skin(int bones, int used, int maxVW, int[] usedB, int[][] vbi, float[][] vbw, float[][][] bt, int bl, int ng, int nr) {
		numBones = bones;
		numUsedBones = used;
		maxVertexWeights = maxVW;
		usedBones = usedB;
		vertexBoneIndices = vbi;
		vertexBoneWeights = vbw;
		boneTransformations = bt;
		boneLimit = bl;
		numGroups = ng;
		numRemaps = nr;
	}
	
	public Skin(int bones, int used, int maxVW, int[] usedB, int[][] vbi, float[][] vbw, float[][][] bt, int bl, int ng, int nr, int[] bri, int[][] bg, int[][] br) {
		numBones = bones;
		numUsedBones = used;
		maxVertexWeights = maxVW;
		usedBones = usedB;
		vertexBoneIndices = vbi;
		vertexBoneWeights = vbw;
		boneTransformations = bt;
		boneLimit = bl;
		numGroups = ng;
		numRemaps = nr;
		boneRemapIndices = bri;
		boneGroup = bg;
		boneRemap = br;
	}
	
	public int getNumBones() {
		return numBones;
	}
	
	public int getNumUsedBones() {
		return numUsedBones;
	}
	
	public int getMaxVertexWeights() {
		return maxVertexWeights;
	}
	
	public int[] getUsedBones() {
		return usedBones;
	}
	
	public int[][] getVertexBoneIndices() {
		return vertexBoneIndices;
	}
	
	public float[][] getVertexBoneWeights() {
		return vertexBoneWeights;
	}
	
	public float[][][] getBoneTransformations() {
		return boneTransformations;
	}
	
	public float[][] getBoneTransformationsAs2d() {
		float[][] transforms = new float[numBones][16];
		
		for (int i = 0; i < numBones; i++) {
			float[] trans = new float[16];
			int idx = 0; 
			
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					trans[idx] = boneTransformations[i][j][k];
					idx++;
				}
			}
			
			transforms[i] = trans;
		}
		
		return transforms;
	}
	
	public int getBoneLimit() {
		return boneLimit;
	}
	
	public int getNumGroups() {
		return numGroups;
	}
	
	public int getNumRemaps() {
		return numRemaps;
	}
	
	public int[] getBoneRemapIndices() {
		return boneRemapIndices;
	}
	
	public int[][] getBoneGroups() {
		return boneGroup;
	}
	
	public int[][] getBoneRemaps() {
		return boneRemap;
	}
	
	public String getExtensionType() {
		return "SKIN";
	}
}
