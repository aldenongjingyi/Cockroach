import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket
import akka.actor.ActorSelection
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class ClientHandler(clientSocket: Socket, serverActor: ActorSelection) {

  // Timeout and ExecutionContext
  implicit val timeout: Timeout = Timeout(5.seconds)
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  // Handle method to process client input
  def handle(): Unit = {
    // Client address information
    val clientAddress = s"${clientSocket.getInetAddress}:${clientSocket.getPort}"
    println(s"Accepted connection from $clientAddress")

    // BufferedReader and PrintWriter for communication with the client
    val reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
    val writer = new PrintWriter(clientSocket.getOutputStream, true)

    try {
      // Continuously handle client input
      while (true) {
        print("Press spacebar (type 'exit' to close connection): ")
        val userInput = reader.readLine()

        if (userInput == null || userInput.equalsIgnoreCase("exit")) {
          // Close connection if 'exit' is received
          println(s"Closing connection with $clientAddress")
          return
        }

        // Resolve the ActorRef from the ActorSelection with Timeout
        val resolvedServerActor = Await.result(serverActor.resolveOne(), timeout.duration)

        // Send message to the server and await the response
        val responseFuture: Future[Any] = (resolvedServerActor ? Server.SpacebarPressed).mapTo[Any]
        responseFuture.foreach {
          case Server.SpacebarCount(totalCount, countDifference) =>
            // Display the response from the server to the client
            writer.println(s"Server response: Spacebar pressed $totalCount times. Count difference: $countDifference")
          case other =>
            // Handle unexpected responses
            println(s"Unexpected response from server: $other")
        }
      }
    } catch {
      case e: Exception =>
        // Handle exceptions and display error messages
        println(s"An error occurred with $clientAddress: ${e.getMessage}")
    } finally {
      // Close resources
      reader.close()
      writer.close()
      clientSocket.close()
    }
  }
}
