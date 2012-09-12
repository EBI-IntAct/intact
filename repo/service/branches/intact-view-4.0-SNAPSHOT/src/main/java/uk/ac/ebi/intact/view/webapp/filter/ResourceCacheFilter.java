package uk.ac.ebi.intact.view.webapp.filter;

import javax.faces.application.ResourceHandler;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter for caching resources
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12/09/12</pre>
 */
public class ResourceCacheFilter implements Filter {

    private static final String GET_METHOD = "GET_METHOD";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String uri = httpRequest.getRequestURI();
            if (GET_METHOD.equalsIgnoreCase(httpRequest.getMethod()) && uri.contains(ResourceHandler.RESOURCE_IDENTIFIER)) {
                httpResponse.setHeader("Cache-Control", "public"); // Secure caching
            }
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }
}
