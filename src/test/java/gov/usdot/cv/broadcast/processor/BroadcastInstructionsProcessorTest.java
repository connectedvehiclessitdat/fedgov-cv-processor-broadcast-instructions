package gov.usdot.cv.broadcast.processor;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.deleidos.rtws.core.framework.processor.EnrichmentAction;
import com.deleidos.rtws.core.framework.processor.EnrichmentFieldMappingCache;
import com.deleidos.rtws.core.framework.processor.EnrichmentModelMappingCache;

public class BroadcastInstructionsProcessorTest {

	@Test
	public void testAddAdvBroadcastInstructionsWithoutStartStopTime() {
		BroadcastInstructionsProcessor processor = new BroadcastInstructionsProcessor();
		
		JSONObject advSitData = buildAdvSitData();
		
		EnrichmentAction geojsonAction = new EnrichmentAction();
		geojsonAction.setType("cv_broadcast_instructions");
		geojsonAction.setField("broadcastInstructions");
		geojsonAction.setParameters(
			new String[] {
				"advisoryDetails.distType", 
				"advisoryDetails.asdmType", 
				"advisoryDetails.startTime", 
				"advisoryDetails.stopTime"
			}
		);
		geojsonAction.setRemoveParameters(false);
		
		EnrichmentFieldMappingCache fields = new EnrichmentFieldMappingCache();
		fields.setModelName("test");
		fields.setModelVersion("1.0");
		fields.setFields(new EnrichmentAction[]{geojsonAction});
		
		EnrichmentModelMappingCache models = new EnrichmentModelMappingCache();
		models.setModels(new EnrichmentFieldMappingCache[]{fields});
		
		processor.setEnrichments(models);
		
		processor.initialize();
		processor.process(advSitData);
		
		assertEquals(true, advSitData.has("broadcastInstructions"));
		assertEquals(2, advSitData.getJSONObject("broadcastInstructions").getInt("type"));
		assertEquals(32771, advSitData.getJSONObject("broadcastInstructions").getInt("psid"));
		assertEquals(1, advSitData.getJSONObject("broadcastInstructions").getInt("txMode"));
		assertEquals(5, advSitData.getJSONObject("broadcastInstructions").getInt("txChannel"));
		assertEquals(1, advSitData.getJSONObject("broadcastInstructions").getInt("txInterval"));
		assertEquals(true, advSitData.getJSONObject("broadcastInstructions").has("deliveryStart"));
		assertEquals(true, advSitData.getJSONObject("broadcastInstructions").has("deliveryStop"));
		assertEquals(true, advSitData.getJSONObject("broadcastInstructions").getBoolean("signature"));
		assertEquals(false, advSitData.getJSONObject("broadcastInstructions").getBoolean("encryption"));
		
		processor.dispose();
	}
	
	@Test
	public void testAddAdvBroadcastInstructionsWithStartStopTime() {
		BroadcastInstructionsProcessor processor = new BroadcastInstructionsProcessor();
		
		JSONObject advSitData = buildAdvSitData();
		
		Calendar start = null, stop = null;
		start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		start.setTimeInMillis(System.currentTimeMillis());
		stop = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		stop.setTimeInMillis(System.currentTimeMillis() + (1000 * 60 * 15));
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String startTime = formatter.format(start.getTime());
		String stopTime = formatter.format(stop.getTime());
		
		JSONObject details = advSitData.getJSONObject("advisoryDetails");	
		details.put("startTime", startTime);
		details.put("stopTime", stopTime);
		
		EnrichmentAction geojsonAction = new EnrichmentAction();
		geojsonAction.setType("cv_broadcast_instructions");
		geojsonAction.setField("broadcastInstructions");
		geojsonAction.setParameters(
			new String[] {
				"advisoryDetails.distType", 
				"advisoryDetails.asdmType", 
				"advisoryDetails.startTime", 
				"advisoryDetails.stopTime"
			}
		);
		geojsonAction.setRemoveParameters(false);
		
		EnrichmentFieldMappingCache fields = new EnrichmentFieldMappingCache();
		fields.setModelName("test");
		fields.setModelVersion("1.0");
		fields.setFields(new EnrichmentAction[]{geojsonAction});
		
		EnrichmentModelMappingCache models = new EnrichmentModelMappingCache();
		models.setModels(new EnrichmentFieldMappingCache[]{fields});
		
		processor.setEnrichments(models);
		
		processor.initialize();
		processor.process(advSitData);
		
		assertEquals(true, advSitData.has("broadcastInstructions"));
		assertEquals(2, advSitData.getJSONObject("broadcastInstructions").getInt("type"));
		assertEquals(32771, advSitData.getJSONObject("broadcastInstructions").getInt("psid"));
		assertEquals(1, advSitData.getJSONObject("broadcastInstructions").getInt("txMode"));
		assertEquals(5, advSitData.getJSONObject("broadcastInstructions").getInt("txChannel"));
		assertEquals(1, advSitData.getJSONObject("broadcastInstructions").getInt("txInterval"));
		assertEquals(true, advSitData.getJSONObject("broadcastInstructions").has("deliveryStart"));
		assertEquals(startTime, advSitData.getJSONObject("broadcastInstructions").getString("deliveryStart"));
		assertEquals(true, advSitData.getJSONObject("broadcastInstructions").has("deliveryStop"));
		assertEquals(stopTime, advSitData.getJSONObject("broadcastInstructions").getString("deliveryStop"));
		assertEquals(true, advSitData.getJSONObject("broadcastInstructions").getBoolean("signature"));
		assertEquals(false, advSitData.getJSONObject("broadcastInstructions").getBoolean("encryption"));
		
		processor.dispose();
	}
	
