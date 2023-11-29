package com.sgcor.shopply.shared;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SharedService {
    public boolean isNotValidPassword(String password) {
        if (password.length() < 6) {
            return true;
        }

        // Check for at least one special character
        Pattern specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher specialCharMatcher = specialCharPattern.matcher(password);
        if (!specialCharMatcher.find()) {
            return true;
        }

        // Check for at leas one number
        Pattern numberpattern = Pattern.compile("[0-9]");
        Matcher numberMatcher = numberpattern.matcher(password);
        if (!numberMatcher.find()) {
            return true;
        }

        // Check for at least one uppercase character
        Pattern uppercasePattern = Pattern.compile("[A-Z]");
        Matcher uppercaseMatcher = uppercasePattern.matcher(password);
        if (!uppercaseMatcher.find()) {
            return true;
        }

        // Check for at least one lowercase character
        Pattern lowercasePattern = Pattern.compile("[a-z]");
        Matcher lowercaseMatcher = lowercasePattern.matcher(password);
        return !lowercaseMatcher.find();
    }
}
