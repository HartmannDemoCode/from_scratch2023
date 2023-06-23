package dk.cphbusiness.utils;

import dk.cphbusiness.config.HibernateConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
    private InputStream inputStream;
    public static String getPomProp(String propName)  {
        InputStream is = dk.cphbusiness.utils.Utils.class.getClassLoader().getResourceAsStream("properties-from-pom.properties");
        Properties pomProperties = new Properties();
        try {
            pomProperties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pomProperties.getProperty(propName);
    }
}
