/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.hmrcfrontend.config.ContactFrontendConfig

import java.time.LocalDate

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, contactFrontendConfig: ContactFrontendConfig) {

  final val ENGLISH = "en"
  final val WELSH = "cy"
  final val UK_COUNTRY_CODE = "GB"

  val betaFeedbackUrl = s"${contactFrontendConfig.baseUrl.get}/contact/beta-feedback?service=${contactFrontendConfig.serviceId.get}"

  lazy val agentsSubscriptionsUrl: String = configuration.get[String]("urls.agentSubscriptions")
  lazy val agentServicesUrl = s"$agentsSubscriptionsUrl?continue=$loginContinueUrl"

  lazy val estatesHelplineUrl: String = configuration.get[String]("urls.estatesHelpline")
  lazy val registerEstateGuidanceUrl: String = configuration.get[String]("urls.registerEstateGuidance")

  lazy val authUrl: String = configuration.get[Service]("auth").baseUrl
  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val logoutUrl: String = configuration.get[String]("urls.logout")

  lazy val logoutAudit: Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.logout")

  lazy val countdownLength: Int = configuration.get[Int]("timeout.countdown")
  lazy val timeoutLength: Int = configuration.get[Int]("timeout.length")

  def redirectToLoginUrl: String = {
    s"$loginUrl?continue=$loginContinueUrl"
  }

  lazy val agentOverviewUrl: String = configuration.get[String]("urls.agentOverview")

  lazy val estatesStoreUrl: String = configuration.get[Service]("microservice.services.estates-store").baseUrl + "/estates-store"

  lazy val enrolmentStoreProxyUrl: String = configuration.get[Service]("microservice.services.enrolment-store-proxy").baseUrl

  lazy val locationCanonicalList: String = configuration.get[String]("location.canonical.list.all")
  lazy val locationCanonicalListCY: String = configuration.get[String]("location.canonical.list.allCY")

  lazy val estatesUrl: String = configuration.get[Service]("microservice.services.estates").baseUrl
  lazy val estatesAuthUrl: String = configuration.get[Service]("microservice.services.estates-auth").baseUrl

  lazy val declarationEmailEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.declaration.email.enabled")
  lazy val primaryEnrolmentCheckEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.primaryEnrolmentCheck.enabled")

  def addNewPersonalRepUrl(utr: String): String = configuration.get[String]("urls.maintainPersonalRep") + s"/$utr/add-new-personal-rep"
  def amendExistingPersonalRepUrl(utr: String): String = configuration.get[String]("urls.maintainPersonalRep") + s"/$utr/amend-existing-personal-rep"

  def verifyIdentityForAnEstateUrl(utr: String) =
    s"${configuration.get[String]("urls.startVerifyIdentity")}/$utr"

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang(ENGLISH),
    "cymraeg" -> Lang(WELSH)
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  private val minDay: Int = configuration.get[Int]("dates.minimum.day")
  private val minMonth: Int = configuration.get[Int]("dates.minimum.month")
  private val minYear: Int = configuration.get[Int]("dates.minimum.year")
  lazy val minDate: LocalDate = LocalDate.of(minYear, minMonth, minDay)

  val cachettlInSeconds: Long = configuration.get[Long]("mongodb.timeToLiveInSeconds")

  val dropIndexes: Boolean = configuration.getOptional[Boolean]("microservice.services.features.mongo.dropIndexes").getOrElse(false)

}
