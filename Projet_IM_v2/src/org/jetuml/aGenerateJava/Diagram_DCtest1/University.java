package org.jetuml.aGenerateJava.Diagram_DCtest1;

import java.util.*;
public class University {

	//Attributes 
	private ClassRoom classroom;
	private Departments departments;
	private String universityName;

//Constrecteur
	public University(){}
	public University(String universityName){
		
		this.universityName = universityName;
		this.classroom = new ClassRoom();
		this.departments = new Departments();
	}

	//methodes
	public void SetuniversityName(String universityName){
	this.universityName = universityName;
}

	public String getuniversityName(){
		return universityName;
	}

	public void Costs(){
	//function body
	}
}
