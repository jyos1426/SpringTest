import java.util.List;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.my.dao.CustomerDAO;
import com.my.dao.ProductDAO;
import com.my.vo.Customer;
import com.my.vo.Product;

public class Test {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		String configLocation = "classpath:applicationContext.xml";
		GenericXmlApplicationContext ctx;
		ctx = new GenericXmlApplicationContext(configLocation);
		
		CustomerDAO dao = ctx.getBean("customerDAO",CustomerDAO.class);
		Customer c;
		ProductDAO pdao = ctx.getBean("productDAO",ProductDAO.class);
		List<Product> p;
		try {
			c = dao.selectById("do");
			System.out.println(c);

			pdao.insert(new Product("sooh","yun",1004));
			p = pdao.selectAll();
			System.out.println(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
