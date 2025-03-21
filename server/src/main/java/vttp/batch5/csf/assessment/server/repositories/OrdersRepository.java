package vttp.batch5.csf.assessment.server.repositories;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;


@Repository
public class OrdersRepository {

  private static final Logger logger = Logger.getLogger(OrdersRepository.class.getName());
  private static final String C_MENUS = "menus";
  private static final String C_ORDERS = "orders";

  @Autowired
  private MongoTemplate mgTemplate;

  // TODO: Task 2.2
  // You may change the method's signature
  // Write the native MongoDB query in the comment below
  //
  //  Native MongoDB query here
  //  db.menus.find({})
  //  .sort({"name": 1})

  public List<Document> getMenu() {
    Query query = new Query()
      .with(Sort.by(Sort.Direction.ASC, "name"));
    List<Document> results = mgTemplate.find(query, Document.class, C_MENUS);
    return results;
  }

  // TODO: Task 4
  // Write the native MongoDB query for your access methods in the comment below
  //
  //  Native MongoDB query here
  /* db.orders.insert({
    { "_id": ,
      "order_id": ,
      "payment_id": ,
      "username": ,
      "total": ,
      "timestamp": ,
      "items": []
    }  
  })
  * 
  */
  public boolean insertOrderDetails(String order_id, String payment_id, String username,
    double total, long timestamp, JsonArray items) {
    Document toInsert = new Document("_id", order_id)
      .append("order_id", order_id)
      .append("payment_id", payment_id)
      .append("username", username)
      .append("total", total)
      .append("timestamp", new Date(timestamp))
      .append("items", arrayToDocs(items));
    Document inserted = mgTemplate.insert(toInsert, C_ORDERS);
    return !inserted.isEmpty() || inserted != null;
  }

  private List<Document> arrayToDocs(JsonArray items) {
    List<Document> docs = new LinkedList<>();
    for(int i = 0; i < items.size(); i++) {
      JsonObject item = items.getJsonObject(i);
      Document itemDoc = new Document("id", item.getString("id"))
        .append("price", Double.parseDouble(item.getJsonNumber("price").toString()))
        .append("quantity", item.getInt("quantity"));
      logger.info("[Orders Repo] Item Doc: " + itemDoc);
      docs.add(itemDoc);
    }
    return docs;
  }
  
}
