package sh.raza.sand.loaders;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import sh.raza.sand.rw.Atomic;
import sh.raza.sand.rw.Extension;
import sh.raza.sand.rw.Frame;
import sh.raza.sand.rw.Header;
import sh.raza.sand.rw.geom.Geometry;
import sh.raza.sand.rw.geom.MorphTarget;
import sh.raza.sand.rw.geom.RpMaterial;
import sh.raza.sand.rw.plg.HAnim;
import sh.raza.sand.util.FileData;
import sh.raza.sand.util.GeomFlags;

import java.io.EOFException;

public class DFFLoader {
	// methodology for reading and loading dff files was
	// inspired by various projects and wikis such as:
	// -- https://github.com/andrewixz/DFFLoader/blob/master/src/Reader.js
	// -- https://github.com/Parik27/DragonFF/blob/master/gtaLib/dff.py
	// -- https://gtamods.com/wiki/RenderWare_binary_stream_file
	// -- https://gta.fandom.com/wiki/RenderWare_binary_stream_file
	private DataInputStream stream;
	
	public static void main(String[] args) throws IOException {
		DFFLoader dff = new DFFLoader();
		dff.parse("smoke.dff");
		
		Header r = dff.readHeader();
		
		if (r.getChunkName().equals("CLUMP")) {
			int[] clump = dff.readClump();
			System.out.println("CLUMP" + " " + clump[0] + " " + clump[1] + " " + clump[2]);
			Header f = dff.readHeader();
			if (f.getChunkName().equals("FRAME_LIST")) {
				dff.readFrameList();
				dff.readGeometryList();
//				System.out.println("reading frame list now..");
//				Header hk = dff.readHeader();
//				Header g = dff.readHeader();
//				if (g.getChunkName().equals("EXTENSION")) {
//					System.out.println("reading extension now..");
//					dff.readExtension(g);
//				}
//				for (int i =0;i<dff.frames.length; i++) {
//					System.out.println(dff.frames[i].getPosition()[0] + " " + dff.frames[i].getPosition()[1] + " " + dff.frames[i].getPosition()[2]);
//					System.out.println(dff.frames[i].getFrameIndex());
//					System.out.println(dff.frames[i].getFlags());
//					for (int j = 0; j < 3; j++) {
//						for (int k =0; k < 3; k++) {
//							System.out.print(dff.frames[i].getMatrix()[j][k] + " ");
//						}
//						System.out.println();
//					}
//					System.out.println();
//				}
			}
		}
		
		/*
		while ((read = FileData.readUInt32(dff.stream)) != -1) {
			System.out.print(read + " ");
		}
		*/
	}
	
	public void parse(String filename) {
		try {
			stream = new DataInputStream(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.out);
		}
	}
	
