package me.codetalk.param.type;

import java.util.List;

import me.codetalk.util.StringUtils;

public class StringListParam extends AbstractParam {

	private boolean allowEmpty = false;
	
	public StringListParam(String name, boolean required) {
		this(name, required, false);
	}
	
	public StringListParam(String name, boolean required, boolean allowEmpty) {
		super(name, required);
		
		this.allowEmpty = allowEmpty;
	}
	
	public boolean isValid(Object objParam) {
		if(required && objParam == null) return false;
		if(!required && objParam == null) return true;
		
		try {
			List<Object> objList = (List<Object>)objParam;
			
			for(Object obj : objList) {
				if(obj == null || !(obj instanceof String)) return false;
				
				String s = (String)obj;
				if(StringUtils.isNull(s)) {
					return false;
				}
			}
			
			if(( required || !allowEmpty ) && objList.size() == 0) return false;
		} catch(Exception ex) {
			return false;
		}
		
		return true;
	}

}














