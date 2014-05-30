
package cmsMini;

import java.io.Serializable;

/**
 * This class will hold information of a user in the course this "cms-mini" is managing.
 * @author Muhammad Wannous
 */
public class User implements Serializable{
  /*<person><userid>*/
  private String userID;
  /*<person password>*/
  private String userMD5Pass;
  /*<systemrole systemroletype>*/
  private String userRole;

  public User(String userID, String userMD5Pass, String userRole) {
    this.userID = userID;
    this.userMD5Pass = userMD5Pass;
    this.userRole = userRole;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getUserMD5Pass() {
    return userMD5Pass;
  }

  public void setUserMD5Pass(String userMD5Pass) {
    this.userMD5Pass = userMD5Pass;
  }

  public String getUserRole() {
    return userRole;
  }

  public void setUserRole(String userRole) {
    this.userRole = userRole;
  }
}
