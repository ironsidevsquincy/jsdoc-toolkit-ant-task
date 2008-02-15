package uk.co.darrenhurley.ant.types;

import org.apache.tools.ant.Project;

public class Source {
	private String file;
	private Project project;
	
	public Source(Project project){
		this.project = project;
	}
	
	public Source(){
	}
	
	public void setFile(String file){
		this.file = file;
	}
	
	public String getFile(){
		return this.file;
	}
	
	public Project getProject(){
		return this.project;
	}
}
