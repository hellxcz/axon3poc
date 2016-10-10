package main

import com.mongodb.MongoClient
import de.svenkubiak.embeddedmongodb.EmbeddedMongo
import main.domain.Article
import main.domain.ArticleCommandGateway
import main.domain.ArticleESRepository
import org.apache.commons.codec.binary.Base64
import org.axonframework.commandhandling.AggregateAnnotationCommandHandler
import org.axonframework.commandhandling.CommandBus
import org.axonframework.eventsourcing.EventSourcingRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import java.net.URI

@SpringBootTest()
@RunWith(SpringRunner::class)
//@ContextConfiguration(classes = arrayOf(MainTest.AppConfigLocal::class))
//@EnableAutoConfiguration(exclude = arrayOf(DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class))
class MainTest {

//    @EnableAutoConfiguration(exclude = arrayOf(DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class))
//    open class AppConfigLocal : Application() {
//
//        var fakeMongoClient:FakeMongoClient = FakeMongoClient()
//
//        @Bean
//        override fun mongoClient(
//                mongoConfiguration: MongoConfiguration): MongoClient{
//
//            return fakeMongoClient.mongo
//
//        }
//
//        @Bean
//        override fun morphia(): Morphia {
//
//            return fakeMongoClient.morphia
//        }
//
//        @Bean
//        override open fun datastore(morphia: Morphia,
//                           mongoClient: MongoClient,
//                           databaseName: String): Datastore {
//
//            return fakeMongoClient.datastore
//        }
//    }

    @Autowired
    lateinit var commandBus: CommandBus;

    @Autowired
    lateinit var articleCommandGateway: ArticleCommandGateway

    @Autowired
    lateinit var articleESRepository: ArticleESRepository

    @Test
    fun some() {

        val id = "123"
        val title = "title"

        articleCommandGateway.createArticle(
                Article.CreateArticleCommand(id = id, title = title)
        )
    }

    @Test
    fun getTheRest(){

        //http://docs.intrinio.com/#master-data-feed

//        val url = URI.create("https://api.intrinio.com/companies?ticker=AAPL")
//        val url = URI.create("https://api.intrinio.com/historical_data?identifier=AAPL&item=totalrevenue")
        val url = URI.create("https://api.intrinio.com/historical_data?identifier=\$GDP&item=level")

        val plainCreds = "876f6e6c27a4053fdae57ecc2007037a:7f2447d5c50ff553680ce094cd30af72"
        val plainCredsBytes = plainCreds.toByteArray()
        val base64CredsBytes = Base64.encodeBase64(plainCredsBytes)
        val base64Creds = String(base64CredsBytes)

        val headers = HttpHeaders()
        headers.add("Authorization", "Basic " + base64Creds)

        val restTemplate = RestTemplate()

        val requestEntity = RequestEntity<String>(headers, HttpMethod.GET, url)

        val clazz = object: ParameterizedTypeReference<String>(){};

        val responseEntity = restTemplate.exchange(requestEntity, clazz)


    }

    fun <T> subscribeToCommandBus(commandBus: CommandBus, clazz: Class<T>, esRepository: EventSourcingRepository<T>) {


        val subscribe = AggregateAnnotationCommandHandler(clazz, esRepository).subscribe(commandBus);

    }
}