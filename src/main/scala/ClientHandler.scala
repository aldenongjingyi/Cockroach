import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import java.net.Socket
import java.io.{BufferedReader, InputStreamReader, PrintWriter}

class ClientHandler(clientSocket: Socket, serverActor: ActorRef) {

  implicit val timeout: Timeout = Timeout(5.seconds)

  def handle(): Unit = {
    val clientAddress = s"${clientSocket.getInetAddress}:${clientSocket.getPort}"
    println(s"Accepted connection from $clientAddress")

    val reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
    val writer = new PrintWriter(clientSocket.getOutputStream, true)

    try {
      while (true) {
        print("Enter ISBN (type 'exit' to close connection): ")
        val isbn = reader.readLine()

        if (isbn == null || isbn.equalsIgnoreCase("exit")) {
          println(s"Closing connection with $clientAddress")
          return
        }

        val responseFuture: Future[Server.Response] = (serverActor ? Server.Request(isbn)).mapTo[Server.Response]
        val response = Await.result(responseFuture, timeout.duration)

        writer.println(s"The price for ISBN $isbn is $$ ${response.price}")
      }
    } catch {
      case e: Exception =>
        println(s"An error occurred with $clientAddress: ${e.getMessage}")
    } finally {
      reader.close()
      writer.close()
      clientSocket.close()
    }
  }
}  // Add this closing brace
