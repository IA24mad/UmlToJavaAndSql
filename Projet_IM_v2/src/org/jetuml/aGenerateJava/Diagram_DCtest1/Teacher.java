package org.jetuml.aGenerateJava.Diagram_DCtest1;

import java.util.*;
public class Teacher extends Person {

	//Attributes 
	private String teacher_id;

//Constrecteur
	public Teacher(){}
	public Teacher(String teacher_id, String nom, int age){
		super(nom,age);
		this.teacher_id = teacher_id;
	}

	//methodes
	public void Setteacher_id(String teacher_id){
	this.teacher_id = teacher_id;
}

	public String getteacher_id(){
		return teacher_id;
	}

	public void CalculeSalaire(){
	//function body
	}
}
