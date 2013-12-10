import AssemblyKeys._

assemblySettings

name := "moviedemo"

version := "1.0"

organization := "edu.berkeley.cs.amplab"

scalaVersion := "2.9.3"

libraryDependencies ++= Seq(
//  "org.eclipse.jetty" % "jetty-server" % "7.6.8.v20121106",
  "org.eclipse.jetty.orbit" % "javax.servlet" % "2.5.0.v201103041518" artifacts Artifact("javax.servlet", "jar", "jar"),
  "org.apache.spark" % "spark-core_2.9.3" % "0.8.0-incubating",
  "org.apache.spark" % "spark-mllib_2.9.3" % "0.8.0-incubating",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

resolvers ++= Seq(
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases",
  "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/",
  "ScalaNLP Maven2" at "http://repo.scalanlp.org/repo",
  "Spray" at "http://repo.spray.cc"
)

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("javax", "servlet", xs @ _*)           => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".html"   => MergeStrategy.first
    case "application.conf"                              => MergeStrategy.concat
    case m if m.toLowerCase.endsWith("manifest.mf")      => MergeStrategy.discard
    case m if m.toLowerCase.matches("meta-inf.*\\.sf$")  => MergeStrategy.discard
    case _ => MergeStrategy.first
  }
}

// libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "8.1.2.v20120308"

// ivyXML := <dependency org="org.eclipse.jetty.orbit" name="javax.servlet" rev="3.0.0.v201112011016"> <artifact name="javax.servlet" type="orbit" ext="jar"/> </dependency>
