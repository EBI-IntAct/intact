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

import com.sun.syndication.feed.synd.SyndFeed;
import uk.ac.ebi.intact.site.mb.NewsBean;
import uk.ac.ebi.intact.site.util.FeedType;
import uk.ac.ebi.intact.site.util.SiteUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class FeedServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String newsXml = request.getSession().getServletContext().getInitParameter(NewsBean.NEWS_URL);

        SyndFeed feed = SiteUtils.createNewsFeed(SiteUtils.readNews(newsXml));

        try
        {
            SiteUtils.writeFeed(feed, FeedType.DEFAULT, response.getWriter());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

    }

}
