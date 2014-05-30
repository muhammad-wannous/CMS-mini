package cmsMini;

/**
 * An instance of this class will hold the detailed information of the course delivered via CMS-mini. The fields of this
 * class correspond to the fields in the xml file containing the details.
 *
 * @author Muhammad Wannous
 */
public class CourseDetails {
  /*<sourcedid> --> <id>*/

  private String courseID;
  /*<description> --> <short>*/
  private String courseName;
  /*<org> --> <orgname><orgunit><type><id>*/
  private String department;
  /*<instructor>*/
  private String instructor;
  /*<semester>*/
  private String semester;
  /*<credits>*/
  private String credits;
  /*<level>*/
  private String level;
  /*The data inside <schedule>*/
  private String schedule;
  /*<prerequisites>*/
  private String prerequisites;
  /*The data inside <textbook>*/
  private String textBook;
  /*<grading> [3]*/
  private String[][] grading = new String[3][2];
  /*<description> --> <long>*/
  private String catalogDescription;
  /*<outcomes> --> <outcome>*/
  private String[] objectives = new String[10];
  /*<topics> --> <topic>*/
  private String[][] toc = new String[20][2];
  /*<url>*/
  private String url;

  public CourseDetails() {
    courseID = "";
    courseName = "";
    department = "";
    instructor = "";
    semester = "";
    credits = "";
    level = "";
    schedule = "";
    prerequisites = "";
    textBook = "";
    grading = new String[][]{{"", ""}, {"", ""}, {"", ""}};
    catalogDescription = "";
    objectives = new String[]{"", "", "", "", "", "", "", "", "", ""};
    toc = new String[][]{{"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}};
    url = "";
  }

  public String getCourseID() {
    return courseID;
  }

  public void setCourseID(String courseID) {
    this.courseID = courseID;
  }

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getInstructor() {
    return instructor;
  }

  public void setInstructor(String instructor) {
    this.instructor = instructor;
  }

  public String getSemester() {
    return semester;
  }

  public void setSemester(String semester) {
    this.semester = semester;
  }

  public String getCredits() {
    return credits;
  }

  public void setCredits(String credits) {
    this.credits = credits;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getSchedule() {
    return schedule;
  }

  public void setSchedule(String schedule) {
    this.schedule = schedule;
  }

  public String getPrerequisites() {
    return prerequisites;
  }

  public void setPrerequisites(String prerequisites) {
    this.prerequisites = prerequisites;
  }

  public String getTextBook() {
    return textBook;
  }

  public void setTextBook(String textBook) {
    this.textBook = textBook;
  }

  public String[][] getGrading() {
    return grading;
  }

  public void setGrading(String[][] grading) {
    this.grading = grading;
  }

  public String getCatalogDescription() {
    return catalogDescription;
  }

  public void setCatalogDescription(String catalogDescription) {
    this.catalogDescription = catalogDescription;
  }

  public String[] getObjectives() {
    return objectives;
  }

  public void setObjectives(String[] objectives) {
    this.objectives = objectives;
  }

  public String[][] getToc() {
    return toc;
  }

  public void setToc(String[][] toc) {
    this.toc = toc;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String printAsStr() {
    return "ID = " + courseID + "\n"
            + "Name = " + courseName + "\n"
            + " department= " + department + "\n"
            + "instructor = " + instructor + "\n"
            + "semester = " + semester + "\n"
            + "credits = " + credits + "\n"
            + "level = " + level + "\n"
            + "schedule = " + schedule + "\n"
            + "prerequisites = " + prerequisites + "\n"
            + "textBook = " + textBook + "\n"
            + "grading = " + grading[0][0] + " " + grading[0][1]+ "\n"
            + grading[1][0] + " " + grading[1][1]+ "\n"
            + grading[2][0] + " " + grading[2][1]+ "\n"
            + "catalogDescription = " + catalogDescription + "\n"
            + "objectives = " + objectives[0]+ "\n"
            + objectives[1]+ "\n"
            + objectives[2]+ "\n"
            + objectives[3]+ "\n"
            + objectives[4]+ "\n"
            + objectives[5]+ "\n"
            + objectives[6]+ "\n"
            + objectives[7]+ "\n"
            + objectives[8]+ "\n"
            + objectives[9] + "\n"
            + "toc = " + toc[0][0] + toc[0][1]+ "\n"
            + toc[1][0] + toc[1][1]+ "\n"
            + toc[2][0] + toc[2][1]+ "\n"
            + toc[3][0] + toc[3][1]+ "\n"
            + toc[4][0] + toc[4][1]+ "\n"
            + toc[5][0] + toc[5][1]+ "\n"
            + toc[6][0] + toc[6][1]+ "\n"
            + toc[7][0] + toc[7][1]+ "\n"
            + toc[8][0] + toc[8][1]+ "\n"
            + toc[9][0] + toc[9][1]+ "\n"
            + toc[10][0] + toc[10][1]+ "\n"
            + toc[11][0] + toc[11][1]+ "\n"
            + toc[12][0] + toc[12][1]+ "\n"
            + toc[13][0] + toc[13][1]+ "\n"
            + toc[14][0] + toc[14][1]+ "\n"
            + toc[15][0] + toc[15][1]+ "\n"
            + toc[16][0] + toc[16][1]+ "\n"
            + toc[17][0] + toc[17][1]+ "\n"
            + toc[18][0] + toc[18][1]+ "\n"
            + toc[19][0] + toc[19][1]+ "\n"
            + "url = " + url + "\n";
  }
}
