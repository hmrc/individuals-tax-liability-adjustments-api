import sbt.*

object AppDependencies {

  private val bootstrapPlayVersion = "10.5.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"        %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
    "org.typelevel"      %% "cats-core"                 % "2.13.0",
    "com.github.jknack"  % "handlebars"                 % "4.3.1"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"          %% "bootstrap-test-play-30" % bootstrapPlayVersion,
    "org.scalamock"        %% "scalamock"              % "7.5.5",
    "org.scalatestplus"    %% "scalacheck-1-19"        % "3.2.19.0"
  ).map(_ % Test)

  val itDependencies: Seq[ModuleID] = Seq(
    "io.swagger.parser.v3"         % "swagger-parser-v3"     % "2.1.37",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.21.0"
  ).map(_ % Test)

}
