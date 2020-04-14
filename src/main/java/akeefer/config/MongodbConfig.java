package akeefer.config;

import akeefer.model.mongo.User;
import akeefer.repository.mongo.MongoUserRepository;
import com.mongodb.Mongo;
import com.mongodb.MongoClientURI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.UnknownHostException;

@Configuration
@EnableMongoRepositories(basePackageClasses = {MongoUserRepository.class, User.class})
@PropertySource("classpath:application.properties")
@Slf4j
public class MongodbConfig extends AbstractMongoConfiguration {

    @Value("${mongodb.user}")
    private String mongodbUser;
    @Value("${mongodb.password}")
    private String mongodbPassword;
    @Value("${mongodb.databasename}")
    private String mongodbDatabaseName;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    protected String getDatabaseName() {
        throw new UnsupportedOperationException("not needed, using #mongoDbFactory()");
    }

    @Override
    public Mongo mongo() {
        throw new UnsupportedOperationException("not needed, using #mongoDbFactory()");
    }

    @Bean
    @Override
    public MongoDbFactory mongoDbFactory() throws UnknownHostException {
        log.info("connecting to mongodb database {} as user {}", mongodbDatabaseName, mongodbUser);
        MongoClientURI uri = new MongoClientURI(
                "mongodb://" + mongodbUser + ":" + mongodbPassword + "@" +
                        "cluster0-shard-00-00-9dkn1.mongodb.net:27017," +
                        "cluster0-shard-00-01-9dkn1.mongodb.net:27017," +
                        "cluster0-shard-00-02-9dkn1.mongodb.net:27017" +
                        "/" + mongodbDatabaseName + "?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true&w=majority");
        return new SimpleMongoDbFactory(uri);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        return mongoTemplate;
    }
}
