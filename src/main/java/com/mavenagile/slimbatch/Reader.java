package com.mavenagile.slimbatch;

public interface Reader
{
	public boolean setupReader();

	public Object readNextItem();

	public boolean closeReader(); 
}
