/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.site.mb;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import uk.ac.ebi.faces.component.news.NewsUtil;
import uk.ac.ebi.faces.component.news.FeedType;
import uk.ac.ebi.faces.DataLoadingException;
import uk.ac.ebi.faces.model.News;
import uk.ac.ebi.faces.model.NewsItem;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class NewsBean implements Serializable
{

    public static final String NEWS_URL = "uk.ac.ebi.faces.NEWS_URL";

    private News newsObject;
    private List<NewsItem> news;
    private List<NewsItem> urgentNews;

    public NewsBean()
    {

        String newsXml = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(NEWS_URL);

        try
        {
            newsObject = NewsUtil.readNews(newsXml);
            news = newsObject.getNewsItem();
        }
        catch (DataLoadingException e)
        {
            e.printStackTrace();
            news = new ArrayList<NewsItem>();
        }

        // urgent news
        urgentNews = new ArrayList<NewsItem>();

        for (NewsItem newsItem : news)
        {
            if (newsItem.isUrgent() != null && newsItem.isUrgent())
            {
                urgentNews.add(newsItem);
            }
        }

    }

    public void exportFeed(ActionEvent evt)
    {
        SyndFeed feed = NewsUtil.createNewsFeed(newsObject);

        FacesContext context = FacesContext.getCurrentInstance();

        try
        {
            NewsUtil.writeFeed(feed, FeedType.DEFAULT, context);
        }
        catch (FeedException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        context.getApplication().getStateManager().saveSerializedView(context);
        context.renderResponse();
    }    

    public List<NewsItem> getNews()
    {
        return news;
    }

    public void setNews(List<NewsItem> news)
    {
        this.news = news;
    }

    public News getNewsObject() {
        return newsObject;
    }

    public void setNewsObject(News newsObject) {
        this.newsObject = newsObject;
    }

    public List<NewsItem> getUrgentNews() {
        return urgentNews;
    }

    public void setUrgentNews(List<NewsItem> urgentNews) {
        this.urgentNews = urgentNews;
    }
}
