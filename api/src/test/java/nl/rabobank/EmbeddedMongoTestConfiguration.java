package nl.rabobank;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@TestConfiguration
@Profile("test")
public class EmbeddedMongoTestConfiguration {

    private TransitionWalker.ReachedState<RunningMongodProcess> running;

    @Value("${spring.data.mongodb.host:localhost}")
    private String host;

    private int port;

    @PostConstruct
    public void startMongo() {
        running = Mongod.instance().start(Version.Main.V8_0);
        this.port = running.current().getServerAddress().getPort();
    }

    @PreDestroy
    public void stopMongo() {
        if (running != null) {
            running.close();
        }
    }

    @Bean(name = "testMongoClient")
    @Primary
    public MongoClient testMongoClient() {
        return MongoClients.create("mongodb://" + host + ":" + port);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(testMongoClient(), "test");
    }
}
