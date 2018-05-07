package pt.ulisboa.tecnico.softeng.broker.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class ActivityInterface {
	private static Logger logger = LoggerFactory.getLogger(ActivityInterface.class);

	private static String ENDPOINT = "http://localhost:8081";

	public static String reserveActivity(RestActivityBookingData activityBookingData) {
		logger.info("reserveActivity begin:{}, end:{}, age:{}, nif:{}, iban:{}, adventureId:{}",
				activityBookingData.getBegin(), activityBookingData.getEnd(), activityBookingData.getAge(),
				activityBookingData.getNif(), activityBookingData.getIban(), activityBookingData.getAdventureId());
		RestTemplate restTemplate = new RestTemplate();
		try {
			String result = restTemplate.postForObject(ENDPOINT + "/rest/providers/reserve", activityBookingData,
					String.class);
			return result;
		} catch (HttpClientErrorException e) {
			logger.info(
					"reserveActivity HttpClientErrorException begin:{}, end:{}, age:{}, nif:{}, iban:{}, adventureId:{}",
					activityBookingData.getBegin(), activityBookingData.getEnd(), activityBookingData.getAge(),
					activityBookingData.getNif(), activityBookingData.getIban(), activityBookingData.getAdventureId());
			throw new ActivityException();
		} catch (Exception e) {
			logger.info("reserveActivity Exception begin:{}, end:{}, age:{}, nif:{}, iban:{}, adventureId:{}",
					activityBookingData.getBegin(), activityBookingData.getEnd(), activityBookingData.getAge(),
					activityBookingData.getNif(), activityBookingData.getIban(), activityBookingData.getAdventureId());
			throw new RemoteAccessException();
		}
	}

	public static String cancelReservation(String activityConfirmation) {
		logger.info("cancelReservation activityConfirmation:{}", activityConfirmation);
		RestTemplate restTemplate = new RestTemplate();
		try {
			String result = restTemplate.postForObject(
					ENDPOINT + "/rest/providers/cancel?reference=" + activityConfirmation, null, String.class);
			return result;
		} catch (HttpClientErrorException e) {
			logger.info("cancelReservation HttpClientErrorException activityConfirmation:{}", activityConfirmation);
			throw new ActivityException();
		} catch (Exception e) {
			logger.info("cancelReservation Exception activityConfirmation:{}", activityConfirmation);
			throw new RemoteAccessException();
		}
	}

	public static RestActivityBookingData getActivityReservationData(String reference) {
		logger.info("getActivityReservationData reference:{}", reference);
		RestTemplate restTemplate = new RestTemplate();
		try {
			RestActivityBookingData result = restTemplate.getForObject(
					ENDPOINT + "/rest/providers/reservation?reference=" + reference, RestActivityBookingData.class);
			return result;
		} catch (HttpClientErrorException e) {
			logger.info("getActivityReservationData HttpClientErrorException:{}", reference);
			throw new ActivityException();
		} catch (Exception e) {
			logger.info("getActivityReservationData Exception:{}", reference);
			throw new RemoteAccessException();
		}
	}

}
