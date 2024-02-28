package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class DatabaseConfig {

    private String databaseType;
    private String databaseName;
    private String databaseHost;
    private String username;
    private String password;


//    private int iothreads;
//    private int workerthreads;
//
//    private int undertowserverport;
//    private String undertowserverhost;
//

    private boolean isUsernameEncrypted;
    private boolean isPasswordEncrypted;

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }


//    public int getiothreads() {
//        return iothreads;
//    }
//
//    public void setiothreads(int iothreads) {
//        this.iothreads = iothreads;
//    }
//
//
//    public int getworkerthreads() {
//        return workerthreads;
//    }
//
//    public void setworkerthreads(int workerthreads) {
//        this.workerthreads = workerthreads;
//    }
//
//    public int getundertowserverport() {
//        return undertowserverport;
//    }
//
//    public void setundertowserverport(int undertowserverport) {
//        this.undertowserverport = undertowserverport;
//    }
//
//    public String getundertowserverhost() {return undertowserverhost; }
//
//    public void setundertowserverhost(String  undertowserverhost) {
//        this.undertowserverhost =  undertowserverhost;
//    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUsernameEncrypted() {
        return isUsernameEncrypted;
    }

    public void setUsernameEncrypted(boolean usernameEncrypted) {
        isUsernameEncrypted = usernameEncrypted;
    }

    public boolean isPasswordEncrypted() {
        return isPasswordEncrypted;
    }

    public void setPasswordEncrypted(boolean passwordEncrypted) {
        isPasswordEncrypted = passwordEncrypted;
    }

    // Update the XML element with the current property values
    public void updateXmlElement(Document doc) {
        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            // Update database-type
            Node databaseTypeNode = (Node) xpath.compile("/database-config/database-type").evaluate(doc, XPathConstants.NODE);
            if (databaseTypeNode != null) {
                databaseTypeNode.setTextContent(this.databaseType);
            }

            // Update database-name
            Node databaseNameNode = (Node) xpath.compile("/database-config/database-name").evaluate(doc, XPathConstants.NODE);
            if (databaseNameNode != null) {
                databaseNameNode.setTextContent(this.databaseName);
            }

            // Update database-host
            Node databaseHostNode = (Node) xpath.compile("/database-config/database-host").evaluate(doc, XPathConstants.NODE);
            if (databaseHostNode != null) {
                databaseHostNode.setTextContent(this.databaseHost);
            }

            // Update username
            Node usernameNode = (Node) xpath.compile("/database-config/username").evaluate(doc, XPathConstants.NODE);
            if (usernameNode != null) {
                usernameNode.setTextContent(this.username);
                usernameNode.getAttributes().getNamedItem("ENCRYPTED").setTextContent(this.isUsernameEncrypted ? "YES" : "NO");
            }

            // Update password
            Node passwordNode = (Node) xpath.compile("/database-config/password").evaluate(doc, XPathConstants.NODE);
            if (passwordNode != null) {
                passwordNode.setTextContent(this.password);
                passwordNode.getAttributes().getNamedItem("ENCRYPTED").setTextContent(this.isPasswordEncrypted ? "YES" : "NO");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
