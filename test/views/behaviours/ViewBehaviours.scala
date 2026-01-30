/*
 * Copyright 2026 HM Revenue & Customs
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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.ViewSpecBase

trait ViewBehaviours extends ViewSpecBase {

  private def findBannerTitle(view: HtmlFormat.Appendable): String =
    asDocument(view).getElementsByClass("govuk-service-navigation__service-name").text().trim

  def normalPage(view: HtmlFormat.Appendable, messageKeyPrefix: String, expectedGuidanceKeys: String*): Unit =

    "behave like a normal page" when {

      "rendered" must {

        "have the correct banner title" in {

          findBannerTitle(view) mustBe messages("service.name")
        }

        "display the correct browser title" in {

          val doc = asDocument(view)
          assertEqualsMessage(doc, "title", s"$messageKeyPrefix.title")
        }

        "display the correct page title" in {

          val doc = asDocument(view)
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading")
        }

        "display the correct guidance" in {

          val doc = asDocument(view)
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {

          val doc = asDocument(view)
          assertRenderedByCssSelector(doc, "a[lang=cy]")
        }
      }
    }

  def pageWithHint[A](form: Form[A], createView: Form[A] => HtmlFormat.Appendable, expectedHintKey: String): Unit =

    "behave like a page with hint text" in {

      val doc = asDocument(createView(form))
      assertContainsHint(doc, "value", Some(messages(s"$expectedHintKey.hint")))
    }

  def pageWithAPrintButton(view: HtmlFormat.Appendable): Unit =

    "behave like a page with a print button" must {

      "have a print button" in {

        val doc = asDocument(view)
        assertRenderedById(doc, "print")
      }
    }

  def normalPageTitleWithCaption(
    view: HtmlFormat.Appendable,
    messageKeyPrefix: String,
    captionParam: String,
    expectedGuidanceKeys: String*
  ): Unit =

    "behave like a normal page" when {

      "rendered" must {

        "have the correct banner title" in {

          findBannerTitle(view) mustBe messages("service.name")
        }

        "display the correct browser title" in {

          val doc = asDocument(view)
          assertEqualsMessage(doc, "title", s"$messageKeyPrefix.title")
        }

        "display the correct page title with caption" in {

          val doc = asDocument(view)
          assertPageTitleWithCaptionEqualsMessages(
            doc,
            s"$messageKeyPrefix.caption",
            captionParam,
            s"$messageKeyPrefix.heading"
          )
        }

        "display the correct guidance" in {

          val doc = asDocument(view)
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {

          val doc = asDocument(view)
          assertRenderedByCssSelector(doc, "a[lang=cy]")
        }
      }
    }

  def pageWithBackLink(view: HtmlFormat.Appendable): Unit =

    "behave like a page with a back link" must {

      "have a back link" in {

        val doc = asDocument(view)
        assertRenderedById(doc, "back-link")
      }
    }

  def pageWithASubmitButton(view: HtmlFormat.Appendable) =

    "behave like a page with a submit button" must {
      "have a submit button" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "submit")
      }
    }

  def pageWithSignOutButton(view: HtmlFormat.Appendable): Unit =

    "behave like a page with a sign out button" must {

      "have a sign out button" in {

        val doc = asDocument(view)
        assertRenderedById(doc, "sign-out")
      }
    }

  def pageWithContinueButton(view: HtmlFormat.Appendable): Unit =

    "behave like a page with a continue button" must {

      "have a continue button" in {

        val doc = asDocument(view)
        assertRenderedById(doc, "continue")
      }
    }

  def expectedHref(view: HtmlFormat.Appendable, id: String, expectedHref: String): Unit =

    s"have a link for $id" in {
      val doc  = asDocument(view)
      val link = doc.getElementById(id)
      link.attr("href") mustBe expectedHref
    }

  def pageWithSubHeading(view: HtmlFormat.Appendable, text: String): Unit =

    "behave like a page with a sub-heading" must {

      "have a sub-heading" in {

        val doc = asDocument(view)
        assertContainsText(doc, text)
      }
    }

  def pageWithoutLogoutButton(view: HtmlFormat.Appendable) =

    "behave like a page without a logout button" must {
      "not have a logout button" in {
        val doc = asDocument(view)
        assertNotRenderedById(doc, "logOut")
      }
    }

}
