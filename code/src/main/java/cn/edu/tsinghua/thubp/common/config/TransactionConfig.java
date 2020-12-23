package cn.edu.tsinghua.thubp.common.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "cn.edu.tsinghua.thubp")
class TransactionConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.database}")
    private String databaseName;
    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Primary
    @Bean("simpleMongoDatabaseFactory")
    MongoDatabaseFactory simpleMongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(connectionString);
    }

    @Profile("prod")
    @Bean
    MongoTransactionManager transactionManager(@Qualifier("simpleMongoDatabaseFactory") MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @NotNull
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
}

