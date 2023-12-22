import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.io.StdIn

object Client {

  // Messages
  case object PressSpacebar
  case class SpacebarCount(totalCount: Int, countDifference: Int)

  // Spacebar Presser Actor
  class SpacebarPresserActor(serverActor: ActorRef)(implicit val timeout: Timeout) extends Actor {
    implicit val ec: ExecutionContext = context.dispatcher

    def receive: Receive = {
      case PressSpacebar =>
        // Send a message to the server actor for each spacebar press
        val response = (serverActor ? Server.SpacebarPressed).mapTo[SpacebarCount]
        response.foreach { spacebarCount =>
          // Display the response from the server for each spacebar press
          println(s"Server response: Spacebar pressed ${spacebarCount.totalCount} times by this player. Count difference: ${spacebarCount.countDifference}")
        }
    }
  }

  def main(args: Array[String]): Unit = {
    // Create an actor system
    implicit val system: ActorSystem = ActorSystem("ClientSystem")

    // Create the server actor
    val serverActor = system.actorOf(Props[Server.SpacebarCounterActor], "spacebarCounterActor")

    // Define the timeout duration
    implicit val timeout: Timeout = Timeout(5.seconds)

    // Create the client actor with the server actor reference and explicit timeout
    val clientActor = system.actorOf(Props(new SpacebarPresserActor(serverActor)(timeout)), "client1")

    // Continuously press the spacebar
    while (true) {
      print("Press spacebar (type 'exit' to quit): ")
      val userInput = scala.io.StdIn.readLine()

      if (userInput == null || userInput.equalsIgnoreCase("exit")) {
        return
      }

      // Press the spacebar for each input
      clientActor ! PressSpacebar
    }
  }
}
