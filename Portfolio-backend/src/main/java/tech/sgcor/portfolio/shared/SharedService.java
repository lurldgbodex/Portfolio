package tech.sgcor.portfolio.shared;

public class SharedService {
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
    public static String BASE_URL = "/api/users/portfolio/";
}
