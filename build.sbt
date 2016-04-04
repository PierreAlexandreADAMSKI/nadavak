name := "ScalaTest"

version := "1.0"

scalaVersion := "2.11.8"

resourceDirectory in Compile := (scalaSource in Compile).value

libraryDependencies ++= Seq(
  "de.sciss" %% "scalacollider" % "1.18.1",
  "de.sciss" % "scalacolliderugens-spec" % "1.14.1",
  "de.sciss" %% "scalacolliderugens-api" % "1.14.1",
  "de.sciss" %% "scalacolliderugens-core" % "1.14.1",
  "de.sciss" %% "scalacolliderugens-plugins" % "1.14.1",
  "com.illposed.osc" % "javaosc-core" % "0.3")

