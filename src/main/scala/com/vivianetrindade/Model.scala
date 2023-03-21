package com.vivianetrindade

import slick.lifted.ProvenShape

import java.time.LocalDate

case class Person(id: Int, firstName: String, lastName: String, gender: String, dateOfBirth: LocalDate)

object SlickTables{
  import slick.jdbc.PostgresProfile.api._

  class PersonTable(tag: Tag) extends Table[Person](tag, Some("people"), "Person"){
    def id = column[Int]("person_id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def gender = column[String]("gender")
    def dateOfBirth = column[LocalDate]("date_of_birth")

    //mapping function to the case class
    override def * = (id,firstName,lastName,gender,dateOfBirth) <> (Person.tupled, Person.unapply)

    //"API entry point"
    lazy val personTable = TableQuery[PersonTable]

  }
}
