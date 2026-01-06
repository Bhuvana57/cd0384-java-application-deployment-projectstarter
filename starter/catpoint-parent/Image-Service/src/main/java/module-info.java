module com.udacity.catpoint.image {
    exports com.udacity.image.service;
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.core;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.rekognition;
    requires software.amazon.awssdk.services.rekognition;
    requires java.desktop;
    requires org.slf4j;
}
