package pt.ulisboa.tecnico.softeng.car.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.car.domain.Vehicle;

public class VehicleData {
    private Vehicle.Type type;
    private String plate;
    private Integer kilometers;
    private Double price;
    private RentACarData rentacar;

    public VehicleData() { }

    public VehicleData(Vehicle.Type type, String plate, int kilometers, double price, RentACarData rentacar) {
        this.type = type;
        this.plate = plate;
        this.kilometers = kilometers;
        this.price = price;
        this.rentacar = rentacar;
    }

    public Vehicle.Type getType() {
        return type;
    }

    public void setType(Vehicle.Type type) {
        this.type = type;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Integer getKilometers() {
        return kilometers;
    }

    public void setKilometers(Integer kilometers) {
        this.kilometers = kilometers;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public RentACarData getRentacar() {
        return rentacar;
    }

    public void setRentACar(RentACarData rentACar) {
        this.rentacar = rentACar;
    }
}
