package com.davidgj23.pekkotest

import com.davidgj23.pekkotest.api.routes.PekkoTestRoutes
import monix.execution.Scheduler
import org.apache.pekko.actor.{ActorSystem, Terminated}
import org.apache.pekko.http.scaladsl.Http

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App {
  private val systemName = "pekko-test"
  implicit val system: ActorSystem = ActorSystem(systemName)
  implicit val scheduler: Scheduler = Scheduler(system.dispatcher)
  registerActorSystemTermination()
  startApplication()

  private def startApplication(): Unit = {
    println("Starting the server in pekko-test microservice")
    val applicationPort = 8090
    val server = Http()
      .newServerAt(interface = "0.0.0.0", port = applicationPort)
      .bindFlow(new PekkoTestRoutes().routes)
    server.onComplete[Any] {
      case Success(binding) =>
        println(s"Http server started - Listening on: ${binding.localAddress}")
      case Failure(exception) =>
        println(s"There was an exception binding the http server $exception")
        terminateActorSystem
    }

    Runtime.getRuntime
      .addShutdownHook(new Thread(() => {
        server
          .flatMap(_.unbind())
          .onComplete(_ => terminateActorSystem)
      }))
  }

  private def registerActorSystemTermination(): Unit =
    system.registerOnTermination {
      println(s"ActorSystem $systemName terminated")
    }

  private def terminateActorSystem: Future[Terminated] =
    system.terminate()
}
