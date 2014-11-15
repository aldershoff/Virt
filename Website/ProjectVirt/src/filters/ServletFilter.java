package filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet Filter for processing GET and POSTS.
 * The mappings are located inside the web.xml.
 * Example: when a POST is processed, a GET method should not be possible.
 * This also could be vise versa, therefore this filter has been created.
 */
public class ServletFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public ServletFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * This filter will filter GET request when getting in. When the filter
	 * blocks, it will redirect to the designed page (for example)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {

		// Setting the requests and responses
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// Calling the filter request for the GETS onto the Logging process
		loginFilterRequests(request, response, req, res, filterChain);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * Method for checking the request made by the user for processing
	 * the POST message for logging into the authenticated Servlet
	 * @param request
	 * @param response
	 * @param req
	 * @param res
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void loginFilterRequests(ServletRequest request,
			ServletResponse response, HttpServletRequest req,
			HttpServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		/**
		 * If a GET request is made, then the
		 */
		if ("GET".equals(req.getMethod())
				&& "/login/processlogin".equals(req.getServletPath())) {
			res.sendRedirect("/ProjectVirt/login");
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private void adminFilterRequests(ServletRequest request,
			ServletResponse response, HttpServletRequest req,
			HttpServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		/**
		 * If a GET request is made, then the
		 */
		if ("GET".equals(req.getMethod())
				&& "/login/processlogin".equals(req.getServletPath())) {
			res.sendRedirect("");
		} else {
			filterChain.doFilter(request, response);
		}
	}
}