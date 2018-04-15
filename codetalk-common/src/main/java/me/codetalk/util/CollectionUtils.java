package me.codetalk.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by guobxu on 17/7/2017.
 */
public class CollectionUtils {

    public static final String concat(List<String> list, String sep) {
        StringBuffer buf = new StringBuffer();

        for(int i = 0, size = list.size(); i < size; i++) {
            buf.append(list.get(i));

            if(i != size - 1) {
                buf.append(sep);
            }
        }

        return buf.toString();
    }
    
    public static final <T, R> String concat(List<T> list, Function<T, R> func, String sep) {
        StringBuffer buf = new StringBuffer();

        for(int i = 0, size = list.size(); i < size; i++) {
            buf.append(func.apply(list.get(i)));

            if(i != size - 1) {
                buf.append(sep);
            }
        }

        return buf.toString();
    }
    
    public static final String concat(Set<String> set, String sep) {
        List<String> list = new ArrayList<>();
        list.addAll(set);
        
        return concat(list, sep);
    }
    
    public static final String concat(String[] strarr, String sep) {
        StringBuffer buf = new StringBuffer();

        for(int i = 0, size = strarr.length; i < size; i++) {
            buf.append(strarr[i]);

            if(i != size - 1) {
                buf.append(sep);
            }
        }

        return buf.toString();
    }
    
    public static final boolean isEmpty(Collection collection) {
    	return collection == null || collection.size() == 0;
    }
    
    public static final <T> List<T> toList(Set<T> set) {
    	List<T> list = new ArrayList<>();
    	list.addAll(set);
    	
    	return list;
    }

}
