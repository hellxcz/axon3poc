package main

import main.domain.Article
import main.domain.ArticleCommandGateway
import main.domain.ArticleESRepository
import main.domain.ArticleQueryModel
import org.axonframework.commandhandling.AggregateAnnotationCommandHandler
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.gateway.CommandGatewayFactory
import org.axonframework.config.DefaultConfigurer
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.eventhandling.*
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.function.Consumer
import java.util.function.Function

@Configuration
//@EnableAxon
@Import(Article::class)
open class AxonSpringConfiguration {

    @Bean
    open fun eventStorageEngine(): EventStorageEngine {
        return InMemoryEventStorageEngine()
    }

    @Bean
    open fun commandBus(): CommandBus {
        return SimpleCommandBus();
    }

    @Bean
    open fun eventBus(eventStore:EventStore): EventBus {
        return eventStore;
    }

    @Bean
    open fun gatewayGatewayFactory(commandBus: CommandBus): CommandGatewayFactory {

        val gatewayGatewayFactory = CommandGatewayFactory(commandBus)

//        gatewayProxyFactory.registerCommandCallback(object : CommandCallback<Any> {
//            fun onSuccess(result: Any) {
//
//            }
//
//            fun onFailure(cause: Throwable) {
//                logger.error("Error during processing command.", cause)
//            }
//        })


        return gatewayGatewayFactory
    }

    @Bean
    open fun articleCommandGateway(factory:CommandGatewayFactory):ArticleCommandGateway{
        return factory.createGateway(ArticleCommandGateway::class.java)

    }


//    @Bean
//    open fun eventStorageEngine(mongoTemplate: MongoTemplate):EventStorageEngine{
//
//        return MongoEventStorageEngine(mongoTemplate);
//
//    }

    @Bean
    open fun eventStore(eventStorageEngine: EventStorageEngine): EventStore {
        return EmbeddedEventStore(eventStorageEngine);
    }
}

@Component
open class AxonApplicationPreparedListener
@Autowired constructor(
        val commandBus: CommandBus,
        val eventBus: EventBus,
        val articleESRepository: ArticleESRepository,
        val articleQueryModel: ArticleQueryModel)
{

    @EventListener
    fun handleContextRefresh(event: ContextRefreshedEvent){

        subscribeToCommandBus(commandBus, Article::class.java, articleESRepository);

        val simpleEventHandlerInvoker = SimpleEventHandlerInvoker(articleQueryModel)

        val processor = SubscribingEventProcessor("default", simpleEventHandlerInvoker, eventBus )

        processor.start()

    }

    fun <T> subscribeToCommandBus(commandBus: CommandBus, clazz: Class<T>, esRepository: EventSourcingRepository<T>) {

        val subscribe = AggregateAnnotationCommandHandler(clazz, esRepository).subscribe(commandBus);

    }

}