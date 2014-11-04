package com.italk2learn.vo;


public class SpeechRecognitionRequestVO extends RequestVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int instance;
	private String server;
	private String language;
	private String model;
	private byte[] finalByteArray;
	
	private byte[] data;
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getInstance() {
		return instance;
	}

	public void setInstance(int instance) {
		this.instance = instance;
	}

	public byte[] getFinalByteArray() {
		return finalByteArray;
	}

	public void setFinalByteArray(byte[] finalByteArray) {
		this.finalByteArray = finalByteArray;
	}

}
