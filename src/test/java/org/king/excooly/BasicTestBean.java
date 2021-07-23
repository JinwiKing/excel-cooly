package org.king.excooly;

public class BasicTestBean {
	@ExcelCell(name = "id", serializingName = "id")
	private String id;
	@ExcelCell(name = "name", serializingName = "name")
	private String name;
	@ExcelCell(name = "性别", serializingName = "性别")
	private String sex;

	@ExcelCell(name = "整数类型", serializingName = "整数类型")
	private int intType;
	@ExcelCell(name = "整数类型", requiredForSerializing = false)
	private Integer integerType;
	@ExcelCell(name = "文本类型", serializingName = "文本类型")
	private String stringType;
	@ExcelCell(name = "浮点类型", requiredForSerializing = false)
	private float floatType;
	@ExcelCell(name = "浮点类型", requiredForSerializing = false)
	private Float wrapperedFloatType;
	@ExcelCell(name = "浮点类型", requiredForSerializing = false)
	private double doubleType;
	@ExcelCell(name = "浮点类型", serializingName = "浮点类型")
	private Double wrapperedDoubleType;
	
	@ExcelCell(matchPattern = "模糊匹配名称.*", serializingName = "模糊匹配值")
	private String patternMatch;
	
	@ExcelCell(name = "文本类型", deserializer = BasicTestBeanText2IntDeserializer.class, serializingName = "des值")
	private int usingDeserializer;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getIntType() {
		return intType;
	}
	public void setIntType(int intType) {
		this.intType = intType;
	}
	public Integer getIntegerType() {
		return integerType;
	}
	public void setIntegerType(Integer integerType) {
		this.integerType = integerType;
	}
	public String getStringType() {
		return stringType;
	}
	public void setStringType(String stringType) {
		this.stringType = stringType;
	}
	public float getFloatType() {
		return floatType;
	}
	public void setFloatType(float floatType) {
		this.floatType = floatType;
	}
	public Float getWrapperedFloatType() {
		return wrapperedFloatType;
	}
	public void setWrapperedFloatType(Float wrapperedFloatType) {
		this.wrapperedFloatType = wrapperedFloatType;
	}
	public double getDoubleType() {
		return doubleType;
	}
	public void setDoubleType(double doubleType) {
		this.doubleType = doubleType;
	}
	public Double getWrapperedDoubleType() {
		return wrapperedDoubleType;
	}
	public void setWrapperedDoubleType(Double wrapperedDoubleType) {
		this.wrapperedDoubleType = wrapperedDoubleType;
	}
	public String getPatternMatch() {
		return patternMatch;
	}
	public void setPatternMatch(String patternMatch) {
		this.patternMatch = patternMatch;
	}
	public int getUsingDeserializer() {
		return usingDeserializer;
	}
	public void setUsingDeserializer(int usingDeserializer) {
		this.usingDeserializer = usingDeserializer;
	}
	
	@Override
	public String toString() {
		return "BasicTestBean [id=" + id + ", name=" + name + ", sex=" + sex + ", intType=" + intType + ", integerType="
				+ integerType + ", stringType=" + stringType + ", floatType=" + floatType + ", wrapperedFloatType="
				+ wrapperedFloatType + ", doubleType=" + doubleType + ", wrapperedDoubleType=" + wrapperedDoubleType
				+ ", patternMatch=" + patternMatch + ", usingDeserializer=" + usingDeserializer + "]";
	}
}
