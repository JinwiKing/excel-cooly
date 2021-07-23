package org.king.excooly;

import java.util.List;

public class BasicTestBean3 {
	@ExcelCell(name = "id", serializingName = "id")
	private String id;
	@ExcelCell(name = "name", serializingName = "name")
	private String name;
	@ExcelCell(name = "性别", serializingName = "sex")
	private String sex;
	
	@ExcelCascadeCell(concreteType = StudentClass.class)
	private List<StudentClass> classes;

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

	public List<StudentClass> getClasses() {
		return classes;
	}

	public void setClasses(List<StudentClass> classes) {
		this.classes = classes;
	}

	@Override
	public String toString() {
		return "BasicTestBean3 [id=" + id + ", name=" + name + ", sex=" + sex + ", classes=" + classes + "]";
	}
}
