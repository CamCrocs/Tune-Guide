package Project.TuneGuide;

import Project.TuneGuide.Components.StartApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class TuneGuideApplication {
	public static String SERVER_PATH = "https://accounts.spotify.com";

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TuneGuideApplication.class, args);

		StartApp startApp = context.getBean(StartApp.class);
		startApp.start();

		context.close();
	}
}
