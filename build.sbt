lazy val commonSettings = Seq(
  scalaVersion := "2.11.7"
, libraryDependencies ++= Seq(
    "org.subscript-lang" %% "subscript-swing" % "3.0.3"
  , "org.scalafx" %% "scalafx" % "8.0.60-R9"
  )
)

lazy val subscriptfx = (project in file("subscriptfx")).settings(commonSettings)

lazy val demos       = (project in file("demos"))
  .dependsOn(subscriptfx)
  .settings(commonSettings)
  .settings(
    fork := true  // JavaFX runtime cannot be launched more than once per session, so we make each run in a separate JVM
  )