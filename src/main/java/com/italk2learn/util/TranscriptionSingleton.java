package com.italk2learn.util;

import java.util.ArrayList;
import java.util.List;

public class TranscriptionSingleton {
	private static TranscriptionSingleton instance = null;

	private List<String> currentWords=new ArrayList<String>();
	private int counter=0;

	protected TranscriptionSingleton() {
		// Exists only to defeat instantiation.
	}

	public static TranscriptionSingleton getInstance() {
		if (instance == null) {
			instance = new TranscriptionSingleton();
		}
		return instance;
	}

	public List<String> getCurrentWords() {
		return currentWords;
	}

	public void setCurrentWords(List<String> currentWords) {
		this.currentWords = currentWords;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}
