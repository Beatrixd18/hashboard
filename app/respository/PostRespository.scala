/*
 * Copyright (c) 2014 Hashcode (Z) Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package respository

import java.util.Date

import com.datastax.driver.core.{ResultSet, Row}
import com.websudos.phantom.Implicits._
import com.websudos.phantom.iteratee.Iteratee
import conf.DataConnection
import domain.Post

import scala.concurrent.Future


class PostRespository extends CassandraTable[PostRespository, Post] {

  object zone extends StringColumn(this) with PartitionKey[String]

  object date extends DateColumn(this) with PrimaryKey[Date] with ClusteringOrder[Date] with Descending

  object linkhash extends StringColumn(this) with PrimaryKey[String] with ClusteringOrder[String] with Descending

  object yeardate extends DateColumn(this)

  object domain extends StringColumn(this)

  object title extends StringColumn(this)

  object article extends StringColumn(this)

  object metakeywords extends StringColumn(this)

  object metaDescription extends StringColumn(this)

  object link extends StringColumn(this)

  object imageUrl extends StringColumn(this)

  object seo extends StringColumn(this)

  object imagePath extends StringColumn(this)

  object caption extends StringColumn(this)

  object siteCode extends StringColumn(this)

  override def fromRow(row: Row): Post = {
    Post(
      zone(row),
      yeardate(row),
      linkhash(row),
      domain(row),
      date(row),
      title(row),
      article(row),
      metakeywords(row),
      metaDescription(row),
      link(row),
      imageUrl(row),
      seo(row),
      imagePath(row),
      caption(row),
      siteCode(row)
    )
  }
}

object PostRespository extends PostRespository with DataConnection {
  override lazy val tableName = "posts"

  def save(post: Post): Future[ResultSet] = {
    insert
      .value(_.linkhash, post.linkhash)
      .value(_.yeardate, post.yeardate)
      .value(_.domain, post.domain)
      .value(_.date, post.date)
      .value(_.title, post.title)
      .value(_.article, post.article)
      .value(_.metakeywords, post.metakeywords)
      .value(_.metaDescription, post.metaDescription)
      .value(_.link, post.link)
      .value(_.zone, post.zone)
      .value(_.imageUrl, post.imageUrl)
      .value(_.seo, post.seo)
      .value(_.imagePath, post.imagePath)
      .value(_.caption, post.caption)
      .value(_.siteCode, post.siteCode)
      .future() flatMap {
      _ => {
        SinglePostRepository.insert
          .value(_.linkhash, post.linkhash)
          .value(_.yeardate, post.yeardate)
          .value(_.domain, post.domain)
          .value(_.date, post.date)
          .value(_.title, post.title)
          .value(_.article, post.article)
          .value(_.metakeywords, post.metakeywords)
          .value(_.metaDescription, post.metaDescription)
          .value(_.link, post.link)
          .value(_.zone, post.zone)
          .value(_.imageUrl, post.imageUrl)
          .value(_.seo, post.seo)
          .value(_.imagePath, post.imagePath)
          .value(_.caption, post.caption)
          .value(_.siteCode, post.siteCode)
          .future() flatMap {
          _ => {
            WebSiteRepository.insert
              .value(_.linkhash, post.linkhash)
              .value(_.yeardate, post.yeardate)
              .value(_.domain, post.domain)
              .value(_.date, post.date)
              .value(_.title, post.title)
              .value(_.article, post.article)
              .value(_.metakeywords, post.metakeywords)
              .value(_.metaDescription, post.metaDescription)
              .value(_.link, post.link)
              .value(_.zone, post.zone)
              .value(_.imageUrl, post.imageUrl)
              .value(_.seo, post.seo)
              .value(_.imagePath, post.imagePath)
              .value(_.caption, post.caption)
              .value(_.siteCode, post.siteCode)
              .future() flatMap {
              _ => {
                ZonePostRespository.insert
                  .value(_.linkhash, post.linkhash)
                  .value(_.yeardate, post.yeardate)
                  .value(_.domain, post.domain)
                  .value(_.date, post.date)
                  .value(_.title, post.title)
                  .value(_.article, post.article)
                  .value(_.metakeywords, post.metakeywords)
                  .value(_.metaDescription, post.metaDescription)
                  .value(_.link, post.link)
                  .value(_.zone, post.zone)
                  .value(_.imageUrl, post.imageUrl)
                  .value(_.seo, post.seo)
                  .value(_.imagePath, post.imagePath)
                  .value(_.caption, post.caption)
                  .value(_.siteCode, post.siteCode)
                  .future() flatMap {
                  _ => {
                    SitePostRespository.insert
                      .value(_.linkhash, post.linkhash)
                      .value(_.yeardate, post.yeardate)
                      .value(_.domain, post.domain)
                      .value(_.date, post.date)
                      .value(_.title, post.title)
                      .value(_.article, post.article)
                      .value(_.metakeywords, post.metakeywords)
                      .value(_.metaDescription, post.metaDescription)
                      .value(_.link, post.link)
                      .value(_.zone, post.zone)
                      .value(_.imageUrl, post.imageUrl)
                      .value(_.seo, post.seo)
                      .value(_.imagePath, post.imagePath)
                      .value(_.caption, post.caption)
                      .value(_.siteCode, post.siteCode)
                      .future() flatMap {
                      _ => {
                        PublishedLinksRepository.insert
                          .value(_.linkhash, post.linkhash)
                          .value(_.item,"POST")
                          .ttl(604000)
                          .future()
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  def getPostById(zone: String, linkhash: String): Future[Option[Post]] = {
    select.where(_.zone eqs zone)
      .and(_.linkhash eqs linkhash).one()
  }

  def getLatestPosts(zone: String, date: Date): Future[Iterator[Post]] = {
    select.where(_.zone eqs zone)
      .fetchEnumerator() run Iteratee.slice(0, 50)
  }

}
