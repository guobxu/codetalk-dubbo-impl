package me.codetalk.webminer.page;

/**
 * DOM元素属性
 * 
 * @author guobxu
 *
 */
public class PageAttr {

	private String el;		// element 
	private String name;	// attr name	如果为null 取text 或者 html
	private Integer type; 	// type 1 html 2 text
	
	public PageAttr(String el, String name) {
		this.el = el;
		this.name = name;
	}
	
	public PageAttr(String el, String name, Integer type) {
		this(el, name);
		
		this.type = type;
	}
	
	public String getEl() {
		return el;
	}
	
	public void setEl(String el) {
		this.el = el;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	
	
}
