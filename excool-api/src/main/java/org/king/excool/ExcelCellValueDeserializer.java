package org.king.excool;

/**
 * Excel值到Java值反序列化器接口
 * @author wangjw5
 */
@FunctionalInterface
public interface ExcelCellValueDeserializer {
	
	/**
	 * 反序列化Excel值
	 */
	default Object deserialize(ExcelValueDeserializerParameter deserializerParam){
		try {
			return doDeserialize(deserializerParam);
		} catch (Exception e) {
			throw new RuntimeException("Deserializing row no. " + (deserializerParam.rowIdx() + 1) + ", column no. " + (deserializerParam.colIdx() + 1) + " failure" + e.getMessage());
		}
	}
	
	Object doDeserialize(ExcelValueDeserializerParameter deserializerParam);
}
