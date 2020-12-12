package pt.ulisboa.tecnico.softeng.hotel.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.RemoteAccessException;

public class BankInterface {
	private static Logger logger = LoggerFactory.getLogger(BankInterface.class);

	private static String ENDPOINT = "http://localhost:8082";

	public static String processPayment(RestBankOperationData bankOperationData) {
		logger.info("processPayment iban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
				bankOperationData.getIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
				bankOperationData.getTransactionReference());

		RestTemplate restTemplate = new RestTemplate();
		try {
			String result = restTemplate.postForObject(
					ENDPOINT + "/rest/banks/accounts/" + bankOperationData.getIban() + "/processPayment",
					bankOperationData, String.class);
			return result;
		} catch (HttpClientErrorException e) {
			logger.info(
					"processPayment HttpClientErrorException  iban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
					bankOperationData.getIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
					bankOperationData.getTransactionReference());
			throw new BankException();
		} catch (Exception e) {
			logger.info("processPayment Exception iban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
					bankOperationData.getIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
					bankOperationData.getTransactionReference());
			throw new RemoteAccessException();
		}
	}

	public static String cancelPayment(String reference) {
		logger.info("cancelPayment reference:{}", reference);

		RestTemplate restTemplate = new RestTemplate();
		try {
			String result = restTemplate.postForObject(ENDPOINT + "/rest/banks/cancel?reference=" + reference, null,
					String.class);
			return result;
		} catch (HttpClientErrorException e) {
			logger.info("cancelPayment HttpClientErrorException reference:{}", reference);
			throw new BankException();
		} catch (Exception e) {
			logger.info("cancelPayment Exception reference:{}", reference);
			throw new RemoteAccessException();
		}
	}

}
