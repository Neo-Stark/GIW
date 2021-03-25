import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.5"
ThisBuild / useCoursier := false

val luceneVersion = "8.8.1"
val dependencies = Seq("org.apache.lucene" % "lucene-core" % luceneVersion,
  "org.apache.lucene" % "lucene-queryparser" % luceneVersion,
  "org.apache.lucene" % "lucene-analyzers-common" % luceneVersion)

lazy val root = (project in file(".")).
  aggregate(indexer, searcher)

lazy val indexer = project
  .settings(
    name := "indexer",
    mainClass in(Compile, packageBin) := Some("ugr.fjgg.motorBusqueda.indexer"),
    libraryDependencies ++= dependencies
  )
lazy val searcher = project
  .settings(
    name := "searcher",
    mainClass in(Compile, packageBin) := Some("ugr.fjgg.motorBusqueda.searcher"),
    libraryDependencies ++= dependencies,
  )