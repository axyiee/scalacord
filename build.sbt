ThisBuild / tlBaseVersion := "0.1"

ThisBuild / organization := "lol.syntax"
ThisBuild / organizationName := "FromSyntax"
ThisBuild / startYear := Some(2022)
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(tlGitHubDev("FromSyntax", "Pedro Henrique"))

// publish to s01.oss.sonatype.org (set to true to publish to oss.sonatype.org instead)
ThisBuild / tlSonatypeUseLegacyHost := false

ThisBuild / scalaVersion := "3.2.1"

lazy val deps = new {
    val typelevel = Seq(
        libraryDependencies ++= Seq(
            "org.typelevel" %%% "cats-effect" % "3.3.14",
            "org.typelevel" %%% "cats-core" % "2.8.0",
            "org.typelevel" %%% "spire" % "0.18.0"
        )
    )
    val stream = Seq(libraryDependencies ++= Seq("co.fs2" %%% "fs2-core" % "3.3.0"))
    val http = Seq(
        libraryDependencies ++= Seq("client", "circe").map(a => "org.http4s" %%% ("http4s-" + a) % "0.23.16")
    )
    val test = Seq(
        libraryDependencies ++= Seq(
            "org.scalameta" %%% "munit" % "1.0.0-M6" % Test,
            "org.typelevel" %%% "munit-cats-effect" % "2.0.0-M3" % Test,
        )
    )
    val json = Seq(
        libraryDependencies ++= Seq(
            "core",
            "generic",
            "parser",
        ).map(a => "io.circe" %%% ("circe-" + a) % "0.14.3")
    )
    val logging = Seq(libraryDependencies ++= Seq("org.typelevel" %%% "log4cats-core" % "2.5.0"))
}

lazy val common = crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("common"))
    .settings(name := "scalacord-common")
    .settings(deps.typelevel ++ deps.json ++ deps.logging ++ deps.stream ++ deps.test)

lazy val rest = crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("rest"))
    .settings(name := "scalacord-rest")
    .settings(deps.typelevel ++ deps.json ++ deps.logging ++ deps.stream ++ deps.http ++ deps.test)
    .dependsOn(common)

lazy val gateway = crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("gateway"))
    .settings(name := "scalacord-gateway")
    .settings(deps.typelevel ++ deps.json ++ deps.logging ++ deps.stream ++ deps.http ++ deps.test)
    .dependsOn(common)

lazy val core = crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("core"))
    .settings(name := "scalacord-core")
    .dependsOn(common, rest, gateway)

lazy val root = tlCrossRootProject.aggregate(core)
