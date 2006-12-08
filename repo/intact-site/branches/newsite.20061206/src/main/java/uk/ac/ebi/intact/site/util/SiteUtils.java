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
package uk.ac.ebi.intact.site.util;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import uk.ac.ebi.intact.site.items.Datasets;
import uk.ac.ebi.intact.site.items.News;
import uk.ac.ebi.intact.taglib.IntactSiteFunctions;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SiteUtils
{
    
    private static final String MIME_TYPE = "application/xml; charset=UTF-8";

    private SiteUtils(){}

    public static List<News.PieceOfNews> readNews(String newsXml)
    {
        List<News.PieceOfNews> news;

        News objNews = null;
        try {
            URL datasetsUrl = new URL(newsXml);
            objNews = (News) readDatasetsXml(datasetsUrl.openStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (objNews != null)
        {
            news = objNews.getPieceOfNews();
        }
        else
        {
            news = new ArrayList<News.PieceOfNews>();
        }

        return news;
    }

    public static SyndFeed createNewsFeed(List<News.PieceOfNews> news)
    {
        SyndFeed feed = new SyndFeedImpl();

        feed.setTitle("EBI IntAct News");
        feed.setLink("http://www.ebi.ac.uk/intact");
        feed.setDescription("News of the IntAct Project");

        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        for (News.PieceOfNews pieceOfNews : news)
        {
            SyndEntry entry = createNewsFeedEntry(pieceOfNews);
            entries.add(entry);
        }

        feed.setEntries(entries);

        return feed;
    }

    private static SyndEntry createNewsFeedEntry(News.PieceOfNews pieceOfNews)
    {
        SyndEntry entry;
        SyndContent description;

        entry = new SyndEntryImpl();
        entry.setTitle(pieceOfNews.getTitle());

        if (pieceOfNews.getMoreLink() != null)
        {
            entry.setLink(pieceOfNews.getMoreLink());
        }

        entry.setPublishedDate(IntactSiteFunctions.convertToDate(pieceOfNews.getDate(), "yyyyMMdd"));

        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue(pieceOfNews.getDescription());
        entry.setDescription(description);

        return entry;
    }

    public static List<Datasets.Dataset> readDatasets(String datasetsXml)
    {
        List<Datasets.Dataset> dataSets;

        Datasets datasets = null;
        try {
            URL datasetsUrl = new URL(datasetsXml);
            datasets = (Datasets) readDatasetsXml(datasetsUrl.openStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (datasets != null)
        {
            dataSets = datasets.getDataset();
        }
        else
        {
            dataSets = new ArrayList<Datasets.Dataset>();
        }

        return dataSets;
    }

    private static Object readDatasetsXml(InputStream is) throws JAXBException
    {
        JAXBContext jc = JAXBContext.newInstance(News.class.getPackage().getName());
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return  unmarshaller.unmarshal(is);
    }

    public static void writeFeed(SyndFeed feed, FeedType feedType, Writer outputWriter) throws FeedException, IOException
    {
        try {
            feed.setFeedType(feedType.getType());

            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed,outputWriter);
        }
        catch (FeedException ex) {
            ex.printStackTrace();
        }
    }

    public static void writeFeed(SyndFeed feed, FeedType feedType, FacesContext context) throws FeedException, IOException
    {
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        response.setContentType(MIME_TYPE);
        writeFeed(feed, feedType, response.getWriter());
    }
}
