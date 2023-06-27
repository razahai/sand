package sh.raza.sand.util;

import java.io.IOException;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import sh.raza.sand.rw.Header;

public class FileData {
	public static int readUInt32(DataInputStream is) throws IOException {
		// based on:
		// http://www.java2s.com/example/java/java.io/reads-a-uint32-from-the-inputstream-and-returns-it-as-a-java-int.html
		byte[] buffer = new byte[4];
		ByteBuffer byteBuf = ByteBuffer.wrap(buffer);
		
		if (is.read(buffer) < 0) 
			return -1;
		
		byteBuf.order(ByteOrder.LITTLE_ENDIAN);
		byteBuf.position(0);
		
		return byteBuf.getInt();	
	}
	
	public static int readUInt16(DataInputStream is) throws IOException {
		byte[] buffer = new byte[2];
		ByteBuffer byteBuf = ByteBuffer.wrap(buffer);
		
		if (is.read(buffer) < 0)
			return -1;
		
		byteBuf.order(ByteOrder.LITTLE_ENDIAN);
		byteBuf.position(0);
		
		return byteBuf.getInt();
	}
	
	public static int readUInt8(DataInputStream is) throws IOException {
		byte signed = is.readByte();
		return (signed & 0xFF);
	}
	
	public static float readFloat32(DataInputStream is) throws IOException {
		byte[] buffer = new byte[4];
		ByteBuffer byteBuf = ByteBuffer.wrap(buffer);
		
		if (is.read(buffer) < 0)
			return -1;
		
		byteBuf.order(ByteOrder.LITTLE_ENDIAN);
		byteBuf.position(0);
		
		return byteBuf.getFloat();
	}
	
	public static String readString(DataInputStream is, Header h) throws IOException {
		byte[] buffer = new byte[h.length()];
		is.read(buffer);
		String result = "";
		
		for (int i = 0; i < buffer.length; i++) {
			result += (char)buffer[i];
		}
		
		return result;
	}
}
