package vttp.batch5.csf.assessment.server.services;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp.batch5.csf.assessment.server.repositories.OrdersRepository;
import vttp.batch5.csf.assessment.server.repositories.RestaurantRepository;

@Service
public class RestaurantService {

  private static final Logger logger = Logger.getLogger(RestaurantService.class.getName());
  private static final String PAYMENT_URL = 
    "https://payment-service-production-a75a.up.railway.app/api/payment";

  @Value("${payee.name}")
  private String payee;

  @Autowired
  private OrdersRepository ordersRepo;

  @Autowired
  private RestaurantRepository restaurantRepo;

  // TODO: Task 2.2
  // You may change the method's signature
  public JsonArray getMenu() {
    List<Document> results = ordersRepo.getMenu();
    JsonArrayBuilder builder = Json.createArrayBuilder();
    for(Document doc : results) {
      builder.add(Json.createObjectBuilder()
        .add("id", doc.getString("_id"))
        .add("name", doc.getString("name"))
        .add("description", doc.getString("description"))
        .add("price", doc.getDouble("price"))
        .build());
    }
    return builder.build();
  }
  
  // TODO: Task 4
  public JsonObject postFoodOrder(String payload) throws Exception {
    JsonObject input = Json.createReader(new StringReader(payload))
      .readObject().getJsonObject("body");
    JsonArray items = input.getJsonArray("items");
    String order_id = UUID.randomUUID().toString().substring(0, 8);
    double sum = sumItemPrice(items);
    String username = input.getString("username");
    JsonObject body = reqBody(order_id, username, payee, sum);
    logger.info("[Restaurant Svc] Req body: " + body);
    
    RequestEntity<String> req = RequestEntity.post(PAYMENT_URL)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .header("X-Authenticate", username)
      .body(body.toString());

    RestTemplate template = new RestTemplate();
    ResponseEntity<String> resp = template.exchange(req, String.class);

    String result = resp.getBody();
    JsonObject details = Json.createReader((new StringReader(result)))
      .readObject();

    String payment_id = details.getString("payment_id");
    int timestamp = details.getInt("timestamp");
    // Insert order details in MySQL
    boolean sqlInserted = restaurantRepo.insertOrderDetails(order_id, payment_id,
      timestamp, sum, username);

    // Insert order details in MongoDB
    boolean mgInserted = ordersRepo.insertOrderDetails(order_id, payment_id, username, 
      sum, timestamp, items);

    if(sqlInserted && mgInserted) {
      return Json.createObjectBuilder()
        .add("orderId", order_id)
        .add("payment_id", payment_id)
        .add("total", sum)
        .add("timestamp", timestamp)
        .build();
    }
    return Json.createObjectBuilder()
      .add("message", "Error inserting order details")
      .build();
  }

  public boolean validateUser(String payload) {
    JsonObject input = Json.createReader(new StringReader(payload))
      .readObject().getJsonObject("body");
    return restaurantRepo.validateUser(input.getString("username"), 
      input.getString("password"));
  }

  private JsonObject reqBody(String order_id, String payer, String payee, double payment) {
    return Json.createObjectBuilder()
      .add("order_id", order_id)
      .add("payer", payer)
      .add("payee", payee)
      .add("payment", payment)
      .build();
  }

  private double sumItemPrice(JsonArray items) {
    double sum = 0;
    // DecimalFormat df = new DecimalFormat("#.##");
    for(int i = 0; i < items.size(); i++) {
      JsonObject item = items.getJsonObject(i);
      int quantity = item.getInt("quantity");
      double price = Double.parseDouble(item.getJsonNumber("price").toString());
      sum += quantity * price;
    } 
    // sum = Double.valueOf(df.format(sum));
    return sum;
  }


}
