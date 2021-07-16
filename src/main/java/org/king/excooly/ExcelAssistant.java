package org.king.excooly;

import java.io.InputStream;

public class ExcelAssistant {
	
	public ExcelType detectExcelTypeFromStream(InputStream is) {
		try {
			byte[] bytes = new byte[4];
			is.read(bytes);
			if((bytes[0] & 0x000000D0) == 0x000000D0 && (bytes[1] & 0x000000CF) == 0x000000CF && 
				(bytes[2] & 0x00000011) == 0x00000011 && (bytes[3] & 0x000000E0) == 0x000000E0) return ExcelType.XLS;
			else if((bytes[0] & 0x00000050) == 0x00000050 && (bytes[1] & 0x0000004B) == 0x0000004B && 
				(bytes[2] & 0x00000003) == 0x00000003 && (bytes[3] & 0x00000004) == 0x00000004) return ExcelType.XLSX;
			else return ExcelType.UNKNOWN;
		} catch (Exception e) {
			throw new RuntimeException("Can detect excel type", e);
		}
	}
}
