package com.lipl.youthconnect.youth_connect.util;

public class FileOption implements Comparable<FileOption>{
	private String name;
	private String data;
	private String path;
	private boolean isSelected;

	public FileOption(String n, String d, String p, boolean isSelected)
	{
		name = n;
	    data = d;
	    path = p;
		this.isSelected = isSelected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	 public int compareTo(FileOption o) {
	 if(this.name != null)
	         return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
	   else
	      throw new IllegalArgumentException();
	}
	public void toggleChecked() {
		isSelected = !isSelected ;
	}
}