package vttp.batch5.csf.assessment.server.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import vttp.batch5.csf.assessment.server.services.RestaurantService;

@RestController
@RequestMapping(path="/api")
@CrossOrigin(origins="*")
public class RestaurantController {

  private static final Logger logger = Logger.getLogger(RestaurantController.class.getName());

  @Autowired
  private RestaurantService restaurantSvc;

  // TODO: Task 2.2
  // You may change the method's signature
  @GetMapping(path="/menu", produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getMenus() {
    logger.info("[Ctrl] Sending menu items");

    JsonArray results = restaurantSvc.getMenu();
    return ResponseEntity.ok(results.toString());
  }

  // TODO: Task 4
  // Do not change the method's signature
  @PostMapping(path="/food_order", 
    consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> postFoodOrder(@RequestBody String payload) {
    logger.info("[Ctrl] Payload: " + payload);
    boolean validated = restaurantSvc.validateUser(payload);
    if(!validated) {
      return ResponseEntity.status((401))
        .body(Json.createObjectBuilder()
          .add("message", "Invaild username and/or password")
          .build().toString());
    }
    try {
      JsonObject result = restaurantSvc.postFoodOrder(payload);
      if(result.containsKey("message")) {
        return ResponseEntity.status((500))
          .body(result.toString());
      }
      return ResponseEntity.ok(result.toString());

    } catch (Exception ex) {
      ex.printStackTrace();
      return ResponseEntity.status((500))
        .body(Json.createObjectBuilder()
          .add("error", "Error posting food order")
          .add("message", ex.getMessage())
          .build().toString());
    }
  }
}
