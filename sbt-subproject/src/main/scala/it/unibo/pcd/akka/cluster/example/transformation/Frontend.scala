package it.unibo.pcd.akka.cluster.example.transformation

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.util.Timeout
import it.unibo.pcd.akka.cluster.example.transformation.Worker

import scala.concurrent.duration.*
import scala.util.{Failure, Success}

//#frontend
object Frontend:
  import Worker.*
  enum Event:
    private[Frontend] case Tick
    private[Frontend] case WorkersUpdated(newWorkers: Set[ActorRef[TransformText]])
    private[Frontend] case TransformCompleted(originalText: String, transformedText: String)
    private[Frontend] case JobFailed(why: String, text: String)

  import Event.*
  def apply(): Behavior[Event] = Behaviors.setup { ctx =>
    Behaviors.withTimers { timers =>
      // subscribe to available workers
      val subscriptionAdapter = ctx.messageAdapter[Receptionist.Listing] {
        case Worker.WorkerServiceKey.Listing(workers) =>
          WorkersUpdated(workers)
      }
      ctx.system.receptionist ! Receptionist.Subscribe(Worker.WorkerServiceKey, subscriptionAdapter)

      timers.startTimerWithFixedDelay(Tick, Tick, 2.seconds)

      running(ctx, IndexedSeq.empty, jobCounter = 0)
    }
  }

  private def running(
      ctx: ActorContext[Event],
      workers: IndexedSeq[ActorRef[TransformText]],
      jobCounter: Int
  ): Behavior[Event] =
    Behaviors.withTimers { timers =>
      Behaviors.receiveMessage {
        case WorkersUpdated(newWorkers) =>
          ctx.log.info("List of services registered with the receptionist changed: {}", newWorkers)
          running(ctx, newWorkers.toIndexedSeq, jobCounter)
        case Tick =>
          if (workers.isEmpty) {
            ctx.log.warn("Got tick request but no workers available, not sending any work")
            Behaviors.same
          } else {
            // how much time can pass before we consider a request failed
            given Timeout = 5.seconds
            val selectedWorker = workers(jobCounter % workers.size)
            ctx.log.info("Sending work for processing to {}", selectedWorker)
            val text = s"hello-$jobCounter"
            // Ask pattern: ask `selectedWorker` to perform request specified in second argument (through a function of the actor that will receive the response)
            ctx.ask[TransformText, TextTransformed](selectedWorker, TransformText(text, _)) { // we map the Try[Res] to a message this actor understands
              case Success(transformedText) =>
                TransformCompleted(transformedText.text, text)
              case Failure(ex) => JobFailed("Processing timed out", text)
            }
            if (jobCounter == 5) timers.cancelAll()
            running(ctx, workers, jobCounter + 1)
          }
        case TransformCompleted(originalText, transformedText) =>
          ctx.log.info("Got completed transform of {}: {}", originalText, transformedText)
          Behaviors.same

        case JobFailed(why, text) =>
          ctx.log.warn("Transformation of text {} failed. Because: {}", text, why)
          Behaviors.same
      }
    }
//#frontend
