import java.net.{ServerSocket, Socket}
import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global

object Server {

  case class Request(isbn: String)
  case class Response(price: Double)

  class PriceLookupActor extends Actor {
    val priceMap: Map[String, Double] = Map(
      "123456789" -> 50.0,
      "987654321" -> 75.0,
      "111222333" -> 30.0,
      "444555666" -> 60.0,
      "777888999" -> 25.0,
      "112233445" -> 90.0,
      "556677889" -> 40.0,
      "998877665" -> 55.0,
      "223344556" -> 70.0,
      "667788990" -> 80.0
    )

    def receive: Receive = {
      case Request(isbn) =>
        val price = priceMap.getOrElse(isbn, 0.0)
        sender() ! Response(price)
    }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("ServerSystem")
    val priceLookupActor: ActorRef = system.actorOf(Props[PriceLookupActor], "priceLookupActor")

    val serverPort = 5555
    val serverSocket = new ServerSocket(serverPort) // Add this line

    println("Server is running. Waiting for clients...")

    // Display available ISBNs on the server
    println("Available ISBNs:")
    priceLookupActor ! Request("dummyISBN") // To trigger printing available ISBNs

    // Continuously accept client connections and handle them using Akka actors
    while (true) {
      val clientSocket = serverSocket.accept()
      Future {
        val clientHandler = new ClientHandler(clientSocket, priceLookupActor)
        clientHandler.handle()
      }
    }
  }
}
