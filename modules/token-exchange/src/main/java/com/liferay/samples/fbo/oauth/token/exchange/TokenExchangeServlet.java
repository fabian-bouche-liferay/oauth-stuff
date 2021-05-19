package com.liferay.samples.fbo.oauth.token.exchange;

import com.google.gson.Gson;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
	    immediate = true,
	    property = {
	        "osgi.http.whiteboard.context.path=/",
	        "osgi.http.whiteboard.servlet.pattern=/token-exchange"
	    },
	    service = Servlet.class
	)
public class TokenExchangeServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(TokenExchangeServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		LOG.debug("doPost");
		
		long companyId = _portal.getCompanyId(req);
		
		String grantType = ParamUtil.get(req, "grant_type", "");
		
		if(!TokenExchangeConstants.TOKEN_EXCHANGE_GRANT_TYPE.equals(grantType)) {
			LOG.error("Not token exchange grant type");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

		String subjectToken = ParamUtil.get(req, "subject_token", "");
		String subjectTokenType = ParamUtil.get(req, "subject_token_type", "");
		
		LOG.debug("Service references: {}", tokenExchangeServiceReferences.size());
		
		try {
			final String tokenType = determineSubjectTokenType(subjectToken, subjectTokenType);
			LOG.debug("Token type: {}", tokenType);
			
			LOG.debug("property {}", tokenExchangeServiceReferences.get(0).getProperty(TokenExchangeConstants.TOKEN_SUBJECT_TYPE));
			
			TokenExchangeService tokenExchangeService = null; 
					
			for(int i = 0; i < tokenExchangeServiceReferences.size(); i++) {
				ServiceReference<TokenExchangeService> serviceReference = tokenExchangeServiceReferences.get(i);
				String tokenSubjectType = (String) serviceReference.getProperty(TokenExchangeConstants.TOKEN_SUBJECT_TYPE);
				if(tokenSubjectType != null && tokenSubjectType.equals(tokenType)) {
					LOG.debug("Fetching TokenExchangeService");
					tokenExchangeService = _context.getService(serviceReference);
					LOG.debug("Found TokenExchangeService");
					break;
				}
			}

			if(tokenExchangeService == null) {
				throw new NotImplementedSubjectTokenTypeException();
			}
			
		    TokenExchangeResponse tokenExchangeResponse = tokenExchangeService.getTokenExchangeResponse(subjectToken, companyId);
		    
		    Gson gson = new Gson();
		    String jsonInString = gson.toJson(tokenExchangeResponse);
		    
		    PrintWriter writer = resp.getWriter();
		    writer.print(jsonInString);
		    writer.close();
			
		} catch (NotImplementedSubjectTokenTypeException e) {
			LOG.error("Subject token type {} not implemented", subjectTokenType);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		} catch (InvalidTokenException e) {
			LOG.error("Invalid token", e);
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
			    
	}

	private String determineSubjectTokenType(String subjectToken, String subjectTokenType) throws NotImplementedSubjectTokenTypeException {
		
		boolean isJwt = isJwt(subjectToken);
		
		if(isJwt && TokenExchangeConstants.ACCESS_TOKEN_TOKEN_TYPE.equals(subjectTokenType)) {
			return TokenExchangeConstants.JWT_ACCESS_TOKEN_SUBJECT_TYPE;
		} else {
			throw new NotImplementedSubjectTokenTypeException();
		}
		
	}
	
	public void activate(BundleContext context) {
		_context = context;
	}
	
	private boolean isJwt(String token) {
		return true;
	}
	
	@Reference(
			cardinality = ReferenceCardinality.MULTIPLE,
			policyOption = ReferencePolicyOption.GREEDY			
			)
	private List<ServiceReference<TokenExchangeService>> tokenExchangeServiceReferences;
	
	private BundleContext _context;
	
	@Reference
	private Portal _portal;
}
