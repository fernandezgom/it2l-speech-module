package com.italk2learn.util;

import java.util.ArrayList;
import java.util.List;

public class TranscriptionSingleton {
	private static TranscriptionSingleton instance = null;

	private List<String> currentWords=new ArrayList<String>();

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
}
