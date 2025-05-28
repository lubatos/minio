//import com.example.service.MinioService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
//import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

//import com.example.service.MinioService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.InputStream;
import jakarta.enterprise.context.ApplicationScoped;

@Path("/api/minio")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class MinioController {

    @Inject
    MinioService minioService;

    @POST
    @Path("/object/upload")
    //@Consumes(MediaType.APPLICATION_OCTET_STREAM)
    //@Produces(MediaType.APPLICATION_JSON)
    public Response uploadObject(
            @HeaderParam("X-Bucket-Name") String bucketName,
            @HeaderParam("X-Object-Name") String objectName,
            @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
            @HeaderParam(HttpHeaders.CONTENT_LENGTH) long contentLength,
            InputStream fileStream) {
        System.out.println(" uploadObject ");
        try {
            if (bucketName == null || objectName == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Bucket name and object name are required"))
                        .build();
            }

            minioService.uploadObject(
                    bucketName,
                    objectName,
                    fileStream,
                    contentLength,
                    contentType != null ? contentType : "application/octet-stream"
            );

            return Response.ok()
                    .entity(Map.of(
                            "message", "File uploaded successfully",
                            "bucket", bucketName,
                            "object", objectName
                    ))
                    .build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity(Map.of(
                            "error", "Upload failed",
                            "message", e.getMessage()
                    ))
                    .build();
        }
    }

    @POST
    @Path("/buckets")
    public Response listBuckets() {
        try {
            List<String> buckets = minioService.listBuckets();
            return Response.ok(buckets).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

    @POST
    @Path("/bucket/create")
    public Response createBucket(Map<String, String> request) {
        try {
            String bucketName = request.get("bucketName");
            minioService.createBucket(bucketName);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

    @POST
    @Path("/objects/list")
    public Response listObjects(Map<String, String> request) {
        try {
            String bucketName = request.get("bucketName");
            List<String> objects = minioService.listObjects(bucketName);
            return Response.ok(objects).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

//    @POST
//    @Path("/object/upload")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response uploadObject(@MultipartForm FileUpload file,
//                                 @RestForm String bucketName,
//                                 @RestForm String objectName) {
//        try {
//            minioService.uploadObject(bucketName, objectName,
//                    file.uploadedFile().toFile().toPath(),
//                    file.contentType());
//            return Response.ok().build();
//        } catch (Exception e) {
//            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
//        }
//    }

    @POST
    @Path("/object/download")
    public Response downloadObject(Map<String, String> request) {
        try {
            String bucketName = request.get("bucketName");
            String objectName = request.get("objectName");
            InputStream object = minioService.downloadObject(bucketName, objectName);
            return Response.ok(object)
                    .header("Content-Disposition", "attachment; filename=\"" + objectName + "\"")
                    .build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

    @POST
    @Path("/object/delete")
    public Response deleteObject(Map<String, String> request) {
        try {
            String bucketName = request.get("bucketName");
            String objectName = request.get("objectName");
            minioService.deleteObject(bucketName, objectName);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }


}