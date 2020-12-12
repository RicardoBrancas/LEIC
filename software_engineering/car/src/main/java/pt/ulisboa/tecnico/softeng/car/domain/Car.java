package pt.ulisboa.tecnico.softeng.car.domain;

public class Car extends Car_Base {

	public Car(String plate, int kilometers, double price, RentACar rentACar) {
        checkArguments(plate, kilometers, rentACar);

        setPlate(plate.toUpperCase());
	    setKilometers(kilometers);
	    setPrice(price);
	    setRentACar(rentACar);
	}
}