	// RW streams are split up into chunks with each
	// section consisting of a 12 byte header -- the
	// format of a header is as follows:
	// -- uint32 - type
	// -- uint32 - size, including child chunks and/or data
	// -- uint32 - library ID stamp
	public Header readHeader() throws IOException {
		int type = FileData.readUInt32(stream);
		int length = FileData.readUInt32(stream);
		int libID = FileData.readUInt32(stream);
		
		if (type == -1 || length == -1 || libID == -1)
			throw new EOFException();
		
		return new Header(type, length, libID);
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
		Header struct = readHeader();
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
		Header struct = readHeader();
		int num = FileData.readUInt32(stream);
		Frame[] frames = new Frame[num];
		
		for (int i = 0; i < num; i++) {
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
		
		for (int e = 0; e < num; e++) {
			readExtension(readHeader());
		}
		
		return frames;
	}
	
	// Geometry List has a struct section:
	// -- 4b - DWORD - Number of geometries
	public void readGeometryList() throws IOException {
		Header struct = readHeader();
		int num = FileData.readUInt32(stream);
		Geometry[] geoms = new Geometry[num];
		
		for (int i = 0; i < num; i++) {
			geoms[i] = readGeometry();
		}
	}
	
	public Geometry readGeometry() throws IOException {
		// Geometry chunk structure is kinda complicated
		// so I won't document it here -- read the 
		// documentation instead:
		// https://gtamods.com/wiki/RpGeometry#Structure
		Header struct = readHeader();
		Geometry geom;
		
		int format = FileData.readUInt32(stream);
		int triangles = FileData.readUInt32(stream);
		int vertices = FileData.readUInt32(stream);
		int morphTargets = FileData.readUInt32(stream);
		
		if (struct.getVersion() < 0x34000) {
			float ambient = FileData.readFloat32(stream);
			float specular = FileData.readFloat32(stream);
			float diffuse = FileData.readFloat32(stream);
			
			geom = new Geometry(triangles, vertices, morphTargets, ambient, specular, diffuse);
		} else {
			geom = new Geometry(triangles, vertices, morphTargets);
		}
		
		if ((format & GeomFlags.NATIVE) == 0) { 
			if ((format & GeomFlags.PRELIT) != 0) {
				// RwRGBA   prelitcolor[numVertices] (RwRGBA: uint8 r, g, b, a)
				int[][] plColor = new int[vertices][4];
				
				for (int v = 0; v < vertices; v++) {
					plColor[v][0] = FileData.readUInt8(stream); // r
					plColor[v][1] = FileData.readUInt8(stream); // g
					plColor[v][2] = FileData.readUInt8(stream); // b
					plColor[v][3] = FileData.readUInt8(stream); // a
				}
			}
		
			if ((format & (GeomFlags.TEXTURED | GeomFlags.TEXTURED2)) != 0) {
				// RwTexCoords    texCoords[numVertices]  (RwTexCoords: float32 u, v)
				int uvs = (format & 0x00FF0000) >> 16;
				float[][][] tex = new float[uvs][vertices][2];
					
				for (int t = 0; t < uvs; t++) {
					for (int v = 0; v < vertices; v++) {
						tex[t][v][0] = FileData.readFloat32(stream);
						tex[t][v][1] = FileData.readFloat32(stream);
					}
				}
			}
			
			// RpTriangle   triangles[numTriangles]  (RpTriangle: uint16 vertex2, vertex1, materialId, vertex3)
			int[][] tri = new int[triangles][4];
			
			for (int r = 0; r < triangles; r++) {
				tri[r][0] = FileData.readUInt16(stream); // vertex2
				tri[r][1] = FileData.readUInt16(stream); // vertex1
				tri[r][2] = FileData.readUInt16(stream); // materialId
				tri[r][3] = FileData.readUInt16(stream); // vertex3
			}
		}
		
		MorphTarget[] mt = new MorphTarget[morphTargets];
		
		for (int m = 0; m < morphTargets; m++) {
			float x = FileData.readFloat32(stream);
			float y = FileData.readFloat32(stream);
			float z = FileData.readFloat32(stream);
			float rad = FileData.readFloat32(stream);
			
			int hasVert = FileData.readUInt32(stream);
			int hasNorm = FileData.readUInt32(stream);
			
			MorphTarget morph = new MorphTarget(x, y, z, rad, vertices);
			
			if (hasVert != 0) {
				// RwV3d   vertices[numVertices] (RwV3d: float32 x, y, z)
				for (int v = 0; v < vertices; v++) {
					float vertX = FileData.readFloat32(stream);
					float vertY = FileData.readFloat32(stream);
					float vertZ = FileData.readFloat32(stream);
					
					morph.addVertices(v, vertX, vertY, vertZ);
				}
			}
			
			if (hasNorm != 0) {
				// RwV3d   normals[numVertices]
				for (int v = 0; v < vertices; v++) {
					float normX = FileData.readFloat32(stream);
					float normY = FileData.readFloat32(stream);
					float normZ = FileData.readFloat32(stream);
					
					morph.addNormals(v, normX, normY, normZ);
				}
			}
			
			mt[m] = morph;
		}
		
		return geom;
	}
	
	public void readMaterialList() throws IOException {
		// Material list structure:
		// 4b - DWORD - number of materials
		// 4b[] - DWORD[] - array of material indices
		Header struct = readHeader();
		int num = FileData.readUInt32(stream);
		int[] idxs = new int[num];
		
		System.out.println("should be Struct: " + struct.getChunkName());
		
		for (int i = 0; i < num; i++) {
			idxs[i] = FileData.readUInt32(stream);
		}
		
		RpMaterial[] mats = new RpMaterial[num];
		for (int m = 0; m < num; m++) {
			mats[m] = readMaterial();
		}
	}
	
	public RpMaterial readMaterial() throws IOException {
		// Material structure is as follows:
		// -- int32 - flags (unused)
		// -- uint8[] - color (r,g,b,a)
		// -- int32 - unused (???)
		// -- bool32 - isTextured
		// -- if version > 0x30400
		// ---- float ambient, specular, diffuse
		RpMaterial mat;
		
		Header struct = readHeader();
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
			String[] tex = readTexture().split("/");
			mat.setTexture(Integer.parseInt(tex[0]), tex[1], tex[2]);
		}
		
		return mat;
		// READ EXTENSION
	}
	
	public String readTexture() throws IOException {
		// texture is kinda confusing so just
		// read the documentation
		// https://gtamods.com/wiki/Texture_(RW_Section)
		Header struct = readHeader();
		int flags = FileData.readUInt16(stream);
		// checking for mip not necessary
		FileData.readUInt16(stream);
		String texName = readString();
		String layerName = readString();
		
		return flags + "/" + texName + "/" + layerName;
	}
	
	public String readString() throws IOException {
		// String includes a trailing zero
		// and is padded with zeros to the
		// next 4b boundary 
		Header struct = readHeader();
		String result = FileData.readString(stream, struct);
		return result;
	}
	
	public void readAtomic() throws IOException {
		// atomic section contains Struct,
		// Geometry (opt?), and Extensions
		// the struct is as follows:
		// -- uint32 - frameIndex
		// -- uint32 - geomIndex
		// -- uint32 - flags
		// -- uint32 - unused (???)
		Atomic atom;
		
		Header struct = readHeader();
		int frame = FileData.readUInt32(stream);
		int geom = FileData.readUInt32(stream);
		int flags = FileData.readUInt32(stream);
		FileData.readUInt32(stream); // unused
		
		atom = new Atomic(frame, geom, flags);
	}
	// Extensions are mainly for plugin data
	public void readExtension(Header header) throws IOException {
		int position = 0;
		Extension ext;
		
		while (position < header.length()) {
			Header section = readHeader();
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
					
					break;
				case "FRAME":
					// stores a string representing the  
					// name of a frame inside a frame list
					String name = FileData.readString(stream, section);
					
					// TODO: do something with frame and hanim
					
					// the plugin is registered with 24 bytes
					// allocated for the data size
					position += 24;
					break;
				default:
					System.out.println("Unexpected Chunk: " + section.getChunkName() + " -- Skipping...");
					break;
			}
			
			position += section.length();
		}
	}
}
