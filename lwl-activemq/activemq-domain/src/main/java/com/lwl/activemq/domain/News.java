package com.lwl.activemq.domain;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class News implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	
	private String title;
	
	private String content;
	
	private String url;
	
	private String author;

	@Override
	public String toString() {
		return "News [id=" + id + ", title=" + title + ", content=" + content
				+ ", url=" + url + ", author=" + author + "]";
	}
	
}
