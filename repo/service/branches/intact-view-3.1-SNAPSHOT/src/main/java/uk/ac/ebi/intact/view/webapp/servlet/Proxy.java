package uk.ac.ebi.intact.view.webapp.servlet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.view.webapp.controller.config.IntactViewConfiguration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;

/**
 * Created by IntelliJ IDEA.
 * User: cjandras
 * Date: 07/07/11
 * Time: 13:22
 * To change this template use File | Settings | File Templates.
 */
public class Proxy extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String url = req.getParameter("url");
            if (url == null) {
                throw new ServletException("Parameter 'url' was expected");
            }

            url = URLDecoder.decode(url, "UTF-8");
            externalRequest(url, resp.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // the jsonExporter also acts as an Proxy for the internal javascript AJAX requests
    private void externalRequest(String url, Writer outputWriter) throws IOException {
        final IntactViewConfiguration configuration = (IntactViewConfiguration) IntactContext.getCurrentInstance().getSpringContext().getBean("intactViewConfiguration");

        HttpClient client = configuration.getHttpClientBasedOnUrl(url);
        GetMethod method = new GetMethod(url);

        int statusCode = client.executeMethod(method);
        InputStream inputStream = method.getResponseBodyAsStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try{
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                outputWriter.append(line + '\n');
            }
            outputWriter.flush();
        }
        finally {
            bufferedReader.close();
            inputStream.close();
        }
    }
}
