package grwm.develop.utils;

import grwm.develop.auth.properties.AuthProperties;

public final class URLUtils {

    private URLUtils() {
    }

    public static String createURL(AuthProperties authProperties) {
        return authProperties.mapping();
    }
}
