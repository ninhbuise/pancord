package il.panda.pancord.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

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

    public static String numericStringValue(String origVal) {
        String retVal = "";
        if (StringUtils.hasText(origVal)) {
            retVal = origVal.replaceAll("[^0-9]", "");
        }
        return retVal;
    }

    public static long safeParseStringValueToLong(String valToParse, long defaultVal) {
        long retVal = defaultVal;
        if (StringUtils.hasText(valToParse)) {
            try {
                retVal = Long.parseLong(valToParse);
            } catch (NumberFormatException ex) {
                // TODO: log the invalid long int val in text format.
            }
        }
        return retVal;
    }
}
