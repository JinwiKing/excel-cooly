package org.king.excooly;

public class BasicTestBean {
	private String id;
	private String name;
	private String sex;
	
	private int intType;
	private Integer integerType;
	private String stringType;
	private float floatType;
	private Float wrapperedFloatType;
	private double doubleType;
	private Double wrapperedDoubleType;
	
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
	
	@Override
	public String toString() {
		return "BasicTestBean [id=" + id + ", name=" + name + ", sex=" + sex + ", intType=" + intType + ", integerType="
				+ integerType + ", stringType=" + stringType + ", floatType=" + floatType + ", wrapperedFloatType="
				+ wrapperedFloatType + ", doubleType=" + doubleType + ", wrapperedDoubleType=" + wrapperedDoubleType
				+ "]";
	}
}
