import com.google.gson.Gson;
import com.iti.foodCalculator.entity.FoodItem;
import com.iti.foodCalculator.utlity.reader.XLSReader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CalculatorServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        List<FoodItem> foodItemList = new XLSReader().readXlsFileToList();
        String json = new Gson().toJson(foodItemList);

        try {
            response.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
