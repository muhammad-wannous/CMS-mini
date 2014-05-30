package cmsMini;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.io.PrintWriter;
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
public class saveBasics extends HttpServlet {

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  /*This Servlet is responsible for saving the basic information of the course in the Datastore.*/
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    ServletContext application = this.getServletContext();
    HttpSession session = request.getSession();
    /*First, check that we have parsed the basic information of the course*/
    Course thisCourse = (Course) application.getAttribute("courseInfo");
    User thisUser = (User)session.getAttribute("userIn");
    if (thisCourse != null && thisUser!=null && thisUser.getUserRole().endsWith("dmin")) {
      thisUser = (User)application.getAttribute("adminInfo");
      thisCourse.setBasicInfoSaved(true);
      application.setAttribute("courseInfo", thisCourse);
      Key courseKey = KeyFactory.createKey("tableName", "Course");
      Key adminKey = KeyFactory.createKey("tableName", "Admin");
      Entity courseBasicsEntity = new Entity("Course", courseKey);
      Entity adminEntity = new Entity("Admin", adminKey);
      courseBasicsEntity.setProperty("courseID", thisCourse.getCourseID());
      courseBasicsEntity.setProperty("courseTitle", thisCourse.getCourseTitle());
      courseBasicsEntity.setProperty("courseDescriptionFile", thisCourse.getCourseDescriptionFile());
      courseBasicsEntity.setProperty("basicInfoSaved", thisCourse.isBasicInfoSaved());
      courseBasicsEntity.setProperty("detailsSaved", thisCourse.isDetailsSaved());
      
      adminEntity.setProperty("userID", thisUser.getUserID());
      adminEntity.setProperty("userMD5Pass", thisUser.getUserMD5Pass());
      adminEntity.setProperty("userRole", thisUser.getUserRole());

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(courseBasicsEntity);
      datastore.put(adminEntity);
      session.setAttribute("infoString", "Basics saved!");
    } else {
      session.setAttribute("infoString", "Problem in saving basics!");
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
