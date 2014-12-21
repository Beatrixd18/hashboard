package services.posts

import java.util.{Date, UUID}

import com.github.slugify.Slugify
import com.gravity.goose.{Configuration, Goose}
import conf.Util
import domain.{ErrorReport, Link, Post}
import respository.{ErrorReportRespository, PostRespository}
import services.posts.filters.FilterService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Created by hashcode on 2014/12/11.
 */
object FetchContentFromRssLinks {
  def getContent(link: Link) = {
    val article = Future {
      new Goose(new Configuration).extractContent(link.url)
    }
    article onComplete {
      case Success(article) => {
        val post = Post(
          link.zone,
          Util.yearDate,
          link.linkhash,
          link.site,
          link.datePublished,
          link.title,
          article.getCleanedArticleText(),
          Util.getMetaKeywords(link.title),
          Util.getMedecription(article.getCleanedArticleText),
          article.getCanonicalLink,
          article.getTopImage().getImageSrc,
          Util.getPrettySeo(link.title),
          article.getTopImage().getImageSrc,
          Util.getCaption(),
          link.siteCode
        )
        PostRespository.save(post)
      }
      case Failure(error) => {
        val err = ErrorReport(link.zone, UUID.randomUUID().toString, link.site, new Date, "GOOSE: EXTRACTION ERROR: " + error.getMessage)
        ErrorReportRespository.save(err)
      }
    }
  }

}
