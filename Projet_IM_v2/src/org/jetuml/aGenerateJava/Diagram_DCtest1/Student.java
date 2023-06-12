package org.jetuml.aGenerateJava.Diagram_DCtest1;

import java.util.*;
public class Student extends Person {

	//Attributes 
	private String studentId;

//Constrecteur
	public Student(){}
	public Student(String studentId, String nom, int age){
		super(nom,age);
		this.studentId = studentId;
	}

	//methodes
	public void SetstudentId(String studentId){
	this.studentId = studentId;
}

	public String getstudentId(){
		return studentId;
	}

	public void CalculateResults(){
	//function body
	}
}
