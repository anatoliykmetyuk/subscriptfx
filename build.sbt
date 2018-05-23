lazy val homepage      = "https://github.com/scala-subscript/subscriptfx"
lazy val repositoryUrl = "git://github.com/scala-subscript/subscriptfx.git"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8"
, organization := "org.subscript-lang"
, version      := "0.0.2"

, libraryDependencies ++= Seq(
    "org.subscript-lang" %% "subscript-swing" % "3.0.5"
  , "org.scalafx" %% "scalafx" % "8.0.92-R10"
  )

, publishTo := {
    if (isSnapshot.value)
      Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/") 
    else
      Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
  }

, pomExtra :=
    <url>{homepage}</url>
    <licenses>
      <license>
        <name>GNU LGPL</name>
        <url>http://www.gnu.org/licenses/lgpl-3.0.en.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>{repositoryUrl}</url>
      <connection>{s"scm:$repositoryUrl"}</connection>
    </scm>
    <developers>
      <developer>
        <name>Andre van Delft</name>
        <url>https://github.com/AndreVanDelft</url>
      </developer>
      <developer>
        <id>anatoliykmetyuk</id>
        <name>Anatoliy Kmetyuk</name>
        <url>https://github.com/anatoliykmetyuk</url>
      </developer>
    </developers>
)

lazy val subscriptfx = (project in file("subscriptfx"))
  .dependsOn(macros)
  .settings(commonSettings)
  .settings(
    name := "subscriptfx"
  )

lazy val demos = (project in file("demos"))
  .dependsOn(subscriptfx)
  .settings(commonSettings).settings(
    fork := true  // JavaFX runtime cannot be launched more than once per session, so we make each run in a separate JVM
  , packagedArtifacts := Map.empty
  )

lazy val macros = (project in file("macros"))
  .settings(commonSettings).settings(
    name := "subscriptfx-macro"
  , libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.8"
  )

lazy val root = (project in file("."))
  .aggregate(subscriptfx, demos, macros)
  .settings(
    packagedArtifacts := Map.empty  // Don't publish root to maven
  )
