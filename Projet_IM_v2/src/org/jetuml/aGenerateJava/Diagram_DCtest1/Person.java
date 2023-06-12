package org.jetuml.aGenerateJava.Diagram_DCtest1;

import java.util.*;
public class Person {

	//Attributes 
	private String nom;
	private int age;

//Constrecteur
	public Person(){}
	public Person(String nom, int age){
		
		this.nom = nom;
		this.age = age;
	}

	//methodes
	public void Setnom(String nom){
	this.nom = nom;
}

	public String getnom(){
		return nom;
	}

	public void Setage(int age){
	this.age = age;
}

	public int getage(){
		return age;
	}

	public void Introduce(){
	//function body
	}
}
