package cmsMini;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Muhammad Wannous
 */
public class ZipFileToDrive {

  private Drive service;
  private String parentFolderIdString;
  private String zipFileURLString;

  public ZipFileToDrive(Drive service, String parentFolderIdString, String zipFileURLString) {
    this.service = service;
    this.parentFolderIdString = parentFolderIdString;
    this.zipFileURLString = zipFileURLString;
  }

  public Drive getService() {
    return service;
  }

  public void setService(Drive service) {
    this.service = service;
  }

  public String getParentFolderIdString() {
    return parentFolderIdString;
  }

  public void setParentFolderIdString(String parentFolderIdString) {
    this.parentFolderIdString = parentFolderIdString;
  }

  public String getZipFile() {
    return zipFileURLString;
  }

  public void setZipFile(String zipFileURLString) {
    this.zipFileURLString = zipFileURLString;
  }

  public boolean expandZipFileInDrive() throws IOException {
    ZipEntry zipEntry;
    String parentFolderId;
    ZipInputStream zipInputStream = new ZipInputStream(service.getRequestFactory()
            .buildGetRequest(new GenericUrl(zipFileURLString)).execute().getContent());
    zipEntry = zipInputStream.getNextEntry();
    while (zipEntry != null) {
      String fileNameString = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1,
              zipEntry.getName().length());
      if (zipEntry.getName().contains("/")) {
        /*We need to check the whole tree*/
        String fullPathString = zipEntry.getName().substring(0, zipEntry.getName().lastIndexOf("/") + 1);
        /*Keep the last "/"*/
        String tempFolderString = fullPathString.substring(0, fullPathString.indexOf("/"));
        fullPathString = fullPathString.substring(fullPathString.indexOf("/") + 1, fullPathString.length());
        String tempParentFolderIdString = parentFolderIdString;
        while (tempFolderString != null && !tempFolderString.equals("")) {
          Drive.Files.List listRequest = service.files().list().setQ(
                  "mimeType = 'application/vnd.google-apps.folder' "
                  + "and title = '" + tempFolderString + "' "
                  + "and '" + tempParentFolderIdString + "' in parents ");
          FileList folders = listRequest.execute();
          if (folders.getItems().isEmpty()) {
            File newFolder = new File();
            newFolder.setTitle(tempFolderString);
            newFolder.setMimeType("application/vnd.google-apps.folder");
            newFolder.setParents(Arrays.asList(new ParentReference().setId(tempParentFolderIdString)));
            Drive.Files.Insert createFolderInsert = service.files().insert(newFolder);
            tempParentFolderIdString = createFolderInsert.execute().getId();
          } else{
            tempParentFolderIdString = folders.getItems().get(0).getId();
          }
          if (tempFolderString.contains("/")) {
            tempFolderString = fullPathString.substring(0, fullPathString.indexOf("/"));
            fullPathString = fullPathString.substring(fullPathString.indexOf("/") + 1, fullPathString.length());
          } else {
            tempFolderString = "";
          }
        }
        parentFolderId = tempParentFolderIdString;
      } else{
        parentFolderId = parentFolderIdString;
      }
      File body = new File();
      body.setTitle(fileNameString);
      body.setParents(Arrays.asList(new ParentReference().setId(parentFolderId)));
      InputStreamContent contents = new InputStreamContent(null, zipInputStream);
      contents.setCloseInputStream(false);
      contents.setLength(zipEntry.getSize());
      Drive.Files.Insert insertRequest = service.files().insert(body, contents);
      insertRequest.execute();
      zipEntry = zipInputStream.getNextEntry();
    }
    return false;
  }
}
