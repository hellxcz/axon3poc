package main.domain.parser

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.integration.core.PollableChannel

open class FeedReader
@Autowired constructor(val feedChannel: PollableChannel) {


    fun read() {

        for (i in 0..9) {
            // receive the message feed
            val message = feedChannel.receive(1000)
            if (message != null) {
                val entry = message.getPayload()
                // display

            } else {
                break
            }
        }

    }

}



