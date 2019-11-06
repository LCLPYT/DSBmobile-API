package de.sematre.dsbmobile.web;

public interface Fetcher<T> {

	T fetch(String user, String password);
	
}
