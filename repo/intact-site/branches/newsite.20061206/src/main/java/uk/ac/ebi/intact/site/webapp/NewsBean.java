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
package uk.ac.ebi.intact.site.webapp;

import uk.ac.ebi.intact.site.items.News;

import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
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

    private static final String NEWS_URL = "uk.ac.ebi.intact.NEWS_URL";
    private static final String NEWS_SHOWN_NUM = "uk.ac.ebi.intact.NEWS_SHOWN_NUM";

    private List<News.PieceOfNews> news;
    private List<News.PieceOfNews> lastNews;

    public NewsBean()
    {
       // read datasets from XML
        String newsXml = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(NEWS_URL);
        int newsNum = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getInitParameter(NEWS_SHOWN_NUM));

        News objNews = null;
        try {
            URL datasetsUrl = new URL(newsXml);
            objNews = readDatasetsXml(datasetsUrl.openStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (objNews != null)
        {
            news = objNews.getPieceOfNews();

            lastNews = news.subList(0, newsNum);
        }
        else
        {
            news = new ArrayList<News.PieceOfNews>();
            lastNews = new ArrayList<News.PieceOfNews>();
        }
    }

    private static News readDatasetsXml(InputStream is) throws JAXBException
    {
        JAXBContext jc = JAXBContext.newInstance(News.class.getPackage().getName());
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (News)
                unmarshaller.unmarshal(is);
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
