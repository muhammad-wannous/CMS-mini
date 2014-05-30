
package cmsMini;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
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
public class saveDetails extends HttpServlet {

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
    HttpSession session = request.getSession();
    /*First, check that we have parsed the basic information of the course*/
    Course thisCourse = (Course) application.getAttribute("courseInfo");
    User thisUser = (User) session.getAttribute("userIn");
    CourseDetails thisCourseDetails = (CourseDetails) application.getAttribute("courseDetails");
    if ((thisCourse != null)
            && (thisUser != null)
            && (thisCourseDetails != null)
            && thisUser.getUserRole().endsWith("dmin")
            && thisCourse.isBasicInfoSaved()) {
      int i;
      thisCourse.setDetailsSaved(true);
      application.setAttribute("courseInfo", thisCourse);
      Key detailsKey = KeyFactory.createKey("tableName", "Details");

      Entity courseDetailsEntity = new Entity("Details", detailsKey);
      courseDetailsEntity.setProperty("courseID", thisCourseDetails.getCourseID());
      courseDetailsEntity.setProperty("courseName", thisCourseDetails.getCourseName());
      courseDetailsEntity.setProperty("credits", thisCourseDetails.getCredits());
      courseDetailsEntity.setProperty("department", thisCourseDetails.getDepartment());
      for (i = 0; i < 3; i++) {
        courseDetailsEntity.setProperty("grading[" + i + "][0]", thisCourseDetails.getGrading()[i][0]);
        courseDetailsEntity.setProperty("grading[" + i + "][1]", thisCourseDetails.getGrading()[i][1]);
      }
      courseDetailsEntity.setProperty("instructor", thisCourseDetails.getInstructor());
      courseDetailsEntity.setProperty("level", thisCourseDetails.getLevel());
      for (i = 0; i < 10; i++) {
        courseDetailsEntity.setProperty("objectives[" + i + "]", thisCourseDetails.getObjectives()[i]);
      }
      courseDetailsEntity.setProperty("prerequisites", thisCourseDetails.getPrerequisites());
      courseDetailsEntity.setProperty("schedule", thisCourseDetails.getSchedule());
      courseDetailsEntity.setProperty("semester", thisCourseDetails.getSemester());
      courseDetailsEntity.setProperty("catalogDescription", thisCourseDetails.getCatalogDescription());
      courseDetailsEntity.setProperty("textBook", thisCourseDetails.getTextBook());
      for (i = 0; i < 20; i++) {
        courseDetailsEntity.setProperty("toc[" + i + "][0]", thisCourseDetails.getToc()[i][0]);
        courseDetailsEntity.setProperty("toc[" + i + "][1]", thisCourseDetails.getToc()[i][1]);
      }
      courseDetailsEntity.setProperty("url", thisCourseDetails.getUrl());
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(courseDetailsEntity);

      Key courseKey = KeyFactory.createKey("tableName", "Course");
      Query courseBasicsQuery = new Query("Course", courseKey);
      List<Entity> dbCourses = datastore.prepare(courseBasicsQuery).asList(FetchOptions.Builder.withDefaults());
      if (!dbCourses.isEmpty()) {
        while (!dbCourses.isEmpty()) {
          datastore.delete(dbCourses.get(0).getKey());
        }
      }
      Entity courseBasicsEntity = new Entity("Course", courseKey);
      courseBasicsEntity.setProperty("courseID", thisCourse.getCourseID());
      courseBasicsEntity.setProperty("courseTitle", thisCourse.getCourseTitle());
      courseBasicsEntity.setProperty("courseDescriptionFile", thisCourse.getCourseDescriptionFile());
      courseBasicsEntity.setProperty("basicInfoSaved", thisCourse.isBasicInfoSaved());
      courseBasicsEntity.setProperty("detailsSaved", thisCourse.isDetailsSaved());
      datastore.put(courseBasicsEntity);
      session.setAttribute("infoString", "Details saved!");
    } else {
      session.setAttribute("infoString", "Problem in saving details!");
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
