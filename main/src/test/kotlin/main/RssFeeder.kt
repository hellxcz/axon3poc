package main

import org.junit.Test
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.integration.core.PollableChannel


class RssFeeder{

    @Test
    fun isRunning(){

        val context = ClassPathXmlApplicationContext(
                "/rss-inbound.xml")


        val feedChannel = context.getBean("feedChannel", PollableChannel::class.java)

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