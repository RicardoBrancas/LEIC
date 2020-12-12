package pt.ulisboa.tecnico.softeng.car.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.local.RentACarInterface;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.VehicleData;

@Controller
@RequestMapping(value = "/rentacars/rentacar/{code}/vehicles")
public class VehiclesController {
	private static Logger logger = LoggerFactory.getLogger(VehiclesController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String rentacarForm(Model model, @PathVariable String code) {
		logger.info("rentacar");
		model.addAttribute("rentacar", RentACarInterface.getRentACarData(code));
		model.addAttribute("vehicle", new VehicleData());
		model.addAttribute("vehicles", RentACarInterface.getVehicles(code));
		return "vehiclesView";
	}

	@RequestMapping(value = "/vehicle", method = RequestMethod.POST)
	public String rentacarSubmit(Model model, @PathVariable String code, @ModelAttribute VehicleData vehicleData) {
		logger.info("vehicleSubmit plate:{}, km:{}, price:{}, type:{}", vehicleData.getPlate(),
				vehicleData.getKilometers(), vehicleData.getPrice(), vehicleData.getType());

		try {
			RentACarInterface.createVehicle(code, vehicleData);
		} catch (CarException be) {
			model.addAttribute("error", "Error: it was not possible to create the Rent-A-Car");
			model.addAttribute("rentacar", RentACarInterface.getRentACarData(code));
			model.addAttribute("vehicle", vehicleData);
			model.addAttribute("vehicles", RentACarInterface.getVehicles(code));
			return "vehiclesView";
		}

		return "redirect:/rentacars/rentacar/" + code + "/vehicles";
	}
}
