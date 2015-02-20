/*
 * Copyright [2015] [Stefan Pröll]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package GenericTools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Helper methods for reading properties files
 */
public class PropertyHelpers {

    /**
     * Read the properties file and return the properties object.
     *
     * @param filename
     * @return
     */
    public static Properties readPropertyFile(String filename) {

        Properties prop = new Properties();
        InputStream input = null;

        try {


            input = PropertyHelpers.class.getClassLoader().getResourceAsStream(filename);

            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return null;
            } else {
                System.out.println("File found");
            }

            //load a properties file from class path, inside static method
            prop.load(input);


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return prop;
    }

    /**
     * Reads the parameter file and retrieves the value specified
     *
     * @param parameterName
     * @return
     */
    public static int getIntegerParameterFromPropertyFile(String filename, String parameterName) {

        int parameterValue = -1;
        // get the properties object
        Properties prop = PropertyHelpers.readPropertyFile(filename);
        parameterValue = Integer.parseInt(prop.getProperty(parameterName));

        return parameterValue;
    }

    /**
     * Print all elements from a properties file
     *
     * @param filename
     */
    public static void printPropertiesFile(String filename) {

        Properties prop = PropertyHelpers.readPropertyFile(filename);

        Enumeration<?> e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = prop.getProperty(key);
            System.out.println("Key : " + key + ", Value : " + value);
        }


    }

}