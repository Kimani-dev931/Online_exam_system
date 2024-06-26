package org.example;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.example.handlers.authentication.LoginTeacher;
import org.example.rest.RestAPIServer;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.util.*;
import java.sql.*;
public class MainApp {
    public static Connection connection = null;
    private static String connectionString;
    private static String decryptedUsername;
    private static String decryptedPassword;

    public static DatabaseConfig config = new DatabaseConfig();
    private static final String SECRET_KEY = SecretKeyConfig.getSecretKey();
    public static String getConnectionString() {
        return connectionString;
    }

    public static String getDecryptedUsername() {
        return decryptedUsername;
    }

    public static String getDecryptedPassword() {
        return decryptedPassword;
    }
    public static void main(String[] args) {


        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("config.xml"));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();


            List<DatabaseConfig> configs = new ArrayList<>();


//            DatabaseConfig config = new DatabaseConfig();

            String databaseType = (String) xpath.compile("/DATABASE-CONFIG/DATABASE-TYPE").evaluate(doc, XPathConstants.STRING);
            config.setDatabaseType(databaseType);

            String databaseName = (String) xpath.compile("/DATABASE-CONFIG/DATABASE-NAME").evaluate(doc, XPathConstants.STRING);
            config.setDatabaseName(databaseName);

            String databaseHost = (String) xpath.compile("/DATABASE-CONFIG/DATABASE-HOST").evaluate(doc, XPathConstants.STRING);
            config.setDatabaseHost(databaseHost);

            String username = (String) xpath.compile("/DATABASE-CONFIG/USERNAME/text()").evaluate(doc, XPathConstants.STRING);
            config.setUsername(username);

            String password = (String) xpath.compile("/DATABASE-CONFIG/PASSWORD/text()").evaluate(doc, XPathConstants.STRING);
            config.setPassword(password);

            String usernameEncrypted = (String) xpath.compile("/DATABASE-CONFIG/USERNAME/@ENCRYPTED").evaluate(doc, XPathConstants.STRING);
            config.setUsernameEncrypted("YES".equals(usernameEncrypted));

            String passwordEncrypted = (String) xpath.compile("/DATABASE-CONFIG/PASSWORD/@ENCRYPTED").evaluate(doc, XPathConstants.STRING);
            config.setPasswordEncrypted("YES".equals(passwordEncrypted));

            String iothreads = (String) xpath.compile("/DATABASE-CONFIG/IO-THREADS").evaluate(doc, XPathConstants.STRING);
            config.setiothreads(iothreads);


            String workerthreads = (String) xpath.compile("/DATABASE-CONFIG/WORKER-THREADS").evaluate(doc, XPathConstants.STRING);
            config.setworkerthreads(workerthreads);

            String undertowserverport = (String) xpath.compile("/DATABASE-CONFIG/UNDERTOW-SERVER-PORT").evaluate(doc, XPathConstants.STRING);
            config.setundertowserverport(undertowserverport);

            String undertowserverhost = (String) xpath.compile("/DATABASE-CONFIG/UNDERTOW-SERVER-HOST").evaluate(doc, XPathConstants.STRING);
            config.setundertowserverhost(undertowserverhost);



            configs.add(config);


            String usernameEncryptedAttribute = (String) xpath.compile("/DATABASE-CONFIG/USERNAME/@ENCRYPTED").evaluate(doc, XPathConstants.STRING);
            boolean shouldEncryptUsername = !"YES".equals(usernameEncryptedAttribute);


            String passwordEncryptedAttribute = (String) xpath.compile("/DATABASE-CONFIG/PASSWORD/@ENCRYPTED").evaluate(doc, XPathConstants.STRING);
            boolean shouldEncryptPassword = !"YES".equals(passwordEncryptedAttribute);

            if (shouldEncryptUsername) {
                config.setUsername(encrypt(config.getUsername(), SECRET_KEY));
                config.setUsernameEncrypted(true);
            }

            if (shouldEncryptPassword) {
                config.setPassword(encrypt(config.getPassword(), SECRET_KEY));
                config.setPasswordEncrypted(true);
            }

            config.updateXmlElement(doc);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("config.xml"));
            transformer.transform(source, result);


             connectionString = "";
            if ("MySQL".equalsIgnoreCase(config.getDatabaseType())) {
                connectionString = "jdbc:mysql://" + config.getDatabaseHost() + "/" + config.getDatabaseName();

            } else if ("PostgreSQL".equalsIgnoreCase(config.getDatabaseType())) {
                connectionString = "jdbc:postgresql://" + config.getDatabaseHost() + "/" + config.getDatabaseName();

            } else if ("MicrosoftSQL".equalsIgnoreCase(config.getDatabaseType())) {
                connectionString = "jdbc:sqlserver://" + config.getDatabaseHost() + ";databaseName=" + config.getDatabaseName();

            }

            decryptedUsername = config.isUsernameEncrypted() ? decrypt(config.getUsername(), SECRET_KEY) : config.getUsername();
            decryptedPassword = config.isPasswordEncrypted() ? decrypt(config.getPassword(), SECRET_KEY) : config.getPassword();

            RestAPIServer.start();


            String filterString = "date_created:bt:2024-02-27,2024-02-30";
            String whereClause = Filter.generateWhereClause(filterString);
            System.out.println(whereClause);

//            int teacherId = 2;
//
//            // Call the resetAccountLock method with the teacherId
//            Response response = LoginTeacher.resetAccountLock(teacherId);
//
//            // Check the response and print a message to the console
//            if (response.getStatusCode() == 200) {
//                System.out.println("Account unlocked successfully.");
//            } else {
//                System.out.println("Failed to unlock account.");
//            }





        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String encrypt(String input, String key) throws Exception {
        byte[] keyBytes = hexStringToByteArray(key);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Key must be 32 bytes long");
        }

        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static String decrypt(String encryptedText, String key) throws Exception {
        byte[] keyBytes = hexStringToByteArray(key);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Key must be 32 bytes long");
        }

        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


}



