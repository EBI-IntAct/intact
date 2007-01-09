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
import uk.ac.ebi.intact.site.items.News;
import uk.ac.ebi.intact.site.util.FeedType;
import uk.ac.ebi.intact.site.util.SiteUtils;
import uk.ac.ebi.intact.site.util.DataLoadingException;

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

    public static final String NEWS_URL = "uk.ac.ebi.intact.NEWS_URL";
    private static final String NEWS_SHOWN_NUM = "uk.ac.ebi.intact.NEWS_SHOWN_NUM";

    private List<News.PieceOfNews> news;
    private List<News.PieceOfNews> lastNews;

    public NewsBean()
    {
        String newsXml = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(NEWS_URL);

        int newsNum = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getInitParameter(NEWS_SHOWN_NUM));

        try
        {
            news = SiteUtils.readNews(newsXml);
        }
        catch (DataLoadingException e)
        {
            e.printStackTrace();
            news = new ArrayList<News.PieceOfNews>();
        }

        if (!news.isEmpty())
        {
            lastNews = news.subList(0, Math.min(newsNum, news.size()));
        }
        else
        {
            lastNews = new ArrayList<News.PieceOfNews>();
        }
    }

    public void exportFeed(ActionEvent evt)
    {
        SyndFeed feed = SiteUtils.createNewsFeed(news);

        FacesContext context = FacesContext.getCurrentInstance();

        try
        {
            SiteUtils.writeFeed(feed, FeedType.DEFAULT, context);
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

    

    public List<News.PieceOfNews> getNews()
    {
        return news;
    }

    public void setNews(List<News.PieceOfNews> news)
    {
        this.news = news;
    }

    public List<News.PieceOfNews> getLastNews()
    {
        return lastNews;
    }

    public void setLastNews(List<News.PieceOfNews> lastNews)
    {
        this.lastNews = lastNews;
    }
}
