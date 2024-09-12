import sbt._
import sbtassembly.AssemblyPlugin.autoImport.assemblyMergeStrategy

name := "pekko-test"
ThisBuild / scalaVersion := "2.12.12"
ThisBuild / organization := "com.davidgj23"
ThisBuild / version := "1.0.0"
ThisBuild / useCoursier := true
ThisBuild / turbo := true
ThisBuild / useSuperShell := false
Global / onChangedBuildSource := ReloadOnSourceChanges

Revolver.settings.settings

ThisBuild / credentials += Credentials(new File("/tmp/credentials.properties"))

lazy val commonSettings = Seq(
  libraryDependencies ++= {
    val pekkoHttpVersion = "1.0.1"
    val pekkoVersion = "1.0.3"
    val sttpVersion = "1.7.2"
    val jacksonVersion = "2.15.2"
    val awsSdkVersion = "2.20.98"
    val circeVersion = "0.14.5"

    Seq(
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-caching" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-cors" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "org.apache.pekko" %% "pekko-actor" % pekkoVersion,
      "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
      "org.apache.pekko" %% "pekko-http-testkit" % pekkoHttpVersion % "test",
      "org.apache.pekko" %% "pekko-testkit" % pekkoVersion % Test,
      "org.mdedetrich" %% "pekko-http-circe" % "1.0.0",
      "com.github.swagger-akka-http" %% "swagger-pekko-http" % "2.12.2",
      "jakarta.ws.rs" % "jakarta.ws.rs-api" % "3.0.0",
      "io.swagger.core.v3" % "swagger-jaxrs2-jakarta" % "2.2.20",
      "com.typesafe" %% "ssl-config-core" % "0.6.1",
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-yaml" % "0.14.2",
      "io.monix" %% "monix" % "3.4.1",
      "org.scalamock" %% "scalamock" % "4.1.0" % "test",
      "org.scala-lang" % "scala-compiler" % "2.12.12"
    )
  },
  excludeDependencies ++= Seq(
    "log4j" % "log4j",
    "org.apache.logging.log4j" % "log4j-to-slf4j",
    "org.slf4j" % "log4j-over-slf4j",
    "ch.qos.logback" % "logback-core",
    "ch.qos.logback" % "logback-classic",
    "org.slf4j" % "jcl-over-slf4j",
    "org.slf4j" % "jul-to-slf4j"
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:experimental.macros",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint:private-shadow",
    "-Xlint:unsound-match",
    "-Xlint:constant",
    "-Yno-adapted-args",
    "-Ypartial-unification",
    "-Ywarn-dead-code",
    "-Ywarn-inaccessible",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-unused:imports",
    "-Ywarn-unused:locals",
    "-Ywarn-unused:params",
    "-Ywarn-unused:privates",
    "-Xfuture"
  ),
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "io.netty.versions.dtos") =>
      MergeStrategy.discard
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case PathList("META-INF", x) if x.endsWith(".DSA") || x.endsWith(".SF") =>
      MergeStrategy.discard
    case "reference.conf"   => MergeStrategy.concat
    case "application.conf" => MergeStrategy.concat
    case _                  => MergeStrategy.first
  },
  coverageMinimum := 75,
  coverageFailOnMinimum := true,
  coverageHighlighting := true,
  coverageExcludedPackages := "<empty>;.*Main;.*FLXConfig;.*ChubbConfig;.*CorsConfig;.*EventsConfig;.*OSLConfig;com.davidgj23.pekkotest.config.environment.*",
  wartremoverErrors ++= Warts.unsafe
)

lazy val ports = {
  project
    .enablePlugins(BuildInfoPlugin,
                   DockerPlugin,
                   JavaAppPackaging,
                   AshScriptPlugin)
    .settings(
      commonSettings,
      name := "ports",
      organization := "com.davidgj23.pekkotest",
      mainClass in (Compile, run) := Option("com.davidgj23.pekkotest.Main"),
      mainClass in assembly := Some("com.davidgj23.pekkotest.Main"),
      buildInfoKeys := Seq[BuildInfoKey](version),
      buildInfoPackage := "com.davidgj23.pekkotest",
      dockerUpdateLatest := true,
      dockerBaseImage := "345556555012.dkr.ecr.us-east-1.amazonaws.com/adoptopenjdk/openjdk11:rev-20230213.1"
    )
}

scalafmtOnCompile in ThisBuild := true

addCommandAlias(
  "validate",
  ";compile;scalafmt::test;coverage;test;coverageReport"
)
