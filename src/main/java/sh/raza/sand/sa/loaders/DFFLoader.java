package sh.raza.sand.sa.loaders;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sh.raza.sand.sa.SAModel;
import sh.raza.sand.sa.rw.Atomic;
import sh.raza.sand.sa.rw.ExtChunk;
import sh.raza.sand.sa.rw.Frame;
import sh.raza.sand.sa.rw.Header;
import sh.raza.sand.sa.rw.RWFile;
import sh.raza.sand.sa.rw.extension.Extension;
import sh.raza.sand.sa.rw.extension.NodeName;
import sh.raza.sand.sa.rw.extension.ReflectionMaterial;
import sh.raza.sand.sa.rw.extension.RightToRender;
import sh.raza.sand.sa.rw.extension.SpecularMaterial;
import sh.raza.sand.sa.rw.extension.plg.BinMesh;
import sh.raza.sand.sa.rw.extension.plg.Effect;
import sh.raza.sand.sa.rw.extension.plg.HAnim;
import sh.raza.sand.sa.rw.extension.plg.MaterialEffects;
import sh.raza.sand.sa.rw.extension.plg.Skin;
import sh.raza.sand.sa.rw.geom.Geometry;
import sh.raza.sand.sa.rw.geom.MorphTarget;
import sh.raza.sand.sa.rw.geom.RpMaterial;
import sh.raza.sand.sa.rw.geom.Texture;
import sh.raza.sand.util.FileData;
import sh.raza.sand.util.GeomFlags;

public class DFFLoader extends RWFile {
	// methodology for reading and loading dff files was
	// inspired by various projects and wikis such as:
	// -- https://github.com/andrewixz/DFFLoader/blob/master/src/Reader.js
	// -- https://github.com/Parik27/DragonFF/blob/master/gtaLib/dff.py
	// -- https://gtamods.com/wiki/RenderWare_binary_stream_file
	private DataInputStream stream;
	private static DFFLoader instance = new DFFLoader();
	
	public static DFFLoader getInstance() {
		return instance;
	}
	
