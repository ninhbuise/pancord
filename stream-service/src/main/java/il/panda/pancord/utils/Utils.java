package il.panda.pancord.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Utils {

    // Minio response message
    public static String UPLOAD_SUCCESS = "Upload Successfully";
    public static String UPLOAD_FAIL = "Upload Unsuccessfully";
    public static String DOWNLOAD_FAIL = "Download Unsuccessfully";
    public static String DELETE_SUCCESS = "Delete Successfully";
    public static String DELETE_FAIL = "Delete Unsuccessfully";

    //region Response Const
    private static final String RESPONSE_CODE = "status";
    private static final String RESPONSE_MESSAGE = "message";
    private static final String RESPONSE_DATA = "data";

    public static ResponseEntity<?> appendResponse(HttpStatus status, String message, Object data) {
        var responseMessage = Objects.isNull(message) ? status.getReasonPhrase() : message;
        Map<String, Object> response = new HashMap<>();
        response.put(RESPONSE_CODE, status.value());
        response.put(RESPONSE_MESSAGE, responseMessage);
        response.put(RESPONSE_DATA, data);
        return new ResponseEntity<>(response, status);
    }
}
