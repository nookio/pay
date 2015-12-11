import _root_.sbt.Keys._

name := """pay"""

version := "1.0-SNAPSHOT"

resolvers += "osChina" at "http://maven.oschina.net/content/groups/public/"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