	public void parseModel(String filename, SAModel model) {
		try {
			stream = new DataInputStream(new FileInputStream(filename));

			// TODO: rewrite this -- throwing an IOException makes no sense
			if (!(readHeader(stream).getChunkName().equals("CLUMP"))) throw new IOException();
			int[] clump = readClump();
			
			if (!(readHeader(stream).getChunkName().equals("FRAME_LIST"))) throw new IOException(); 
			Frame[] frames = readFrameList();
			
			if (!(readHeader(stream).getChunkName().equals("GEOMETRY_LIST"))) throw new IOException();
			Geometry[] geoms = readGeometryList();
			
			model.setFrameList(frames);
			model.setGeometryList(geoms);
			
			Atomic[] atoms = new Atomic[clump[0]];
			for (int a = 0; a < clump[0]; a++) {
				readHeader(stream);
				atoms[a] = readAtomic();
			}
			
			model.setAtomicList(atoms);
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.out);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	// Clump's child sections are
	// Struct, Frame List, Geometry List,
	// Atomics, and optionally a number
	// of Structs/Lights and Structs/Cameras
	public int[] readClump() throws IOException {
		// read clump's Struct (0x1):
		// -- int32 - Number of Atomics
		// -- int32 - Number of Lights - only present after version 0x33000
		// -- int32 - Number of Cameras - only present after version 0x33000 but 0 in all GTA files
		Header struct = readHeader(stream);
		int atomics = FileData.readUInt32(stream);
		int lights = 0; 
		int cameras = 0;
		
		// if the chunk consists of 12 bytes (0xC), 
		// then it must contain atomics (4 bytes),
		// lights (4 bytes) and cameras (4 bytes)
		if (struct.length() == 0xC) {
			lights = FileData.readUInt32(stream);
			cameras = FileData.readUInt32(stream);
		}
		
		return new int[] {atomics, lights, cameras};
		
	}
	
	// FrameList has a struct:
	// -- 4b - DWORD - Number of frames
	public Frame[] readFrameList() throws IOException {
		readHeader(stream);
		int numFrames = FileData.readUInt32(stream);
		Frame[] frames = new Frame[numFrames];
		
		for (int i = 0; i < numFrames; i++) {
			// Each frame has the following format:
			// -- 36b - TMatrix - Rotation matrix
			// -- 12b - TVector - Position
			// -- 4b - DWORD - Current frame index
			// -- 4b - DWORD - Matrix creation flags
			// 
			// TMatrix is a set of 3 TVectors whose 
			// format is:
			// -- 4b - FLOAT - coord x
			// -- 4b - FLOAT - coord y
			// -- 4b - FLOAT - coord z
			Frame frame = new Frame();
			
			for (int j = 0; j < 3; j++) {
				float x = FileData.readFloat32(stream);
				float y = FileData.readFloat32(stream);
				float z = FileData.readFloat32(stream);
				
				frame.addVector(j, x, y, z);
			}
			
			float posX = FileData.readFloat32(stream);
			float posY = FileData.readFloat32(stream);
			float posZ = FileData.readFloat32(stream);
			
			frame.setPosition(posX, posY, posZ);
			
			frame.setFrameIndex(FileData.readUInt32(stream)); // + 1? since it starts at -1
			frame.setFlags(FileData.readUInt32(stream));
			
			frames[i] = frame;
		}
		
		for (int f = 0; f < numFrames; f++) {
			Header h = readHeader(stream);
			readExtension(h, frames[f]);
		}
		
		return frames;
	}
	
	// Geometry List has a struct section:
	// -- 4b - DWORD - Number of geometries
	public Geometry[] readGeometryList() throws IOException {
		readHeader(stream);
		int num = FileData.readUInt32(stream);
		Geometry[] geoms = new Geometry[num];
		
		for (int i = 0; i < num; i++) {
			// geom header
			readHeader(stream);
			geoms[i] = readGeometry();
		}
		
		return geoms;
	}
	
	public Geometry readGeometry() throws IOException {
		// Geometry chunk structure is kinda complicated
		// so I won't document it here -- read the 
		// documentation instead:
		// https://gtamods.com/wiki/RpGeometry#Structure
		// --
		// Extensions:
		// Bin Mesh PLG, Native Data PLG (Consoles only), 
		// Skin PLG, Breakable, Extra Vert Colour, Morph PLG,
		// 2d Effect
		Header struct = readHeader(stream);
		Geometry geom;
		
		int format = FileData.readUInt32(stream);
		int numTriangles = FileData.readUInt32(stream);
		int numVertices = FileData.readUInt32(stream);
		int numMorphTargets = FileData.readUInt32(stream);

		if (struct.getVersion() < 0x34000) {
			float ambient = FileData.readFloat32(stream);
			float specular = FileData.readFloat32(stream);
			float diffuse = FileData.readFloat32(stream);
			
			geom = new Geometry(numTriangles, numVertices, numMorphTargets, ambient, specular, diffuse);
		} else {
			geom = new Geometry(numTriangles, numVertices, numMorphTargets);
		}
		
		if ((format & GeomFlags.NATIVE) == 0) {
			if ((format & GeomFlags.PRELIT) != 0) {
				// RwRGBA   prelitcolor[numVertices] (RwRGBA: uint8 r, g, b, a)
				int[][] plColor = new int[numVertices][4];
				
				for (int v = 0; v < numVertices; v++) {
					plColor[v][0] = FileData.readUInt8(stream); // r
					plColor[v][1] = FileData.readUInt8(stream); // g
					plColor[v][2] = FileData.readUInt8(stream); // b
					plColor[v][3] = FileData.readUInt8(stream); // a
				}
				
				geom.setPrelitColor(plColor);
			}
		
			if ((format & (GeomFlags.TEXTURED | GeomFlags.TEXTURED2)) != 0) {
				// RwTexCoords    texCoords[numVertices]  (RwTexCoords: float32 u, v)
				int uvs = (format & 0x00FF0000) >> 16;

				if (uvs == 0) {
					if ((format & GeomFlags.TEXTURED) != 0) 
						uvs = 1;
					else if ((format & GeomFlags.TEXTURED2) != 0)
						uvs = 2;
				}
				
				float[][][] tex = new float[uvs][numVertices][2];
				
				for (int t = 0; t < uvs; t++) {
					for (int v = 0; v < numVertices; v++) {
						tex[t][v][0] = FileData.readFloat32(stream);
						tex[t][v][1] = FileData.readFloat32(stream);
					}
				}
				
				geom.setTextureCoords(tex);
			}
			
			// RpTriangle   triangles[numTriangles]  (RpTriangle: uint16 vertex2, vertex1, materialId, vertex3)
			int[][] tri = new int[numTriangles][4];
			
			for (int r = 0; r < numTriangles; r++) {
				tri[r][0] = FileData.readUInt16(stream); // vertex2
				tri[r][1] = FileData.readUInt16(stream); // vertex1
				tri[r][2] = FileData.readUInt16(stream); // materialId
				tri[r][3] = FileData.readUInt16(stream); // vertex3
			}
			
			geom.setTriangles(tri);
		}
		
		MorphTarget[] mt = new MorphTarget[numMorphTargets];
		
		for (int m = 0; m < numMorphTargets; m++) {
			float x = FileData.readFloat32(stream);
			float y = FileData.readFloat32(stream);
			float z = FileData.readFloat32(stream);
			float rad = FileData.readFloat32(stream);
			
			int hasVert = FileData.readUInt32(stream);
			int hasNorm = FileData.readUInt32(stream);
			
			MorphTarget morph = new MorphTarget(x, y, z, rad, numVertices);
			
			if (hasVert != 0) {
				// RwV3d   vertices[numVertices] (RwV3d: float32 x, y, z)
				for (int v = 0; v < numVertices; v++) {
					float vertX = FileData.readFloat32(stream);
					float vertY = FileData.readFloat32(stream);
					float vertZ = FileData.readFloat32(stream);
					
					morph.addVertices(v, vertX, vertY, vertZ);
				}
			}
			
			if (hasNorm != 0) {
				// RwV3d   normals[numVertices]
				for (int v = 0; v < numVertices; v++) {
					float normX = FileData.readFloat32(stream);
					float normY = FileData.readFloat32(stream);
					float normZ = FileData.readFloat32(stream);
					
					morph.addNormals(v, normX, normY, normZ);
				}
			}
			
			mt[m] = morph;
		}
		
		geom.setMorphTargets(mt);
		
		// mat list
		readHeader(stream);
		RpMaterial[] mats = readMaterialList();
		geom.setMaterialList(mats);
		
		readExtension(readHeader(stream), geom);
		
		return geom;
	}
	
	public RpMaterial[] readMaterialList() throws IOException {
		// Material list structure:
		// 4b - DWORD - number of materials
		// 4b[] - DWORD[] - array of material indices
		readHeader(stream);
		int numMaterials = FileData.readUInt32(stream);
		int[] idxs = new int[numMaterials];
		
		for (int i = 0; i < numMaterials; i++) {
			idxs[i] = FileData.readUInt32(stream);
		}
		
		RpMaterial[] mats = new RpMaterial[numMaterials];
		for (int m = 0; m < numMaterials; m++) {
			// mat header
			readHeader(stream);
			mats[m] = readMaterial();
		}
		
		return mats;
	}
	
	public RpMaterial readMaterial() throws IOException {
		// Material structure is as follows:
		// -- int32 - flags (unused)
		// -- uint8[] - color (r,g,b,a)
		// -- int32 - unused (???)
		// -- bool32 - isTextured
		// -- if version > 0x30400
		// ---- float ambient, specular, diffuse
		// --
		// Extensions:
		// Right To Render, User Data PLG, Material Effects PLG,
		// UV Animation PLG, Reflection Material, Specular Material
		RpMaterial mat;
		
		Header struct = readHeader(stream);
		int flags = FileData.readUInt32(stream);
		// RwRGBA       color    (RwRGBA: uint8 r, g, b, a)
		int[] color = new int[] {
				FileData.readUInt8(stream),
				FileData.readUInt8(stream),
				FileData.readUInt8(stream),
				FileData.readUInt8(stream)
		};
		FileData.readUInt32(stream); // unused
		int textured = FileData.readUInt32(stream);
		
		if (struct.getVersion() > 0x30400) {
			float ambient = FileData.readFloat32(stream);
			float specular = FileData.readFloat32(stream);
			float diffuse = FileData.readFloat32(stream);
			
			mat = new RpMaterial(flags, color, ambient, specular, diffuse);
		} else {
			mat = new RpMaterial(flags, color);
		}
		
		// if it's textured there will be a texture chunk
		if (textured != 0) {
			// texture header
			readHeader(stream);
			Texture tex = readTexture();
			mat.setTexture(tex);
		}
		
		readExtension(readHeader(stream), mat);
		
		return mat;
	}
	
	public Texture readTexture() throws IOException {
		// texture is kinda confusing so just
		// read the documentation
		// https://gtamods.com/wiki/Texture_(RW_Section)
		// --
		// Extensions:
		// Sky Mipmap Val, Anisotropy
		readHeader(stream);
		int flags = FileData.readUInt16(stream);
		// checking for mip not necessary
		FileData.readUInt16(stream);
		String texName = readString();
		String layerName = readString();
		Texture tex = new Texture(flags, texName, layerName);
		
		readExtension(readHeader(stream), tex);
		
		return tex;
	}
	
	public String readString() throws IOException {
		// String includes a trailing zero
		// and is padded with zeros to the
		// next 4b boundary 
		// string header
		Header struct = readHeader(stream);
		String result = FileData.readString(stream, struct.length());
		return result;
	}
	
	public Atomic readAtomic() throws IOException {
		// atomic section contains Struct,
		// Geometry (opt?), and Extensions
		// the struct is as follows:
		// -- uint32 - frameIndex
		// -- uint32 - geomIndex
		// -- uint32 - flags
		// -- uint32 - unused (???)
		// --
		// Extensions:
		// Right To Render, Particles PLG, Pipeline Set,
		// User Data PLG, Material Effects PLG
		Atomic atom;
		
		readHeader(stream);
		int frame = FileData.readUInt32(stream);
		int geom = FileData.readUInt32(stream);
		int flags = FileData.readUInt32(stream);
		FileData.readUInt32(stream); // unused
		
		atom = new Atomic(frame, geom, flags);
		readExtension(readHeader(stream), atom);
		
		return atom;
	}
	
	public void readExtension(Header header, ExtChunk parent) throws IOException {
		int position = 0;
		List<Extension> exts = new ArrayList<Extension>();
		
		while (position < header.length()) {
			Header section = readHeader(stream);
			switch (section.getChunkName()) {
				case "HANIM":
					// HAnim starts with general info:
					// -- 4b - UInt32 hAnimVersion - animation version format (0x100)
					// -- 4b - UInt32 nodeId - user id for this bone
					// -- 4b - UInt32 numNodes - number of bones in hierarchy (only for root bone; for all other
					//                                                         bones this parameter is set to 0)
					// if numNodes exists (affected bones):
					// -- 4b - UInt32 flags - flags for hierarchy animation
					// -- 4b - UInt32 keyFrameSize - size of data (bytes) needed for one anim frame (should be 36)
					// 
					// After this there will be an array of bone info:
					// -- 4b - UInt32 nodeId - user Id for this bone
					// -- 4b - UInt32 boneIdx - bone index for this array
					// -- 4b - UInt32 flags - bone flags					
					int animVersion = FileData.readUInt32(stream);
					int nodeId = FileData.readUInt32(stream);
					int numNodes = FileData.readUInt32(stream);

					HAnim anim = new HAnim(animVersion, nodeId, numNodes);
					
					if (numNodes != 0) {
						int flags = FileData.readUInt32(stream);
						int keyframeSize = FileData.readUInt32(stream);
						
						anim.setFlags(flags);
						anim.setKfSize(keyframeSize);
						
						for (int i = 0; i < numNodes; i++) {
							int id = FileData.readUInt32(stream);
							int boneIdx = FileData.readUInt32(stream);
							int boneFlags = FileData.readUInt32(stream);
							
							anim.addBone(i, id, boneIdx, boneFlags);
						}
					}
					
					exts.add(anim);
					break;
				case "FRAME":
					// stores a string representing the  
					// name of a frame inside a frame list
					String name = FileData.readString(stream, section.length());
					NodeName node = new NodeName(name);
					
					exts.add(node);
					break;
				case "BINMESH":
					// bin mesh:
					// -- uint32 - flags (0 = tri list, 1 = tri strip)
					// -- uint32 - number of meshes
					// -- uint32 - total indices
					// repeat for numMeshes
					// -- uint32 - num of indices
					// -- uint32 - material index
					// -- if (pre instanced):
					// ---- if (platform == OpenGL):
					// ------ uint16 - indices[numIndices]
					// ---- else:
					// ------ uint32 - indices[numIndices]	
					int type = FileData.readUInt32(stream); // tri list or tri strip
					int numMeshes = FileData.readUInt32(stream);
					int totalIndices = FileData.readUInt32(stream);
					
					// https://github.com/andrewixz/DFFLoader/blob/7b4659b7f5adc05845a2de8ae6718d8ec01a5a69/src/Reader.js#L387
					// https://github.com/Parik27/DragonFF/blob/ead5572256a609a21210e242cc2e9e7ca870829f/gtaLib/dff.py#L1684
					boolean preinstanced = 12 + numMeshes * 8 + (totalIndices * 2) >= section.length(); // (section.length() > 12 + numMeshes * 8);
					BinMesh bin = new BinMesh(type, totalIndices);
					
					for (int i = 0; i < numMeshes; i++) {
						int numIndices = FileData.readUInt32(stream);
						int matIndex = FileData.readUInt32(stream);
						bin.addMesh(matIndex);
						
						if (preinstanced) {
							int[] indices = new int[numIndices];
							for (int j = 0; j < numIndices; j++) {
								indices[j] = FileData.readUInt16(stream);
							}
							bin.addMesh(matIndex, indices);
						} else {
							int[] indices = new int[numIndices];
							for (int j = 0; j < numIndices; j++) {
								indices[j] = FileData.readUInt32(stream);
							}
							bin.addMesh(matIndex, indices);
						}
					}
					
					exts.add(bin);
					break;
				case "SKIN": 
					// Skins structure starts with a 4 byte val:
					// -- 1b - BYTE - numBones: Overall number of bones in the skeleton.
					// -- 1b - BYTE - usedBones: Number of bones affected by the skin.
					// -- 1b - BYTE - maxVertexWeights: Maximum number of non-zero weights per vertex.
					// -- 1b - BYTE - Padding
					// Then:
					// -- Xb - BYTE[usedBones]       - A list of bone indices, that are affected by the skin.
					// -- Xb - BYTE[numVertices][4]  - A list that maps all vertices to (up to) four bones of the skeleton.
					// -- Xb - FLOAT[numVertices]    - A list that weights each vertex-bone mapping.
					// And then, a list of transformation mats. are provided:
					// -- Xb - Bone[numBones]      - Bone transformations
					// Where each bone is:
					// -- struct Bone {
				    // -- DWORD unused;            // Only stored if version < 0x37000 && maxVertexWeights == 0
				    // -- FLOAT transform[4][4];   // Skin-to-Bone transform.
				    // -- };
					Skin skin;
					
					int numBones = FileData.readUInt8(stream);
					int numUsedBones = FileData.readUInt8(stream);
					int maxVertexWeights = FileData.readUInt8(stream);
					int padding = FileData.readUInt8(stream);
					
					int[] usedBones = new int[numUsedBones];
					for (int i = 0; i < numUsedBones; i++) {
						usedBones[i] = FileData.readUInt8(stream);
					}
					
					Geometry p = (Geometry)parent;
					int[][] vertexBoneIndices = new int[p.getNumVertices()][4];
					for (int v = 0; v < p.getNumVertices(); v++) {
						vertexBoneIndices[v][0] = FileData.readUInt8(stream); // x
						vertexBoneIndices[v][1] = FileData.readUInt8(stream); // y
						vertexBoneIndices[v][2] = FileData.readUInt8(stream); // z
						vertexBoneIndices[v][3] = FileData.readUInt8(stream); // w
					}
					
					float[][] vertexBoneWeights = new float[p.getNumVertices()][4];
					for (int v = 0; v < p.getNumVertices(); v++) {
						vertexBoneWeights[v][0] = FileData.readFloat32(stream); // x
						vertexBoneWeights[v][1] = FileData.readFloat32(stream); // y
						vertexBoneWeights[v][2] = FileData.readFloat32(stream); // z
						vertexBoneWeights[v][3] = FileData.readFloat32(stream); // w
					}
					
					// bone transformations
					float[][][] transform = new float[numBones][4][4]; // // skin to bone transform
					for (int b = 0; b < numBones; b++) {
						if (section.getVersion() < 0x37000 && maxVertexWeights == 0) {
							FileData.readUInt32(stream); // unused
						}
						for (int n = 0; n < 4; n++) {
							for (int m = 0; m < 4; m++) {
								transform[b][n][m] = FileData.readFloat32(stream); 
							}
						}
					}
					
					// bone group remapping
					// this feature is unused and the values are
					// usually 0, so we could ignore?
					// --
					// -- 4b - DWORD  - boneLimit: the maximum number of bones per group. (?)
					// -- 4b - DWORD  - numGroups: the number of bone groups.
					// -- 4b - DWORD  - numRemaps: the number of bone remappings.
					int boneLimit = FileData.readUInt32(stream);
					int numGroups = FileData.readUInt32(stream);
					int numRemaps = FileData.readUInt32(stream);
					
					if (numGroups > 0) {
						// Xb - BYTE[numBones]           - boneRemapIndices: an array of all bone indices where each element identifies a bone of the skeleton
						// Xb - BoneGroup[numGroups]     - boneGroups (see below)
						// Xb - BoneRemap[numRemaps]     - boneRemaps (see below)
						// --
						int[] boneRemapIndices = new int[numBones];
						for (int b = 0; b < numBones; b++) {
							boneRemapIndices[b] = FileData.readUInt8(stream);
						}
						
						// struct BoneGroup {
					    // BYTE firstBone;            // Index of first bone in boneRemaps array.
					    // BYTE numBones;             // Number of bones in boneRemaps that define the mesh (starting from firstBone).
					    //};
						int[][] boneGroup = new int[numGroups][2];
						for (int g = 0; g < numGroups; g++) {
							int firstBone = FileData.readUInt8(stream);
							int numRemapBones = FileData.readUInt8(stream);
							
							boneGroup[g][0] = firstBone;
							boneGroup[g][1] = numRemapBones;
						}
						
						// struct BoneGroup {
					    // BYTE firstBone;            // Index of first bone in boneRemaps array.
					    // BYTE numBones;             // Number of bones in boneRemaps that define the mesh (starting from firstBone).
					    // };
						int[][] boneRemap = new int[numRemaps][2];
						for (int r = 0; r < numRemaps; r++) {
							int boneIndex = FileData.readUInt8(stream);
							int indices = FileData.readUInt8(stream);
							
							boneRemap[r][0] = boneIndex;
							boneRemap[r][1] = indices;
						}
						
						skin = new Skin(numBones, numUsedBones, maxVertexWeights, usedBones, vertexBoneIndices, vertexBoneWeights, transform, boneLimit, numGroups, numRemaps, boneRemapIndices, boneGroup, boneRemap);
					} else {
						skin = new Skin(numBones, numUsedBones, maxVertexWeights, usedBones, vertexBoneIndices, vertexBoneWeights, transform, boneLimit, numGroups, numRemaps);
					}
					
					exts.add(skin);
					break;
				case "BREAKABLE":
					// TODO: complete breakable ext
					// after magic num, there is a 52 byte header
					// along with vertex, triangle, and material data
					int magic = FileData.readUInt32(stream);
					
					if (magic == 0) {
						break;
					}
					break;
				case "RIGHT_TO_RENDER":
					int identifier = FileData.readUInt32(stream);
					int extra = FileData.readUInt32(stream);
					
					RightToRender rtr = new RightToRender(identifier, extra);
					exts.add(rtr);
					break;
				case "MATERIAL_EFFECTS":
					// structure:
					//  uint32  - Type (Header)
					//  First Effect (variable size)
					//  Second Effect (variable size)
					MaterialEffects me;
					
					if (parent instanceof Atomic) {
						// read documentation; but:
						// bool32     - MatFX enabled
						int matfxEnabled = FileData.readUInt32(stream);
						me = new MaterialEffects(matfxEnabled);
						exts.add(me);
						break;
					}
					
					int typeHeader = FileData.readUInt32(stream);
					
					Effect[] effs = readEffect(typeHeader);
					
					if (effs[1] == null) {
						// second effect
						FileData.readUInt32(stream);
						me = new MaterialEffects(effs[0]);
					} else {
						me = new MaterialEffects(effs[0], effs[1]);
					}
					
					exts.add(me);
					break;
				case "REFLECTION_MATERIAL":
					// data layout:
					// -- 4b - FLOAT    - Environment Map Scale X
					// -- 4b - FLOAT    - Environment Map Scale Y
					// -- 4b - FLOAT    - Environment Map Offset X
					// -- 4b - FLOAT    - Environment Map Offset Y
					// -- 4b - FLOAT    - Reflection Intensity (Shininess, 0.0-1.0)
					// -- 4b - DWORD    - Environment Texture Ptr, always 0 (zero)
					float envMapSX = FileData.readFloat32(stream);
					float envMapSY = FileData.readFloat32(stream);
					float envMapOX = FileData.readFloat32(stream);
					float envMapOY = FileData.readFloat32(stream);
					float intensity = FileData.readFloat32(stream);
					FileData.readUInt32(stream); // environment tex ptr = always 0
					
					ReflectionMaterial rm = new ReflectionMaterial(envMapSX, envMapSY, envMapOX, envMapOY, intensity);
					
					exts.add(rm);
					break;
				case "SPECULAR_MATERIAL":
					// data layout:
					// -- 4b - FLOAT    - Specular Level (0.0-1.0)
					// -- 24b - CHAR[24] - Specular Texture Name, see below
					//On the PC and Xbox versions of the game a specular texture is not used.
					float specLevel = FileData.readFloat32(stream);
					byte[] specBuf = new byte[24];
					String specTexName = "";
					
					stream.read(specBuf);
					
					for (int s = 0; s < specBuf.length; s++) {
						if (specBuf[s] == 0) continue;
						specTexName += (char)specBuf[s];
					}
					
					SpecularMaterial specMat = new SpecularMaterial(specLevel, specTexName);
					
					exts.add(specMat);
					break;
				default:
					System.out.println(
						"Unexpected Chunk: " + 
						section.getChunkName() +
						" (" + section.getType() +
						", " + section.length() +
						", " + section.getLibID() +
						") " + " -- Skipping..."
					);
					
					position += section.length();
					break;
			}
			
			position += section.length()+0xC;
		}
		
		for (int e = 0; e < exts.size(); e++) {
			parent.addExtension(exts.get(e));
		}
	}
	
	public Effect[] readEffect(int type) throws IOException {
		Effect[] eff = new Effect[2];
		
		switch (type) {
			case 0x0:
				int rwMATFXNULL = FileData.readUInt32(stream);
				eff[0] = new Effect(rwMATFXNULL);
				break;
			case 0x1:
				Texture bump = null;
				Texture height = null;
				
				int rwMATFXEFFECTBUMPMAP = FileData.readUInt32(stream);
				float intensity = FileData.readFloat32(stream);
				int containsBumpMap = FileData.readUInt32(stream);
				
				if (containsBumpMap != 0) {
					readHeader(stream);
					bump = readTexture();
				}
				
				int containsHeightMap = FileData.readUInt32(stream);
				
				if (containsHeightMap != 0) {
					readHeader(stream);
					height = readTexture();
				}
				
				eff[0] = new Effect(rwMATFXEFFECTBUMPMAP, intensity, bump, height);
				break;
			case 0x2:
				Texture envMap = null;
				
				int rwMATFXENVMAP = FileData.readUInt32(stream);
				float reflectCoeff = FileData.readFloat32(stream);
				int useFBAlphaChannel = FileData.readUInt32(stream);
				int containEnvMap = FileData.readUInt32(stream);
				
				if (containEnvMap != 0) {
					readHeader(stream);
					envMap = readTexture();
	
					eff[0] = new Effect(rwMATFXENVMAP, reflectCoeff, useFBAlphaChannel, envMap);
				} else {
					eff[0] = new Effect(rwMATFXENVMAP, reflectCoeff, useFBAlphaChannel);
				}
				break;
			case 0x3:
				// has 2 effs
				// int rwMATFXEFFECTBUMPENVMAP = FileData.readUInt32(stream);
				Effect bumpEff = readEffect(FileData.readUInt32(stream))[0];
				Effect envEff = readEffect(FileData.readUInt32(stream))[0];
				
				eff[0] = bumpEff;
				eff[1] = envEff;
				break;
			case 0x4:
				Texture dualTexture = null;
				
				int rwMATFXEFFECTDUAL = FileData.readUInt32(stream);
				int srcBlendMode = FileData.readUInt32(stream); // int32 !!!
				int destBlendMode = FileData.readUInt32(stream);
				int containsTexture = FileData.readUInt32(stream);
				
				if (containsTexture != 0) {
					readHeader(stream);
					dualTexture = readTexture();
				}
				
				eff[0] = new Effect(rwMATFXEFFECTDUAL, srcBlendMode, destBlendMode, dualTexture);
				break;
			case 0x5:
				int rwMATFXEFFECTUVTRANSFORM = FileData.readUInt32(stream);
				eff[0] = new Effect(rwMATFXEFFECTUVTRANSFORM);
				break;
			case 0x6:
				// has 2 effs
				// int rwMATFXEFFECTDUALUVTRANSFORM = FileData.readUInt32(stream);
				Effect dualEff = readEffect(FileData.readUInt32(stream))[0];
				Effect uvEff = readEffect(FileData.readUInt32(stream))[0];
				
				eff[0] = dualEff;
				eff[1] = uvEff;
				break;
		}
		
		return eff;
	}
}
