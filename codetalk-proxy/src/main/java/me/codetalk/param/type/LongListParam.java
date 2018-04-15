package me.codetalk.param.type;

import java.util.List;

public class LongListParam extends AbstractParam {

	private boolean allowEmpty = false;
	
	public LongListParam(String name, boolean required) {
		this(name, required, false);
	}
	
	public LongListParam(String name, boolean required, boolean allowEmpty) {
		super(name, required);
		
		this.allowEmpty = allowEmpty;
	}
	
	public boolean isValid(Object objParam) {
		if(required && objParam == null) return false;
		if(!required && objParam == null) return true;
		
		try {
			List<Object> objList = (List<Object>)objParam;
			
			for(Object obj : objList) {
				if(obj == null || (!(obj instanceof Integer) && !(obj instanceof Long))) return false;
			}
			
			if(( required || !allowEmpty ) && objList.size() == 0) return false;
		} catch(Exception ex) {
			return false;
		}
		
		return true;
	}

}














