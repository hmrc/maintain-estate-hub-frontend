import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val bootstrapVersion = "7.13.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"             % "0.74.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "6.4.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.12.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % bootstrapVersion,
    "uk.gov.hmrc"       %% "domain"                         % "8.1.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"               %% "scalatest"                % "3.2.15",
    "org.scalatestplus.play"      %% "scalatestplus-play"       % "5.1.0",
    "org.jsoup"                   %  "jsoup"                    % "1.15.3",
    "com.typesafe.play"           %% "play-test"                % PlayVersion.current,
    "org.scalatestplus"           %% "mockito-4-6"              % "3.2.15.0",
    "org.mockito"                 %  "mockito-all"              % "1.10.19",
    "org.scalatestplus"           %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.scalacheck"              %% "scalacheck"               % "1.17.0",
    "com.github.tomakehurst"      % "wiremock-standalone"       % "2.27.2",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.vladsch.flexmark"        % "flexmark-all"              % "0.62.2",
    "uk.gov.hmrc"                 %% "bootstrap-test-play-28"   % bootstrapVersion

  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}