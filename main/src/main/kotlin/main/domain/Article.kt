package main.domain

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.commandhandling.model.AggregateRoot
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EventStore
import org.springframework.stereotype.Component

@AggregateRoot
class Article {

    @AggregateIdentifier
    lateinit var id: String;

    lateinit var title: String;

    constructor() {
    }

    @CommandHandler
    constructor(cmd: CreateArticleCommand) {

        apply(
                ArticleCreatedEvent(
                        id = cmd.id,
                        title = cmd.title
                )
        )
    }

    @EventSourcingHandler
    fun on(evt:ArticleCreatedEvent){
        id = evt.id;
        title = evt.title;
    }

    data class CreateArticleCommand
    constructor(
            @TargetAggregateIdentifier val id: String,
            val title: String
    )

    data class ArticleCreatedEvent
    constructor(val id: String, val title: String)
}

interface ArticleCommandGateway{

    fun createArticle(cmd:Article.CreateArticleCommand);

}

@Component
open class ArticleESRepository : EventSourcingRepository<Article>{
    constructor(eventStore: EventStore) : super(Article::class.java, eventStore) {

    }
}

@Component
open class ArticleQueryModel{



    @EventHandler
    fun on(evt:Article.ArticleCreatedEvent){

    }

}


