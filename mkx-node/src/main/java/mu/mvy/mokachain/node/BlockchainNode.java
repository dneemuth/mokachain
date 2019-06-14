package mu.mvy.mokachain.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@ComponentScan("mu.mvy.mokachain")
@SpringBootApplication
public class BlockchainNode {

	public static void main(String[] args) {
		SpringApplication.run(BlockchainNode.class, args);
	}
}
