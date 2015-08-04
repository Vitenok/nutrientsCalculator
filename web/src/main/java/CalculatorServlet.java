import com.google.gson.Gson;
import com.iti.foodCalculator.entity.Product;
import com.iti.foodCalculator.service.ProductsService;
import com.iti.foodCalculator.utlity.reader.XLSReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CalculatorServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Product> productsList = new XLSReader().readXlsFileToList();
//        String allFoodItemsJson = new Gson().toJson(productsList);
        
        request.setAttribute("productsList", productsList);
        request.getRequestDispatcher("index.jsp").forward(request, response);


//        try {
//            response.getWriter().write(allFoodItemsJson);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//
//        response.setContentType("application/json");
//        response.getWriter().write(allFoodItemsJson);

//        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
