module com.udacity.catpoint.image {
    exports com.udacity.catpoint.security.service;
    requires org.slf4j;
    requires java.desktop;  // For BufferedImage (java.awt.image)
}
