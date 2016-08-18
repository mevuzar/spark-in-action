name := "spark-in-action"

version := "1.0"

scalaVersion := "2.11.8"

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "org.sia",
  scalaVersion := "2.11.8",
  test in assembly := {}
)

lazy val sia = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("com.spark_in_action.ch3.App"),
    assemblyJarName in assembly := "meine.jar",
    packageOptions in assembly ~= (_.filterNot(po => po.isInstanceOf[Package.MainClass])),
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
      case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
      case PathList("org", "apache", xs @ _*) => MergeStrategy.last
      case PathList("com", "google", xs @ _*) => MergeStrategy.last
      case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
      case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
      case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
      case "about.html" => MergeStrategy.rename
      case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
      case "META-INF/mailcap" => MergeStrategy.last
      case "META-INF/mimetypes.default" => MergeStrategy.last
      case "plugin.properties" => MergeStrategy.last
      case "log4j.properties" => MergeStrategy.last
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )


libraryDependencies ++= Seq(
  sparkModule("org.apache.spark" % "spark-core_2.11" % "1.6.1"),
  sparkModule("org.apache.spark" % "spark-sql_2.11" % "1.6.1"),
  //  "org.specs2" % "specs2_2.11" % "3.7" pomOnly(),
  //  //"junit" % "junit" % "4.12",
  //  "org.scalatest" % "scalatest_2.11" % "3.0.0",
  //  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  //  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  //  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.apache.commons" % "commons-email" % "1.3.1"
)


def sparkModule(id: ModuleID) = {
  id.exclude ("org.mortbay.jetty", "servlet-api").
    exclude("commons-beanutils", "commons-beanutils-core").
    exclude("commons-collections", "commons-collections").
    exclude("commons-logging", "commons-logging").
    exclude("com.esotericsoftware.minlog", "minlog")
}