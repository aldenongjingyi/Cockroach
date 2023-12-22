import akka.actor.{Actor, ActorSystem, Props}

object Server extends App {

  // Messages
  case object SpacebarPressed
  case class SpacebarCount(totalCount: Int, countDifference: Int)
  case class GetSpacebarCount(clientId: Int)

  // Spacebar Counter Actor
  class SpacebarCounterActor extends Actor {
    var client1Count: Int = 0
    var client2Count: Int = 0

    def receive: Receive = {
      case SpacebarPressed =>
        val clientActor = sender()
        clientActor ! "Spacebar pressed."

        // Increment the count based on client ID
        if (clientActor.path.name == "client1") {
          client1Count += 1
          clientActor ! SpacebarCount(client1Count, client1Count - client2Count)
        } else {
          client2Count += 1
          clientActor ! SpacebarCount(client2Count, client1Count - client2Count)
        }
      case GetSpacebarCount(clientId) =>
        sender() ! SpacebarCount(if (clientId == 1) client1Count else client2Count, client1Count - client2Count)
    }
  }

  // Create an actor system
  val system: ActorSystem = ActorSystem("ServerSystem")

  // Create the spacebarCounterActor
  val spacebarCounterActor = system.actorOf(Props[SpacebarCounterActor], "spacebarCounterActor")

  // Keep the application running
  println("Server is running. Press Enter to terminate the server...")
  scala.io.StdIn.readLine()

  // Terminate the actor system
  system.terminate()
}
