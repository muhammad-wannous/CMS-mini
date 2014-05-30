package cmsMini;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is used for resolving the DTD file defined in our xml.
 *
 * @author Muhammad Wannous
 */
public class DTDEntityResolver implements EntityResolver {

  private ServletContext application;

  public void setApplication(ServletContext app) {
    this.application = app;
  }

  @Override
  public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
    if ((systemId != null && systemId.contains("ims_epv1p1.dtd"))
            || (publicId != null && publicId.contains("ims_epv1p1.dtd"))) {
      InputStream dtdStream = application.getResourceAsStream("/dtd/ims_epv1p1.dtd");
      return new InputSource(dtdStream);
    } else if ((systemId != null && systemId.contains("cms-mini_cdv1.dtd"))
            || (publicId != null && publicId.contains("cms-mini_cdv1.dtd"))) {
      InputStream dtdStream = application.getResourceAsStream("/dtd/cms-mini_cdv1.dtd");
      return new InputSource(dtdStream);
    } else {
      return null;
    }
  }
}
