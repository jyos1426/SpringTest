import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.stereotype.Repository;

import com.my.annotation.CustomAnnotation;


class A{
	@CustomAnnotation(id=99,description="test입니다")
	int num;
	void print(){
		System.out.println("num값은: "+num);
	}
}

public class AnnotationTest {
	public static void main(String[] args) {
		Class clazz;
		try {
			clazz = Class.forName("A");
			Object obj = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			
			for(Field field: fields){
				System.out.println("필드이름:"+field.getName());
				Annotation[] as = field.getAnnotations();
				for(Annotation a1 : as){
					if(a1.annotationType() == CustomAnnotation.class){
						System.out.println("어노테이션:" + a1);
						CustomAnnotation ca = (CustomAnnotation)a1;
						field.setInt(obj, ca.id());	//nul=1;
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
