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
 * Servlet Filter implementation class CheckForTwoFactorPin
 */
@WebFilter("/CheckForTwoFactorPin")
public class CheckForTwoFactorPinFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public CheckForTwoFactorPinFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;

		// Getting the session
		HttpSession session = req.getSession(false);

		if (session != null) {
			/**
			 * If the user still has the pincode, then the pincode shall be removed
			 * and will be requested once again
			 */
			if (session.getAttribute("pincode") != null) {
				session.invalidate();
			} else {
				// Let the request pass
				chain.doFilter(request, response);
			}
		} else {

			chain.doFilter(request, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