	@Test
	public void testMissingParameters() {
		BroadcastInstructionsProcessor processor = new BroadcastInstructionsProcessor();
		
		JSONObject advSitData = buildAdvSitData();
		
		EnrichmentAction geojsonAction = new EnrichmentAction();
		geojsonAction.setType("cv_broadcast_instructions");
		geojsonAction.setField("broadcastInstructions");
		geojsonAction.setParameters(
			new String[] {
				"advisoryDetails.distType",
				"advisoryDetails.startTime", 
				"advisoryDetails.stopTime"
			}
		);
		geojsonAction.setRemoveParameters(false);
		
		EnrichmentFieldMappingCache fields = new EnrichmentFieldMappingCache();
		fields.setModelName("test");
		fields.setModelVersion("1.0");
		fields.setFields(new EnrichmentAction[]{geojsonAction});
		
		EnrichmentModelMappingCache models = new EnrichmentModelMappingCache();
		models.setModels(new EnrichmentFieldMappingCache[]{fields});
		
		processor.setEnrichments(models);
		
		processor.initialize();
		processor.process(advSitData);
		
		assertEquals(false, advSitData.has("broadcastInstructions"));
		
		processor.dispose();
	}
	
	@Test
	public void testNobroadcastInstructionsAdded() {
		BroadcastInstructionsProcessor processor = new BroadcastInstructionsProcessor();
		
		JSONObject advSitData = buildAdvSitData();
		
		advSitData.getJSONObject("advisoryDetails").put("distType", 8);
		
		EnrichmentAction geojsonAction = new EnrichmentAction();
		geojsonAction.setType("cv_broadcast_instructions");
		geojsonAction.setField("broadcastInstructions");
		geojsonAction.setParameters(
			new String[] {
				"advisoryDetails.distType",
				"advisoryDetails.asdmType",
				"advisoryDetails.startTime", 
				"advisoryDetails.stopTime"
			}
		);
		geojsonAction.setRemoveParameters(false);
		
		EnrichmentFieldMappingCache fields = new EnrichmentFieldMappingCache();
		fields.setModelName("test");
		fields.setModelVersion("1.0");
		fields.setFields(new EnrichmentAction[]{geojsonAction});
		
		EnrichmentModelMappingCache models = new EnrichmentModelMappingCache();
		models.setModels(new EnrichmentFieldMappingCache[]{fields});
		
		processor.setEnrichments(models);
		
		processor.initialize();
		processor.process(advSitData);
		
		assertEquals(false, advSitData.has("broadcastInstructions"));
		
		processor.dispose();
	}
	
	private static JSONObject buildAdvSitData() {
		JSONObject data = new JSONObject();
		
		JSONObject header = new JSONObject();
		header.put("modelName", "test");
		header.put("modelVersion", "1.0");
		data.put("standardHeader", header);
		
		data.put("receiptId", "b334e47b-2f2b-46e7-ad1c-2ff069e038c1");
		data.put("dialogId", 156);
		data.put("sequenceId", 5);
		data.put("requestId", 428673774);
		
		JSONObject nwPos = new JSONObject();
		nwPos.put("lat", 43.0);
		nwPos.put("lon", -85.0);
		data.put("nwPos", nwPos);
		
		JSONObject sePos = new JSONObject();
		sePos.put("lat", 41.0);
		sePos.put("lon", -82.0);
		data.put("sePos", sePos);
		
		JSONObject advisoryDetails = new JSONObject();
		advisoryDetails.put("asdmId", 428673774);
		advisoryDetails.put("asdmType", 2);
		advisoryDetails.put("distType", 1);
		advisoryDetails.put("advisoryMessage", "Buckle up, its the law!");
		data.put("advisoryDetails", advisoryDetails);
		
		data.put("encodedMsg", "MIICCoACAJyBAQWCBBmNCu6jHKAMgAQZoUeAgQTNVgeAoQyABBhwGoCBBM8fywCkggHbgAQZjQrugQEAggIEAIWCAcowggHGgAQZjQrugQEFggEAoxygDIAEGUBHA4EEznwiTaEMgAQZO49LgQTOhSn2pIIBmKBLgAMAo2eBCUNPTVBVV0FSRYIMOTkgTW9ucm9lIFN0gwdEZXRyb2l0hAJNSYUDALxihgEBpwyABBk7j0uBBM6AQQyoCKEGgAEIgQEGoVGAAwCQSIELTkVYVCBFTkVSR1mCEDQ2MSBCdXJyb3VnaHMgU3SDB0RldHJvaXSEAk1JhQMAvEqGAQGnDIAEGUBHA4EEznwiTagIoQaAAQGBAQGiVYADAJ35gQ9ERVQgSU5TVCBPRiBBUlSCEDIzNCBGcmVkZXJpY2sgU3SDB0RldHJvaXSEAk1JhQMAvEqGAQGnDIAEGT+hGoEEzn2iCKgIoQaAAQKBAQGjToADAIqVgQpEVEUgRU5FUkdZgg4xIEVuZXJneSBQbGF6YYMHRGV0cm9pdIQCTUmFAwC8YoYBAacMgAQZO7Q5gQTOfn7JqAihBoABBIEBAaRPgAMAoDOBD1VBV0dNIDIwMFdBTEtFUoIKMjAwIFdhbGtlcoMHRGV0cm9pdIQCTUmFAwC8T4YBAacMgAQZPGqQgQTOhSn2qAihBoABAoEBAQ==");
		
		return data;
	}
	
}