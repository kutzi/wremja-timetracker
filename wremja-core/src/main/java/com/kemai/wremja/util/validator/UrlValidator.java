package com.kemai.wremja.util.validator;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlValidator implements Validator {

    @Override
    public boolean validate(String value) {
        try {
            new URL(value);
            return true;
        } catch (NullPointerException e) {
            return false;
        } catch (MalformedURLException e) {
           return false;
        }
    }

}
