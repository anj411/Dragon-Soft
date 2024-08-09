package entpack;

import com.jfinal.config.Routes;
import entpack.controller.IndexController;
import entpack.controller.DragonSoftController;

/**
 * 路由配置
 */
public class RouteConfig extends Routes {
	public void config() {
		setBaseViewPath("/views/");
		add("/", IndexController.class, "index");


			add("/dragonSoft", DragonSoftController.class);


		System.out.println("RouteConfig");
	}
}
