package org.jetuml.aGenerateJava.Diagram_DCtest1;

import java.util.*;
public class ClassRoom {

	//Attributes 
	private String roomNumber;

//Constrecteur
	public ClassRoom(){}
	public ClassRoom(String roomNumber){
		
		this.roomNumber = roomNumber;
	}

	//methodes
	public void SetroomNumber(String roomNumber){
	this.roomNumber = roomNumber;
}

	public String getroomNumber(){
		return roomNumber;
	}

	public void VerifyMaterials(){
	//function body
	}
}
