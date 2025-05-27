package org.example.gmaildemoproject;

import org.example.gmaildemoproject.config.LabelDefinitionsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({LabelDefinitionsProperties.class})
public class GmailApiDemoProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmailApiDemoProjectApplication.class, args);
	}

}
