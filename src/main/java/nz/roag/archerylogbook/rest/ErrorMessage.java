package nz.roag.archerylogbook.rest;

public class ErrorMessage {

    public static String getErrorJson(String errorStatus, String errorMessage, String path) {
        return """
                {
                   "status": "%s",
                   "errorMessage": "%s",
                   "path": "/archers/%s/scores"   
                }
                """
                .formatted(errorStatus, errorMessage, path);
    }
}
