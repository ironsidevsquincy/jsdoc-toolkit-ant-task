package uk.co.darrenhurley.ant.types;

import org.apache.tools.ant.Project;

public class Arg {
	private String name;
	private String value;
	private Project project;

	public Arg(Project project){
		this.project = project;
	}
	
	public Arg(){
	}
	
	public void setName(String name){
		this.name= name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public Project getProject(){
		return project;
	}
}
