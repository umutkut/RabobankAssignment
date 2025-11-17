package nl.rabobank.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Configuration
@EnableMongoRepositories(basePackages = "nl.rabobank.mongo.repository")
public class EmbeddedMongoTestConfiguration extends AbstractMongoClientConfiguration {

    private int port;

    @Override
    @NonNull
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    @NonNull
    protected Collection<String> getMappingBasePackages() {
        return Set.of("nl.rabobank.mongo.documents");
    }

    @Bean(destroyMethod = "stop")
    public MongodProcess embeddedMongoProcess() throws IOException {
        this.port = Network.freeServerPort(Network.getLocalHost());

        MongodConfig mongodConfig = MongodConfig.builder()
                .version(Version.Main.V4_4)
                .net(new Net("localhost", port, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodExecutable executable = starter.prepare(mongodConfig);
        return executable.start();
    }

    @Bean(destroyMethod = "close")
    @Override
    @NonNull
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:" + port + "/test");
    }
}
