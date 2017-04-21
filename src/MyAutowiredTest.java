import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.my.annotation.MyAutowired;

class DAO{
	public String toString(){
		return " DAO 객체입니다.";
	}
}

class Handler{
	@MyAutowired
	DAO dao;	
	public void print(){
		System.out.println("현재 사용중인 dao" + dao);
	}
}

public class MyAutowiredTest {
	public static void main(String[] args) {
		DAO dao = new DAO();
		Class clazz;
		
		try {
			clazz = Class.forName("Handler");
			Object obj = clazz.newInstance();
			
			Field[] fields = clazz.getDeclaredFields();
			for(Field field: fields){
				Class fieldTypeClass = field.getType();
				Annotation[] as = field.getAnnotations();
				
				for(Annotation a1 : as){
					if(a1.annotationType() == MyAutowired.class){
						if(fieldTypeClass.isInstance(dao)){
							field.set(obj, dao);
						}
					}
				}
			}
			
			Method[] methods = clazz.getDeclaredMethods();
			for(Method method: methods){
				if(method.getName().equals("print")){
					method.invoke(obj, null);
				}
				Annotation[] as = method.getAnnotations();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
