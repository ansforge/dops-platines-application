package fr.asipsante.tomcat.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPFilter implements Filter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IPFilter.class);

	private FilterConfig config;

	private String IP_CHECK = "";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.config = filterConfig;
		IP_CHECK = config.getInitParameter("IP_CHECK");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		boolean check = false;
		
		String ip = request.getRemoteAddr();
		LOGGER.info("ip du client: "+ ip);
		LOGGER.info("ip renseigné: "+ IP_CHECK);
		
		List<String> addresses = Arrays.asList(IP_CHECK.split("\\s*,\\s*"));
		for (String address : addresses) {
			
			IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(address);
			check = ipAddressMatcher.matches(ip);
			
			if (check) {
				break;
			}
		}
		
		HttpServletResponse httpResp = null;
		if (response instanceof HttpServletResponse)
			httpResp = (HttpServletResponse) response;
		if (check) {
			chain.doFilter(request, response);
		} else {
			LOGGER.error("Error 403: Forbidden, client ip: " + ip + ", does not match " + IP_CHECK);
			httpResp.sendError(HttpServletResponse.SC_FORBIDDEN, "Error 403: Forbidden, client ip: " + ip + ", does not match " + IP_CHECK);
		}

	}

	@Override
	public void destroy() {
	}

}
