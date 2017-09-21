package gov.usdot.cv.broadcast.processor;

import gov.usdot.asn1.j2735.CVTypeHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.deleidos.rtws.core.framework.EnrichmentDefinition;
import com.deleidos.rtws.core.framework.processor.AbstractEnrichmentProcessor;
import com.deleidos.rtws.core.framework.processor.EnrichmentAction;
import com.deleidos.rtws.core.framework.processor.ParameterList;

@EnrichmentDefinition(
	description = "Add broadcast instructions to the advisory situation data.", 
	type = "cv_broadcast_instructions", 
	properties = {}
)
public class BroadcastInstructionsProcessor extends AbstractEnrichmentProcessor {
	private static final Logger logger 			= Logger.getLogger(BroadcastInstructionsProcessor.class);
	private static final String DATE_PATTERN 	= "yyyy-MM-dd'T'HH:mm:ss";
	private static final String UTC_TIMEZONE	= "UTC";
	private static final int RSU_DIST_TYPE 		= CVTypeHelper.DistributionType.RSU.intValue();
	
	// Default values for the RSU broadcast instructions for May PlugFest
	private static final int PSID 				= 0x8003;
	private static final int PRIORITY 			= 2;
	private static final long TX_MODE 			= 1;
	private static final long TX_CHANNEL 		= 5;
	private static final int TX_INTERVAL 		= 1;
	private static final boolean SIGNATURE 		= true;
	private static final boolean ENCRYPTION 	= false;
	private static final int ONE_DAY 			= 1000 * 60 * 60 * 24;
	
	@Override
	public String getType() {
		return "cv_broadcast_instructions";
	}

	/**
	 * When configuring this enrichment, the order of the parameters must be distribution type, 
	 * advisory situation data message type, delivery start time, and delivery stop time.
	 */
	@Override
	public Object buildEnrichedElement(EnrichmentAction action, ParameterList parameters) {
		if (parameters.toArray() == null || parameters.toArray().length != 4) {
			logger.warn("Parameters is empty or not of length 4. " + 
				"Expecting distribution type, asdm type, start time, and end time parameters.");
			return null;
		}
		
		Integer distType = parameters.get(0, Integer.class);
		if (distType.intValue() != RSU_DIST_TYPE) {
			logger.debug("Advisory situation data is not of distribution type 'rsu', no broadcast instruction will be added.");
			return null;
		}
		
		Integer asdmType 	= parameters.get(1, Integer.class);
		String startTime 	= parameters.get(2, String.class);
		String stopTime 	= parameters.get(3, String.class);
		
		long current = System.currentTimeMillis();
		
		Calendar start = null, stop = null;
		
		if (startTime == null) {
			start = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE));
			start.setTimeInMillis(current);
			
			DateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
			startTime = formatter.format(start.getTime());
		}
		
		if (stopTime == null) {
			stop = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE));
			stop.setTimeInMillis(current + (ONE_DAY * 7));
			
			DateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
			stopTime = formatter.format(stop.getTime());
		} 
		
		JSONObject broadcastInstructions = new JSONObject();
		broadcastInstructions.put("type", 			asdmType);
		broadcastInstructions.put("psid", 			PSID);
		broadcastInstructions.put("priority", 		PRIORITY);
		broadcastInstructions.put("txMode", 		TX_MODE);
		broadcastInstructions.put("txChannel", 		TX_CHANNEL);
		broadcastInstructions.put("txInterval", 	TX_INTERVAL);
		broadcastInstructions.put("deliveryStart", 	startTime);
		broadcastInstructions.put("deliveryStop",	stopTime);
		broadcastInstructions.put("signature", 		SIGNATURE);
		broadcastInstructions.put("encryption", 	ENCRYPTION);
		
		return broadcastInstructions;
	}
	
}