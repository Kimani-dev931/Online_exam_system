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


    private String iothreads;
    private String workerthreads;

    private String undertowserverport;

    private String undertowserverhost;

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

    public String getiothreads() {return iothreads;}

    public void setiothreads(String iothreads) {this.iothreads = iothreads;}


    public String getworkerthreads() {return workerthreads;}

    public void setworkerthreads(String workerthreads) {this.workerthreads = workerthreads;}
    public String getundertowserverport() {return undertowserverport;}
    public void setundertowserverport(String undertowserverport) {this.undertowserverport = undertowserverport;}

    public String getundertowserverhost() {return undertowserverhost; }

    public void setundertowserverhost(String  undertowserverhost) {
        this.undertowserverhost =  undertowserverhost;
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
            Node databaseTypeNode = (Node) xpath.compile("/DATABASE-CONFIG/DATABASE-TYPE").evaluate(doc, XPathConstants.NODE);
            if (databaseTypeNode != null) {
                databaseTypeNode.setTextContent(this.databaseType);
            }

            // Update database-name
            Node databaseNameNode = (Node) xpath.compile("/DATABASE-CONFIG/DATABASE-NAME").evaluate(doc, XPathConstants.NODE);
            if (databaseNameNode != null) {
                databaseNameNode.setTextContent(this.databaseName);
            }

            // Update database-host
            Node databaseHostNode = (Node) xpath.compile("/DATABASE-CONFIG/DATABASE-HOST").evaluate(doc, XPathConstants.NODE);
            if (databaseHostNode != null) {
                databaseHostNode.setTextContent(this.databaseHost);
            }


            // Update username
            Node usernameNode = (Node) xpath.compile("/DATABASE-CONFIG/USERNAME").evaluate(doc, XPathConstants.NODE);
            if (usernameNode != null) {
                usernameNode.setTextContent(this.username);
                usernameNode.getAttributes().getNamedItem("ENCRYPTED").setTextContent(this.isUsernameEncrypted ? "YES" : "NO");
            }

            // Update password
            Node passwordNode = (Node) xpath.compile("/DATABASE-CONFIG/PASSWORD").evaluate(doc, XPathConstants.NODE);
            if (passwordNode != null) {
                passwordNode.setTextContent(this.password);
                passwordNode.getAttributes().getNamedItem("ENCRYPTED").setTextContent(this.isPasswordEncrypted ? "YES" : "NO");
            }
            // Update database-host
            Node iothreadsNode = (Node) xpath.compile("/DATABASE-CONFIG/IO-THREADS").evaluate(doc, XPathConstants.NODE);
            if (iothreadsNode != null) {
                iothreadsNode.setTextContent(iothreads);
            }
            // Update database-host
            Node workerthreadsNode = (Node) xpath.compile("/DATABASE-CONFIG/WORKER-THREADS").evaluate(doc, XPathConstants.NODE);
            if (workerthreadsNode != null) {
                workerthreadsNode.setTextContent(workerthreads);

            }
            // Update database-host
            Node undertowserverportNode = (Node) xpath.compile("/DATABASE-CONFIG/UNDERTOW-SERVER-PORT").evaluate(doc, XPathConstants.NODE);
            if (undertowserverportNode != null) {
                undertowserverportNode.setTextContent(undertowserverport);

            }
            // Update database-host
            Node undertowserverhostNode = (Node) xpath.compile("/DATABASE-CONFIG/UNDERTOW-SERVER-HOST").evaluate(doc, XPathConstants.NODE);
            if (undertowserverhostNode!= null) {
                undertowserverhostNode.setTextContent(this.undertowserverhost);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
