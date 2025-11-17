package nl.rabobank.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.UnknownHostException;

@Configuration
@EnableMongoRepositories
public class EmbeddedMongoConfiguration {

    private TransitionWalker.ReachedState<RunningMongodProcess> running;
    @Value("${spring.data.mongodb.port:27027}")
    private int port;
    @Value("${spring.data.mongodb.host:localhost}")
    private String host;

    @PostConstruct
    public void startMongo() throws UnknownHostException {
        Net net = Net.builder()
                .bindIp(host)
                .port(port)
                .isIpv6(de.flapdoodle.net.Net.localhostIsIPv6())
                .build();
        running = Mongod.instance()
                .withNet(Start.to(Net.class).initializedWith(net))
                .start(Version.Main.V8_0);
        this.port = running.current().getServerAddress().getPort();
    }

    @PreDestroy
    public void stopMongo() {
        if (running != null) {
            running.close();
        }
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://" + host + ":" + port);
    }
}