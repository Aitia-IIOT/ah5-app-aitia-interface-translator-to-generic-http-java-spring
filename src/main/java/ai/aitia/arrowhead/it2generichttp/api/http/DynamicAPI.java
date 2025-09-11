package ai.aitia.arrowhead.it2generichttp.api.http;

import java.io.IOException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPConstants;
import ai.aitia.arrowhead.it2generichttp.api.http.utils.PayloadProcessor;
import ai.aitia.arrowhead.it2generichttp.service.DynamicService;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.http.HttpUtilities;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@Hidden
public class DynamicAPI {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private PayloadProcessor processor;

	@Autowired
	private DynamicService service;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@PostMapping(path = InterfaceTranslatorToGenericHTTPConstants.HTTP_API_DYNAMIC_PATH_WITH_PARAM)
	public void doBridge(@PathVariable(required = true) final String pathId, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
		logger.debug("doBridge started...");

		final String origin = HttpMethod.POST.name() + " " + InterfaceTranslatorToGenericHTTPConstants.HTTP_API_DYNAMIC_PATH_WITH_PARAM
				.replace(InterfaceTranslatorToGenericHTTPConstants.HTTP_PARAM_PATH_ID, pathId);

		try {
			final String payloadBase64 = processor.extractPayload(httpServletRequest);
			final Pair<Integer, Optional<String>> result = service.doBridgeOperation(pathId, payloadBase64, origin);
			handleResponse(httpServletRequest, httpServletResponse, result, origin);
		} catch (final Throwable t) {
			handleException(t, httpServletResponse, origin);
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void handleResponse(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final Pair<Integer, Optional<String>> result, final String origin) {
		logger.debug("handleResponse started...");

		try {
			final String acceptedContentType = httpServletRequest.getHeader(HttpHeaders.ACCEPT);
			if (!Utilities.isEmpty(acceptedContentType)) {
				httpServletResponse.setContentType(acceptedContentType);
			}
			httpServletResponse.setStatus(result.getFirst());
			if (result.getSecond().isPresent()) {
				final byte[] resultBytes = processor.extractResult(result.getSecond().get());
				httpServletResponse.getOutputStream().write(resultBytes);
				httpServletResponse.getOutputStream().flush();
			}
			httpServletResponse.getOutputStream().close();
		} catch (final IOException ex) {
			// nothing we can do
			logger.error("{} at {}: {}", ex.getClass().getName(), origin, ex.getMessage());
			logger.debug("Exception", ex);
		}

	}

	//-------------------------------------------------------------------------------------------------
	private void handleException(final Throwable t, final HttpServletResponse response, final String origin) {
		logger.debug("handleException started...");
		logger.debug("{} at {}: {}", t.getClass().getName(), origin, t.getMessage());
		logger.debug("Exception", t);

		final HttpStatus status = (t instanceof ArrowheadException)
				? HttpUtilities.calculateHttpStatusFromArrowheadException((ArrowheadException) t)
				: HttpStatus.INTERNAL_SERVER_ERROR;

		try {
			response.setContentType(MediaType.TEXT_PLAIN_VALUE);
			response.setStatus(status.value());
			response.getWriter().print(status + " " + t.getMessage());
			response.getWriter().flush();
			response.getWriter().close();
		} catch (final IOException ex) {
			// nothing we can do
			logger.error("{} at {}: {}", ex.getClass().getName(), origin, ex.getMessage());
			logger.debug("Exception", ex);
		}
	}
}