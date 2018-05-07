package pt.ulisboa.tecnico.softeng.car.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ulisboa.tecnico.softeng.car.domain.Renting;
import pt.ulisboa.tecnico.softeng.car.domain.Vehicle;
import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.local.RentACarInterface;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentingData;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.VehicleData;

@Controller
@RequestMapping(value = "/rentacars/rentacar/{code}/vehicles/vehicle/{plate}/rentings/{reference}")
public class RentingController {
    private static Logger logger = LoggerFactory.getLogger(RentingController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String rentingForm(Model model,
                              @PathVariable String code,
                              @PathVariable String plate,
                              @PathVariable String reference) {

        RentingData rentingData = RentACarInterface.getRentingData(code, plate, reference);
        model.addAttribute("rentacar", RentACarInterface.getRentACarData(code));
        model.addAttribute("renting", rentingData);
        model.addAttribute("rentings", RentACarInterface.getRentings(code, plate));
        model.addAttribute("vehicle", RentACarInterface.getVehicleByPlate(code, plate));
        return "rentingView";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public String cancelSubmit(Model model,
                              @PathVariable String code,
                              @PathVariable String plate,
                              @PathVariable String reference) {
        try {

            RentingData rentingData = RentACarInterface.cancelRenting(code, plate, reference);
            model.addAttribute("rentacar", RentACarInterface.getRentACarData(code));
            model.addAttribute("renting", rentingData);
            model.addAttribute("rentings", RentACarInterface.getRentings(code, plate));
            model.addAttribute("vehicle", RentACarInterface.getVehicleByPlate(code, plate));

            return "redirect:/rentacars/rentacar/" + code + "/vehicles/vehicle/" + plate + "/rentings/" + reference;

        } catch(CarException carEx) {

            model.addAttribute("error", "Error: Cannot cancel this renting!");
            model.addAttribute("rentacar", RentACarInterface.getRentACarData(code));
            model.addAttribute("renting", RentACarInterface.getRentingData(code, plate, reference));
            model.addAttribute("rentings", RentACarInterface.getRentings(code, plate));
            model.addAttribute("vehicle", RentACarInterface.getVehicleByPlate(code, plate));

            return "rentingView";
        }
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public String checkoutSubmit(Model model,
                               @PathVariable String code,
                               @PathVariable String plate,
                               @PathVariable String reference,
                               @ModelAttribute RentingData rentingData) {
        try {

            RentingData rentData = RentACarInterface.checkoutRenting(code, plate, reference, rentingData.getKilometers());

            model.addAttribute("rentacar", RentACarInterface.getRentACarData(code));
            model.addAttribute("renting", rentData);
            model.addAttribute("rentings", RentACarInterface.getRentings(code, plate));
            model.addAttribute("vehicle", RentACarInterface.getVehicleByPlate(code, plate));
            model.addAttribute("notification", "Checkout out done!");

            return "redirect:/rentacars/rentacar/" + code + "/vehicles/vehicle/" + plate + "/rentings/" + reference;

        } catch(CarException carEx) {

            model.addAttribute("error", "Error: Cannot cancel this renting!");
            model.addAttribute("rentacar", RentACarInterface.getRentACarData(code));
            model.addAttribute("renting",  RentACarInterface.getRentingData(code, plate, reference));
            model.addAttribute("rentings", RentACarInterface.getRentings(code, plate));
            model.addAttribute("vehicle", RentACarInterface.getVehicleByPlate(code, plate));

            return "rentingView";

        }
    }

}
