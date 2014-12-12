package services.posts.actors

import akka.actor.{Actor, ActorLogging}
import services.messages.Messages.ZoneMessage
import services.posts.{LinksService, FetchContentFromRssLinks}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by hashcode on 2014/12/10.
 */
class PostFeedLinkContentActor extends Actor with ActorLogging {
  override def receive: Receive = {

    case ZoneMessage(zone) => {
      LinksService.getLatestLinks(zone) map (links => {
        if (links.size > 0) {
          links foreach (link => FetchContentFromRssLinks.getContent(link))
        }
      })
    }
  }
}
