import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.io.StdIn

object Client {

  case class ServerResponse(price: Double)

  class ServerCommunicatorActor(serverActor: ActorRef) extends Actor {
    def receive: Receive = {
      case userInput: String =>
        serverActor ! Server.Request(userInput)
      case Server.Response(price) =>
        println(s"Server response: $price")
    }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("ClientSystem")
    val serverActor = system.actorOf(Props[Server.PriceLookupActor], "serverActor")
    val clientActor = system.actorOf(Props(new ServerCommunicatorActor(serverActor)), "clientActor")

    // Continuously prompt the user to enter ISBNs and send requests to the server
    while (true) {
      print("Enter ISBN (type 'exit' to quit): ")
      val userInput = StdIn.readLine()

      if (userInput == null || userInput.equalsIgnoreCase("exit")) {
        return
      }

      clientActor ! userInput
    }
  }
}
