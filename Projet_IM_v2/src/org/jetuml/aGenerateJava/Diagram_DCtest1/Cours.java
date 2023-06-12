package org.jetuml.aGenerateJava.Diagram_DCtest1;

import java.util.*;
public class Cours {

	//Attributes 
	private ClassRoom classroom;
	private String coursName;

//Constrecteur
	public Cours(){}
	public Cours(String coursName, ClassRoom classroom){
		
		this.coursName = coursName;
		this.classroom = classroom;
	}

	//methodes
	public void SetcoursName(String coursName){
	this.coursName = coursName;
}

	public String getcoursName(){
		return coursName;
	}

	public void CoursSessions(){
	//function body
	}
}
