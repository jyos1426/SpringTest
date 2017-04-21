package aspectj.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MyAdvice {
	
	@Pointcut("execution(* com.my.vo.*.*())")
	private void aaa(){
		
	}
	
	//@Around("aaa()")
	public Object around(ProceedingJoinPoint joinpoint) throws Exception{
		System.out.println("핵심로직 실행 전");
		try {
			return joinpoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}finally{
			System.out.println("핵심로직 실행 후");			
		}
		return null;
	}
	
	@Before("aaa()")
	public void before(){
		System.out.println("핵심로직 실행 전");
	}
	
	//@After("aaa()")
	public void after(){
		System.out.println("핵심로직 실행 후");
	}
	
	//AfrterReturning: 반환값이 있는 경우
	@AfterReturning(pointcut="execution(* com.my.vo.*.*())",returning="obj")
	public void afterReturn(Object obj){
		System.out.println("핵심로직 반환 후");
		System.out.println("AfterReturning Advice가 반환받은결과:"+obj);
	}
	
}
