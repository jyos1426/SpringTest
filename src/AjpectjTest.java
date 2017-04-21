import org.springframework.context.support.GenericXmlApplicationContext;

import com.my.vo.Customer;
import com.my.vo.Product;

public class AjpectjTest {

	public static void main(String[] args) {
		GenericXmlApplicationContext ctx =
				new GenericXmlApplicationContext("classpath:applicationContext.xml");
//		Customer c = ctx.getBean("customer", Customer.class);
//		System.out.println(c.toString());
		
		Product p = ctx.getBean("product",Product.class);
		System.out.println(p.toString());
				
	}
}
