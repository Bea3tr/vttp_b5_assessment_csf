package vttp.batch5.csf.assessment.server.repositories;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

// Use the following class for MySQL database
@Repository
public class RestaurantRepository {

    private static final String SELECT_USER = """
            SELECT * FROM customers WHERE username = ?
            """;

    private static final String INSERT_ORDER_DETAILS = """
            INSERT INTO place_orders (order_id, payment_id, order_date, total, username)
            VALUES (?, ?, ?, ?, ?)
            """;

    @Autowired
    private JdbcTemplate sqlTemplate;

    public boolean validateUser(String username, String password) {
        SqlRowSet rs = sqlTemplate.queryForRowSet(SELECT_USER, username);
        if(rs.next()) {
            if(rs.getString("password").equals(password))
                return true;
        }
        return false;
    }

    public boolean insertOrderDetails(String order_id, String payment_id, long timestamp,
        double total, String username) {
        Date date = new Date(timestamp);
        try {
            sqlTemplate.update(INSERT_ORDER_DETAILS, order_id, payment_id, date, total, username);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
