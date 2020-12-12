package pt.ulisboa.tecnico.softeng.tax.domain;

import java.util.Map;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class Buyer extends Buyer_Base {
	private final static int PERCENTAGE = 5;

	public Buyer(IRS irs, String NIF, String name, String address) {
		checkArguments(irs, NIF, name, address);

		setNif(NIF);
		setName(name);
		setAddress(address);

		irs.addTaxPayer(this);
	}

	@Override
	public void delete() {
		for (Invoice invoice : getInvoiceSet()) {
			invoice.delete();
		}

		super.delete();
	}

	public double taxReturn(int year) {
		if (year < 1970) {
			throw new TaxException();
		}

		double result = 0;
		for (Invoice invoice : getInvoiceSet()) {
			if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
				result = result + invoice.getIva() * PERCENTAGE / 100;
			}
		}
		return result;
	}

	@Override
	public Invoice getInvoiceByReference(String invoiceReference) {
		if (invoiceReference == null || invoiceReference.isEmpty()) {
			throw new TaxException();
		}

		for (Invoice invoice : getInvoiceSet()) {
			if (invoice.getReference().equals(invoiceReference)) {
				return invoice;
			}
		}
		return null;
	}

	public Map<Integer, Double> getTaxReturnPerYear() {
		return getInvoiceSet().stream().map(i -> i.getDate().getYear()).distinct()
				.collect(Collectors.toMap(y -> y, y -> taxReturn(y)));
	}

}
