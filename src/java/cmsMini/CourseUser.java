package cmsMini;

/**
 *
 * @author Muhammad Wannous An instance of this class will hold the information of 1 user in the course. The user can be
 * a student or an instructor. The instance should be formed with the information obtained from an xml file which the
 * admin specifies.
 */
public class CourseUser {
  /*<userid>*/

  private String courseUserID;
  /*<userid password>*/
  private String courseUserPassword;
  /*<name><n><family>*/
  private String courseUserFamilyName;
  /*<name><n><given>*/
  private String courseUserGivenName;
  /*<name><n><prefix>*/
  private String courseUserNamePrefix;
  /*<demographics><gender>*/
  private String courseUserGender;
  /*<demographics><bd>*/
  private String courseUserBirthday;
  /*<email>*/
  private String courseUserEmail;
  /*<url>*/
  private String courseUserURL;
  /*<tel>*/
  private String courseUserTel;
  /*<systemrole>*/
  private String courseUserSystemRole;
  /*<institutionrole>*/
  private String courseUserInstitutionRole;

  public CourseUser() {
    courseUserID = "";
    courseUserPassword = "";
    courseUserFamilyName = "";
    courseUserGivenName = "";
    courseUserNamePrefix = "";
    courseUserGender = "";
    courseUserBirthday = "";
    courseUserEmail = "";
    courseUserURL = "";
    courseUserTel = "";
    courseUserSystemRole = "";
    courseUserInstitutionRole = "";
  }

  public String getCourseUserID() {
    return courseUserID;
  }

  public void setCourseUserID(String courseUserID) {
    this.courseUserID = courseUserID;
  }

  public String getCourseUserPassword() {
    return courseUserPassword;
  }

  public void setCourseUserPassword(String courseUserPassword) {
    this.courseUserPassword = courseUserPassword;
  }

  public String getCourseUserFamilyName() {
    return courseUserFamilyName;
  }

  public void setCourseUserFamilyName(String courseUserFamilyName) {
    this.courseUserFamilyName = courseUserFamilyName;
  }

  public String getCourseUserGivenName() {
    return courseUserGivenName;
  }

  public void setCourseUserGivenName(String courseUserGivenName) {
    this.courseUserGivenName = courseUserGivenName;
  }

  public String getCourseUserNamePrefix() {
    return courseUserNamePrefix;
  }

  public void setCourseUserNamePrefix(String courseUserNamePrefix) {
    this.courseUserNamePrefix = courseUserNamePrefix;
  }

  public String getCourseUserGender() {
    return courseUserGender;
  }

  public void setCourseUserGender(String courseUserGender) {
    this.courseUserGender = courseUserGender;
  }

  public String getCourseUserBirthday() {
    return courseUserBirthday;
  }

  public void setCourseUserBirthday(String courseUserBirthday) {
    this.courseUserBirthday = courseUserBirthday;
  }

  public String getCourseUserEmail() {
    return courseUserEmail;
  }

  public void setCourseUserEmail(String courseUserEmail) {
    this.courseUserEmail = courseUserEmail;
  }

  public String getCourseUserURL() {
    return courseUserURL;
  }

  public void setCourseUserURL(String courseUserURL) {
    this.courseUserURL = courseUserURL;
  }

  public String getCourseUserTel() {
    return courseUserTel;
  }

  public void setCourseUserTel(String courseUserTel) {
    this.courseUserTel = courseUserTel;
  }

  public String getCourseUserSystemRole() {
    return courseUserSystemRole;
  }

  public void setCourseUserSystemRole(String courseUserSystemRole) {
    this.courseUserSystemRole = courseUserSystemRole;
  }

  public String getCourseUserInstitutionRole() {
    return courseUserInstitutionRole;
  }

  public void setCourseUserInstitutionRole(String courseUserInstitutionRole) {
    this.courseUserInstitutionRole = courseUserInstitutionRole;
  }
}
