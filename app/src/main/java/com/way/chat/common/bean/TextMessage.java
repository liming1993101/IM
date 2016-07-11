package com.way.chat.common.bean;

import java.io.File;
import java.io.Serializable;

/**
 * 文本消息
 * 
 * @author way
 */
public class TextMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private File img;

	public File getImg() {
		return img;
	}

	public void setImg(File img) {
		this.img = img;
	}

	public TextMessage() {

	}

	public TextMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
