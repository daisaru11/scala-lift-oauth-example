name := "oauthexample"
 
scalaVersion := "2.9.1"
 
seq(webSettings: _*)

resolvers ++= Seq(
  "Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
)


libraryDependencies ++= {
  val liftVersion = "2.4" // Put the current/latest lift version here
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-wizard" % liftVersion % "compile->default")
}

// Customize any further dependencies as desired
libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-webapp" % "8.0.4.v20111024" % "container", // For Jetty 8
  "net.databinder" %% "dispatch-http-json" % "0.8.8", //OAuth
  "net.databinder" %% "dispatch-oauth" % "0.8.8",
  "org.scala-tools.testing" % "specs_2.9.0" % "1.6.8" % "test", // For specs.org tests
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "org.slf4j" % "slf4j-log4j12" % "1.6.4"
)
