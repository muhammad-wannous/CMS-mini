
package cmsMini;

/**
 * This class will hold the information of the course an instance of "cms-mini" is managing.
 *
 * @author Muhammad Wannous
 */
public class Course {
  /*<sourcedid> --> <id>*/
  private String courseID;
  /*<description> --> <short>*/
  private String courseTitle;
  /*<extension>*/
  private String courseDescriptionFile;
  //Are course details saved?
  private boolean detailsSaved;
  //Are basic info saved? (course id and admin id)
  private boolean basicInfoSaved;

  public Course(String courseID, String courseTitle, String courseDescriptionFile, 
          boolean basicInfoSaved, boolean detailsSaved) {
    this.courseID = courseID;
    this.courseTitle = courseTitle;
    this.courseDescriptionFile = courseDescriptionFile;
    this.detailsSaved = detailsSaved;
    this.basicInfoSaved = basicInfoSaved;
  }

  public String getCourseID() {
    return courseID;
  }

  public void setCourseID(String courseID) {
    this.courseID = courseID;
  }

  public String getCourseTitle() {
    return courseTitle;
  }

  public void setCourseTitle(String courseTitle) {
    this.courseTitle = courseTitle;
  }

  public String getCourseDescriptionFile() {
    return courseDescriptionFile;
  }

  public void setCourseDescriptionFile(String courseDescriptionFile) {
    this.courseDescriptionFile = courseDescriptionFile;
  }

  public boolean isDetailsSaved() {
    return detailsSaved;
  }

  public void setDetailsSaved(boolean detailsSaved) {
    this.detailsSaved = detailsSaved;
  }

  public boolean isBasicInfoSaved() {
    return basicInfoSaved;
  }

  public void setBasicInfoSaved(boolean basicInfoSaved) {
    this.basicInfoSaved = basicInfoSaved;
  }
  
}
