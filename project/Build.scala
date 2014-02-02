import sbt._
import Keys._
import play.Project._

object Build extends Build {

  val appName = "meatbros"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    "org.squeryl" % "squeryl_2.10" % "0.9.6-RC2",
  "com.github.tototoshi" %% "slick-joda-mapper" % "0.4.0",
    "commons-io" % "commons-io" % "2.4",
    "javax.mail" % "mail" % "1.4.7",
    "net.tanesha.recaptcha4j" % "recaptcha4j" % "0.0.7",
    "com.google.code.gson" % "gson" % "2.2.4",
    "securesocial" %% "securesocial" % "master-SNAPSHOT",
    "postgresql" % "postgresql" % "9.1-901.jdbc4"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += Resolver.url("play-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
  )

  playScalaSettings

}
