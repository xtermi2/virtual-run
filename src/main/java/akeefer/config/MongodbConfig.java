package akeefer.config;

import akeefer.model.mongo.User;
import akeefer.repository.mongo.MongoUserRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
@EnableMongoRepositories(basePackageClasses = {MongoUserRepository.class, User.class})
@PropertySource("classpath:application.properties")
@Slf4j
public class MongodbConfig extends AbstractMongoClientConfiguration {

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
        return mongodbDatabaseName;
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(
                "mongodb+srv://" + mongodbUser + ":" + mongodbPassword + "@cluster0-9dkn1.mongodb.net/?retryWrites=true&w=majority");
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Bean
    @Override
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = super.mongoTemplate();
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        return mongoTemplate;
    }

    @Bean
    @Override
    public CustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                BigDecimalToDecimal128Converter.INSTANCE,
                Decimal128ToBigDecimalConverter.INSTANCE
        ));
    }

    @WritingConverter
    public enum BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {
        INSTANCE;

        @Override
        public Decimal128 convert(BigDecimal bigDecimal) {
            return new Decimal128(bigDecimal);
        }
    }

    @ReadingConverter
    public enum Decimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {
        INSTANCE;

        @Override
        public BigDecimal convert(Decimal128 decimal128) {
            return decimal128.bigDecimalValue();
        }
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
