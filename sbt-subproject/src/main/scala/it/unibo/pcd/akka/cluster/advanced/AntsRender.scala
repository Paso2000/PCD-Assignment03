package it.unibo.pcd.akka.cluster.advanced

import akka.actor.typed.receptionist.ServiceKey
import akka.actor.typed.ActorRef
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import it.unibo.pcd.akka.Message

import concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps

object AntsRender:
  // Constants
  val width = 800
  val height = 600
  //rappresenta un comando che l'attore puo ricevere
  sealed trait Command
  final case class Render(x: Int, y: Int, id: ActorRef[_]) extends Message with Command

  //un oggetto che estende Command. Viene utilizzato come messaggio privato per aggiornare periodicamente la GUI
  private case object Flush extends Command // Private message (similar to a private method in OOP)

  //chiave di servizio per il sistema di receptionist di Akka
  //che consente la registrazione e la ricerca di attori.
  val Service = ServiceKey[Render]("RenderService")

  //comportamento dell'attore
  def apply(frameRate: Double = 60): Behavior[Command] = {
    Behaviors.setup { ctx =>
      val frontendGui = SimpleGUI(width, height) // init the gui
      Behaviors.withTimers { timers =>
        timers.startTimerAtFixedRate(Flush, ((1 / frameRate) * 1000).toInt milliseconds)
        var toRender: Map[ActorRef[_], (Int, Int)] = Map.empty

        //registra l'attore corrente presso il receptionist di Akka,
        //permettendo ad altri attori di trovarlo tramite il servizio Service
        ctx.system.receptionist ! Receptionist.Register(Service, ctx.self)

        Behaviors.receiveMessage {
          case Render(x, y, id) =>
            ctx.log.info(s"render.. $id")
            toRender = toRender + (id -> (x, y))
            frontendGui.render(toRender.values.toList)
            Behaviors.same

          case Flush =>
            frontendGui.render(toRender.values.toList)
            Behaviors.same
        }
      }
    }
  }
