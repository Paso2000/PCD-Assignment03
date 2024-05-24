package it.unibo.pcd.akka.cluster.example.transformation

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import akka.serialization.*
import it.unibo.pcd.akka.cluster.example.CborSerializable

//#worker
object Worker:
  val WorkerServiceKey = ServiceKey[TransformText]("Worker")

  /** enum Command extends CborSerializable: case TransformText(text: String, replyTo: ActorRef[TextTransformed])
    *
    * Currently does not work
    */
  sealed trait Command
  case class TransformText(text: String, replyTo: ActorRef[TextTransformed]) extends Command with CborSerializable

  case class TextTransformed(text: String) extends CborSerializable

  def apply(): Behavior[Command] =
    Behaviors.setup { ctx =>
      // each worker registers themselves with the receptionist
      ctx.log.info(s"Registering myself (${ctx.self.path}) with receptionist")
      ctx.system.receptionist ! Receptionist.Register(WorkerServiceKey, ctx.self)

      Behaviors.receiveMessage { case TransformText(text, replyTo) =>
        replyTo ! TextTransformed(text.toUpperCase)
        Behaviors.same
      }
    }
