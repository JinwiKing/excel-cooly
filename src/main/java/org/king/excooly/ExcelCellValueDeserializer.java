package org.king.excooly;

import org.king.excooly.support.ExcelValueDeserializerParameter;

/**
 * Excel值到Java值反序列化器接口
 * @author wangjw5
 */
@FunctionalInterface
public interface ExcelCellValueDeserializer {
	
	/**
	 * 反序列化Excel值
	 */
	Object deserialize(ExcelValueDeserializerParameter deserializerParam);
}
