package ai.aitia.arrowhead.it2generichttp.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DynamicService {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	public Pair<Integer, Optional<String>> doBridgeOperation(String endpointId, String payloadBase64, String origin) {
		System.out.println("endpointID: " + endpointId);
		return Pair.of(HttpStatus.CREATED.value(), Optional.of(payloadBase64));
	}

}