//import scala.collection.mutable.ArrayBuffer
//import scala.io.Source
//import scala.util.Random
//import scala.io.StdIn.readLine
//
//object RandomWordGame extends App {
//  def readFileFromResources(filename: String): List[String] = {
//    val source = Source.fromInputStream(getClass.getResourceAsStream(filename))
//    val words = source.getLines().toList
//    source.close()
//    words
//  }
//
//  def pickRandomWords(words: List[String], numWords: Int): ArrayBuffer[String] = {
//    val myList = Random.shuffle(words).take(numWords)
//    var buf = ArrayBuffer(myList: _*)
//
//    buf
//  }
//
//  def playGame(randomWords: ArrayBuffer[String]): Unit = {
//    println("Random words:")
//    randomWords.foreach(println)
//
//    while (randomWords.length > 0){
//      print("Type a word: ")
//      val userInput = readLine()
//
//      if (randomWords.contains(userInput)) {
//        println(s"Correct! Removing '$userInput'")
//        randomWords -= userInput
//        println("Remaining words:")
//        randomWords.foreach(println)
//      } else {
//        println("Incorrect! The word is not in the list.")
//      }
//    }
//  }
//
//  val filename: String = "/words.txt"
//  val wordsFromFile: List[String] = readFileFromResources(filename)
//  var randomWords: ArrayBuffer[String] = pickRandomWords(wordsFromFile, 5)
//  playGame(randomWords)
//}

import scala.io.Source
import scala.util.Random
import scala.io.StdIn.readLine

object DynamicWordGame extends App {
  def readFileFromResources(filename: String): List[String] = {
    val source = Source.fromInputStream(getClass.getResourceAsStream(filename))
    val words = source.getLines().toList
    source.close()
    words
  }

  def pickRandomWords(words: List[String], numWords: Int): List[String] = {
    Random.shuffle(words).take(numWords)
  }

  def playGame(remainingWords: List[String], allWords: List[String]): List[String] = {
    if (remainingWords.isEmpty) {
      println("No more words to guess. Exiting the game.")
      List.empty
    } else {
      println("Random words:")
      remainingWords.foreach(println)

      print("Type one of the words or press Enter to exit: ")
      val userInput = readLine()

      if (userInput.isEmpty) {
        println("Exiting the game.")
        List.empty
      } else if (remainingWords.contains(userInput)) {
        println(s"Correct! Removing '$userInput'")
        val updatedWords = remainingWords.filterNot(_ == userInput)
        if (allWords.nonEmpty) {
          val newWord = Random.shuffle(allWords).head
          println(s"Adding a new word: '$newWord'")
          val updatedWordsWithNew = updatedWords :+ newWord
//          println("Remaining words:")
//          updatedWordsWithNew.foreach(println)
          updatedWordsWithNew
        } else {
          println("No more words available to add.")
          updatedWords
        }
      } else {
        println("Incorrect! The word is not in the list.")
        playGame(remainingWords, allWords)
      }
    }
  }

  val filename: String = "/words.txt"
  val wordsFromFile: List[String] = readFileFromResources(filename)
  var remainingWords: List[String] = pickRandomWords(wordsFromFile, 5)

  // Loop to keep playing the game until no more words or user exits
  while (remainingWords.nonEmpty) {
    remainingWords = playGame(remainingWords, wordsFromFile)
  }
}