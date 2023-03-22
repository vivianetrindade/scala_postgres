package com.vivianetrindade

import java.time.LocalDate
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object PrivateExecutionContext {
  val executor = Executors.newFixedThreadPool(4)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executor)
}
object Main {
  import slick.jdbc.PostgresProfile.api._
  import PrivateExecutionContext._

  val viviane = Person(1, "Viviane", "Vasconcelos", "Female", LocalDate.of(1985, 6, 1))
  val isabela = Person(2, "Isabela", "Vasconcelos", "Female", LocalDate.of(2005, 3, 26))

  def insertPerson(person: Person): Unit = {
    println(person.personId)
    val queryDescription = SlickTables.personTable.returning(SlickTables.personTable) += person
    val futureId: Future[Person] = Connection.db.run(queryDescription)

    futureId.onComplete {
      case Failure(exception) => println(s"Query failed, reason $exception")
      case Success(value) => println(s"Query was successful, new id is $value")
    }

    Thread.sleep(10000)
  }

  def getAllPeople(): Unit = {
    val resultFuture: Future[Seq[Person]] = Connection.db.run(SlickTables.personTable.result) // SELECT * FROM Person

    resultFuture.onComplete {
      case Failure(exception) => println(s"Fetched failed: $exception")
      case Success(people) => println(s"Fetched people: ${people.mkString(", ")}")
    }
    Thread.sleep(5000)
  }
  def findPersonByName(firstName: String): Unit = {
    val futurePerson: Future[Seq[Person]] = Connection.db.run(SlickTables.personTable.filter(_.firstName.like(firstName)).result)

    futurePerson.onComplete {
      case Failure(exception) => println(s"Fetched failed: $exception")
      case Success(people) => println(s"Fetched people: ${people.mkString(", ")}")
    }
    Thread.sleep(5000)
  }

  def updatePerson(personId: Int, person: Person): Unit = {
    val updateQuery = SlickTables.personTable.filter(_.personId === personId).update(person)
    val resultFuture = Connection.db.run(updateQuery)

    resultFuture.onComplete {
      case Failure(exception) => println(s"Query failed, reason $exception")
      case Success(value) => println(s"Query was successful, updated $value")
    }

    Thread.sleep(10000)
  }

  def deletePerson(personId: Int): Unit = {
    val deleteQuery = SlickTables.personTable.filter(_.personId === personId).delete
    val resultFuture = Connection.db.run(deleteQuery)

    resultFuture.onComplete {
      case Failure(exception) => println(s"Query failed, reason $exception")
      case Success(value) => println(s"Query was successful, deleted $value")
    }

    Thread.sleep(10000)
  }


  def main(args: Array[String]): Unit = {
    deletePerson(2)
  }
}