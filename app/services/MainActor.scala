package services

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.routing.RoundRobinPool
import services.feeds.actors.{CustomFeedsActor, FeedsActor, SocialMediaFeedsActor}
import services.messages.Messages.{StartMessage, ZoneMessage}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by hashcode on 2014/12/09.
 */
class MainActor extends Actor {
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case StartMessage(start) => {
      //      val latestLinks = context.actorOf(Props[GetLinksActor])
      //      val socialMediaLinks = context.actorOf(Props[GetSocialMediaLiksActor])
      //      val customLinks = context.actorOf(Props[GetCustomLinksActor])
      //      val fetchCustomLinks = context.actorOf(Props[FetchCustomLinksActor])
      val feedsActor = context.actorOf(Props[FeedsActor].withRouter(RoundRobinPool(nrOfInstances = 5)))
      val customFeedsActor = context.actorOf(Props[CustomFeedsActor].withRouter(RoundRobinPool(nrOfInstances = 5)))
      val SocialMediaFeedsActor = context.actorOf(Props[SocialMediaFeedsActor].withRouter(RoundRobinPool(nrOfInstances = 5)))

      ZoneService.getZones map (zones => zones foreach (
        zone => {
          //          latestLinks ! Zone(zone.code)
          //          socialMediaLinks ! Zone(zone.code)
          SocialMediaFeedsActor ! ZoneMessage(zone.code)
          customFeedsActor ! ZoneMessage(zone.code)
          feedsActor ! ZoneMessage(zone.code)
        })
        )
    }
    case _ => log.info("received unknown message")
  }

}
