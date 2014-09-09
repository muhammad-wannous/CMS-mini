package cmsMini;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Muhammad Wannous
 */
public class Validate extends HttpServlet {

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    ServletContext application = this.getServletContext();
    HttpSession session = request.getSession(true);
    String userID = request.getParameter("userID");
    String userSecret = request.getParameter("userSecret");
    Course thisCourse = (Course) application.getAttribute("courseInfo");
    ArrayList<String> usersInList = (ArrayList<String>) application.getAttribute("usersInList");
    boolean doneCheck = false;
    User userIn = null;
    String homeFolderId = "";
    if ((thisCourse != null)
            && (userID != null)
            && !"".equals(userID)
            && (userSecret != null)
            && !"".equals(userSecret)
            && usersInList != null) {
      if (!usersInList.contains(userID)) {
//        Iterator usersIt = usersInList.iterator();
//        while (usersIt.hasNext()) {
//          System.out.println("User in: " + (String) usersIt.next());
//        }
//        System.out.println("UserID=" + userID);
        /*If the course data has already been parsed and saved into the Datastore then we might find the user there.*/
          DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
          Key userKey = KeyFactory.createKey("tableName", "User");
          Query userQuery = new Query("User", userKey);
          Filter nameFilter = new Query.FilterPredicate("courseUserID", Query.FilterOperator.EQUAL, userID);
          Filter secretFilter = new Query.FilterPredicate("courseUserPassword",
                  Query.FilterOperator.EQUAL, userSecret);
          Filter nameSecretFilter = Query.CompositeFilterOperator.and(nameFilter, secretFilter);
          userQuery.setFilter(nameSecretFilter);
          List<Entity> dbUsers = datastore.prepare(userQuery).asList(FetchOptions.Builder.withDefaults());
          if (!dbUsers.isEmpty()) {
            Entity userEntity = dbUsers.get(0);
            userIn = new User((String) userEntity.getProperty("courseUserID"),
                    "", (String) userEntity.getProperty("courseUserSystemRole"));
            homeFolderId = (String) userEntity.getProperty("homeFolderId");
            doneCheck = true;
          }
        if (doneCheck) {
          session.setAttribute("userIn", userIn);
          session.setAttribute("homeFolderId", homeFolderId);
          session.setAttribute("infoString", "User in!");
          usersInList.add(userID);
          application.setAttribute("usersInList", usersInList);
        } else {
          session.setAttribute("infoString", "Problem in authentication!");
        }
      } else {
        session.setAttribute("infoString", "User already in!");
      }
    }
    response.sendRedirect("home.jsp");
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>

}
