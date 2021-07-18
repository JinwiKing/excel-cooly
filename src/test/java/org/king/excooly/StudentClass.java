package org.king.excooly;

public class StudentClass {
	@ExcelCell(name = "classno")
	private String classNo;
	@ExcelCell(name = "classname")
	private String className;
	public String getClassNo() {
		return classNo;
	}
	public void setClassNo(String classNo) {
		this.classNo = classNo;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	@Override
	public String toString() {
		return "StudentClass [classNo=" + classNo + ", className=" + className + "]";
	}
}
