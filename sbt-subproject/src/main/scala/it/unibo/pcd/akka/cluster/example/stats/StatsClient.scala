/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */
package it.unibo.pcd.akka.cluster.example.stats

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration.*
import scala.util.Random

object StatsClient:

  enum Event:
    case Tick
    case ServiceResponse(result: StatsService.Response)

  import Event.*

  def apply(service: ActorRef[StatsService.ProcessText]): Behavior[Event] =
    Behaviors.setup { ctx =>
      Behaviors.withTimers { timers =>
        timers.startTimerWithFixedDelay(Tick, Tick, 2.seconds)
        val responseAdapter = ctx.messageAdapter(ServiceResponse)

        Behaviors.receiveMessage {
          case Tick =>
            val text = Random
              .shuffle("this is the text that will be analyzed".split(" ").toList)
              .take(1 + Random.nextInt(10))
              .mkString(" ")
            val req = StatsService.ProcessText(text, responseAdapter)
            ctx.log.info(s"Sending process request $req")
            service ! req
            Behaviors.same
          case ServiceResponse(result) =>
            ctx.log.info("Service result: {}", result)
            Behaviors.same
        }
      }
    }
