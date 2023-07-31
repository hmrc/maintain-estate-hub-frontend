import sbt.*

object AppDependencies {
  val bootstrapVersion = "7.20.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"             % "0.74.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "7.16.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % bootstrapVersion,
    "uk.gov.hmrc"       %% "domain"                         % "8.3.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"               %% "scalatest"                % "3.2.16",
    "org.jsoup"                   %  "jsoup"                    % "1.16.1",
    "org.scalatestplus"           %% "mockito-4-11"             % "3.2.16.0",
    "org.scalatestplus"           %% "scalacheck-1-17"          % "3.2.16.0",
    "com.github.tomakehurst"      %  "wiremock-standalone"      % "2.27.2",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.vladsch.flexmark"        %  "flexmark-all"             % "0.64.8",
    "uk.gov.hmrc"                 %% "bootstrap-test-play-28"   % bootstrapVersion

  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}