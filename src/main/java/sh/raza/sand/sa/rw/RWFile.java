package sh.raza.sand.sa.rw;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import sh.raza.sand.util.FileData;

public class RWFile  {
	// RW streams are split up into chunks with each
	// section consisting of a 12 byte header -- the
	// format of a header is as follows:
	// -- uint32 - type
	// -- uint32 - size, including child chunks and/or data
	// -- uint32 - library ID stamp
	public Header readHeader(DataInputStream stream) throws IOException {
		int type = FileData.readUInt32(stream);
		int length = FileData.readUInt32(stream);
		int libID = FileData.readUInt32(stream);
		
		if (type == -1 || length == -1 || libID == -1)
			throw new EOFException();
		
		return new Header(type, length, libID);
	}
}
