import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.4.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"                     % "2.11.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"             % "12.25.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30"  % "3.4.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"             % bootstrapVersion,
    "uk.gov.hmrc"       %% "domain-play-30"                         % "10.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus"           %% "scalacheck-1-18"          % "3.2.19.0",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2",
    "uk.gov.hmrc"                 %% "bootstrap-test-play-30"   % bootstrapVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
