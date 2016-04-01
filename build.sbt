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
  "org.scalafx" %% "scalafx" % "8.0.60-R9",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.2.2",
  "com.illposed.osc" % "javaosc-core" % "0.3")

addCompilerPlugin("org.scalamacros" % "paradise_2.11.8" % "2.1.0" cross CrossVersion.full)
