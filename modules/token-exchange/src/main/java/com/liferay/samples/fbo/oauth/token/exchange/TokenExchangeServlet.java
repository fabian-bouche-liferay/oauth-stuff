package com.liferay.samples.fbo.oauth.token.exchange;

import com.google.gson.Gson;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
	    immediate = true,
	    property = {
	        "osgi.http.whiteboard.context.path=/",
	        "osgi.http.whiteboard.servlet.pattern=/token-exchange/*"
	    },
	    service = Servlet.class
	)
public class TokenExchangeServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(TokenExchangeServlet.class);
	
	private ServiceTracker<TokenExchangeService, TokenExchangeService> _tokenExchangeCheckServiceTracker;
	private BundleContext _context;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String grantType = ParamUtil.get(req, "grant_type", "");
		
		if(!TokenExchangeConstants.TOKEN_EXCHANGE_GRANT_TYPE.equals(grantType)) {
			LOG.error("Not token exchange grant type");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

		String subjectToken = ParamUtil.get(req, "subject_token", "");
		String subjectTokenType = ParamUtil.get(req, "subject_token_type", "");
		
		String tokenType = null;
		try {
			tokenType = determineSubjectTokenType(subjectToken, subjectTokenType);
		} catch (NotImplementedSubjectTokenTypeException e) {
			LOG.error("Subject token type {} not implemented", subjectTokenType);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		
		TokenExchangeService tokenExchangeService = null;
		
	    ServiceReference<TokenExchangeService>[] serviceReferences = this._tokenExchangeCheckServiceTracker.getServiceReferences();
	    for(int i = 0; i < serviceReferences.length; i++) {
	    	ServiceReference<TokenExchangeService> serviceReference = serviceReferences[i];
	    	String serviceReferenceTokenSubjectType = (String) serviceReference.getProperty(TokenExchangeConstants.TOKEN_SUBJECT_TYPE);
	    	if(serviceReferenceTokenSubjectType != null && serviceReferenceTokenSubjectType.equals(tokenType)) {
	    		tokenExchangeService = this._tokenExchangeCheckServiceTracker.getService(serviceReference);
	    		break;
	    	}
	    }
	    
	    if(tokenExchangeService == null) {
			LOG.error("Subject token type {} not implemented", subjectTokenType);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
	    }

	    TokenExchangeResponse tokenExchangeResponse = tokenExchangeService.getTokenExchangeResponse(subjectToken);
	    
	    Gson gson = new Gson();
	    String jsonInString = gson.toJson(tokenExchangeResponse);
	    
	    PrintWriter writer = resp.getWriter();
	    writer.print(jsonInString);
	    
		super.doPost(req, resp);
	}

	private String determineSubjectTokenType(String subjectToken, String subjectTokenType) throws NotImplementedSubjectTokenTypeException {
		
		boolean isJwt = isJwt(subjectToken);
		
		if(isJwt && TokenExchangeConstants.ACCESS_TOKEN_TOKEN_TYPE.equals(subjectTokenType)) {
			return TokenExchangeConstants.JWT_ACCESS_TOKEN_SUBJECT_TYPE;
		} else {
			throw new NotImplementedSubjectTokenTypeException();
		}
		
	}
	
	private boolean isJwt(String token) {
		return true;
	}
	
	@Activate
	public void activate(BundleContext bundleContext) {

		_context = bundleContext;
		this._tokenExchangeCheckServiceTracker = new ServiceTracker<>(
	        _context, TokenExchangeService.class, null);
	    this._tokenExchangeCheckServiceTracker.open();

	}

	@Deactivate
	public void deactivate() {
		
		this._tokenExchangeCheckServiceTracker.close();

	}
	
}
