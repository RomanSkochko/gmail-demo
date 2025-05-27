package org.example.gmaildemoproject;

import org.example.gmaildemoproject.properties.GmailProcessingProperties;
import org.example.gmaildemoproject.properties.LabelDefinitionsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({LabelDefinitionsProperties.class, GmailProcessingProperties.class})
public class GmailApiDemoProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmailApiDemoProjectApplication.class, args);
	}

}
