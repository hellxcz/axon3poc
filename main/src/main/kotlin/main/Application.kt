package main

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean


@SpringBootApplication
//@EnableAutoConfiguration()
@EnableAutoConfiguration(exclude = arrayOf(DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class))
open class Application : SpringBootServletInitializer() {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }

    @Bean
    open fun mongoConfiguration(
            @Value("\${mongoConnection.host}") hostNames: List<String>,
            @Value("\${mongoConnection.dbName}") databaseName: String,
            @Value("\${mongoConnection.port}") port: Int?,
            @Value("\${mongoConnection.userName}") userName: String?,
            @Value("\${mongoConnection.password}") password: String?
    ): MongoConfiguration {

        return MongoConfiguration(
                hostNames = hostNames,
                databaseName = databaseName,
                port = port,
                userName = userName,
                password = password);

    }

    data class MongoConfiguration(
            val hostNames: List<String>,
            val databaseName: String,
            val port: Int?,
            val userName: String?,
            val password: String?
    );

    @Bean
    open fun mongoClient(
            mongoConfiguration: MongoConfiguration): MongoClient {

        val defaultPort = 27017

        val serverAddresses = mongoConfiguration.hostNames.map { ServerAddress(it, mongoConfiguration.port ?: defaultPort) }

        if (mongoConfiguration.userName != null && mongoConfiguration.password != null) {
            val credential = MongoCredential.createCredential(
                    mongoConfiguration.userName, mongoConfiguration.databaseName, mongoConfiguration.password.toCharArray())

            val credentialList = arrayListOf(credential);

            return MongoClient(serverAddresses, credentialList);

        }

        val mongoClient = MongoClient(serverAddresses)

        return mongoClient
    }

    @Bean
    open fun morphia(): Morphia {

        val morphia = Morphia();

        val converters = morphia.mapper.converters;

        return morphia;
    }

    @Bean
    open fun datastore(morphia: Morphia,
                       mongoClient: MongoClient,
                       @Value("\${mongoConnection.dbName}") databaseName: String): Datastore {

        val datastore = morphia.createDatastore(mongoClient, databaseName)

        datastore.ensureIndexes()

        return datastore;

    }
}