package mu.mvy.mokachain.common.utils;

import java.util.List;
import java.util.Map;

public class SanityCheck {

	
	public static boolean isValid(Object obj){
		boolean isValid = false;
		
		if(obj!=null){
			if((obj instanceof String) && !obj.equals("")){
				isValid=isValid((String)obj);
			}else if(obj instanceof List){
				isValid=isValid((List<?>)obj);
			}else if(obj instanceof Object[]){
				isValid=isValid((Object[])obj);
			}else if(obj instanceof Map<?, ?>){
				isValid=isValid((Map<?, ?>)obj);
			}else{
				isValid=true;
			}
		}
		
		return isValid;
	}
	
	public static boolean isValid(String obj){
		boolean isValid = false;
		if(obj!=null && !obj.equals("")){
			isValid=true;
		}
		return isValid;
	}
	
	public static boolean isValid(Object[] arr){
		boolean isValid = false;
		if(arr!=null && !(arr.length==0)){
			isValid=true;
		}
		return isValid;
	}
	
	public static boolean isValid(List<?> obj){
		boolean isValid = false;
		if(obj!=null && !obj.isEmpty()){
			isValid=true;
		}
		return isValid;
	}
	
	public static boolean isValid(Map<?,?> map){
		boolean isValid = false;
		if(map!=null && !map.isEmpty()){
			isValid=true;
		}
		return isValid;
	}
}
