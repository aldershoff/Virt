package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class CheckIfLoggedFilter
 */
@WebFilter("/CheckIfLoggedFilter")
public class CheckIfLoggedInFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public CheckIfLoggedInFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// Getting the session
		HttpSession session = req.getSession(false);

		if(session != null){
		/**
		 * If the session has the userID (set inside the user servlet), a
		 * redirection to the home page will taken place
		 */
		if (session.getAttribute("userID") != null && session.getAttribute("username") != null && session.getAttribute("pincode") == null) {
			res.sendRedirect("/ProjectVirt/customer/home");
		} else {
			// Let the request pass
			chain.doFilter(request, response);
		}
		}
		else{
			chain.doFilter(request, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
