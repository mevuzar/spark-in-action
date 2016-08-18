package com.spark_in_action.ch3

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source.fromFile

/**
 * @author yoav @since 8/14/16.
 */
object GitHubDay extends App{
  val conf = new SparkConf()
    //.setAppName("GitHub push counter")
    //.setMaster("local[*]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  val homeDir = System.getenv("HOME")
  //val inputPath = s"$homeDir/sia/github‐archive/2015‐03‐01‐0.json"
  val exerciseDataPath = args(0)//"/Users/yoav/sia/github‐archive"
  val ghLog = sqlContext.read.json(exerciseDataPath)
  val pushes = ghLog.filter("type = 'PushEvent'")
  pushes.printSchema
  println("all events: " + ghLog.count)
  println("only pushes: " + pushes.count)
  pushes.show(5)
  println("Aggregation:\n")
  val grouped = pushes.groupBy("actor.login").count
  grouped.show(5)
  println("Ordered:\n")
  val counts = grouped("count").desc
  val ordered = grouped.orderBy(counts)
  ordered.show(5)

  val employeesPath = exerciseDataPath + "/ghEmployees.txt"
  val employees = Set() ++ (for{
    line <- fromFile(args(1)).getLines
  } yield line.trim
  )

  println("Filtered(employees only):\n")
  import sqlContext.implicits._
  val bcEmployess = sc.broadcast(employees)
  val isEmp = (user: String) => bcEmployess.value.contains(user)
  val isEmployee = sqlContext.udf.register("SetContainsUdf", isEmp)
  val filtered = ordered.filter(isEmployee($"login"))
  filtered.show()

  filtered.write.format(args(3)).save(args(2))
}

//spark‐submit ‐‐class org.sia.chapter03App.GitHubDay ‐‐master local[*] ‐‐name "Daily GitHub
//Push Counter" chapter03App‐0.0.1‐SNAPSHOT.jar "$HOME/sia/github‐archive/*.json"
//"$HOME/sia/ghEmployees.txt" "$HOME/sia/emp‐gh‐push‐output" "json"