import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.5.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"                     % "1.9.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"             % "8.5.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30"  % "2.0.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"             % bootstrapVersion,
    "uk.gov.hmrc"       %% "domain-play-30"                         % "9.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.jsoup"                   %  "jsoup"                    % "1.17.2",
    "org.scalatestplus"           %% "scalacheck-1-17"          % "3.2.18.0",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2",
    "uk.gov.hmrc"                 %% "bootstrap-test-play-30"   % bootstrapVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
