import play.ebean.sbt.PlayEbean

name := """pay"""

version := "1.0-SNAPSHOT"

resolvers += "osChina" at "http://maven.oschina.net/content/groups/public/"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "com.alibaba" % "fastjson" % "1.2.5"
)

libraryDependencies += "org.jdom" % "jdom2" % "2.0.5"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5"

libraryDependencies += "org.bouncycastle" % "bcprov-jdk16" % "1.46"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "log4j" % "log4j" % "1.2.17"

libraryDependencies += specs2 % Test

javaOptions in Test ++= Seq(
  "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9998",
  "-Xms512M",
  "-Xmx1536M",
  "-Xss1M",
  "-XX:MaxPermSize=384M"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
