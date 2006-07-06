/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Date;
import java.sql.Timestamp;

public class IntactNode extends BasicObject {



    /**
     * ftp attributes: the address,directory, login and password
     * to access to the directory where xml dump files are available.
     * Format: One string
     */
    protected String ftpAddress;

    protected String ftpDirectory;

    protected String ftpLogin;

    protected String ftpPassword;


    protected int lastCheckId;

    protected int lastProvidedId;


    protected Timestamp lastProvidedDate=new Timestamp(0);

    protected int rejected = 0;

    protected String ownerPrefix;

  ///////////////////////////////////////
  //access methods for attributes


    public String getFtpAddress() {
        return ftpAddress;
    }
    public void setFtpAddress(String ftpAddress) {
        this.ftpAddress = ftpAddress;
    }

    public String getFtpDirectory() {
        return ftpDirectory;
    }
    public void setFtpDirectory(String ftpDirectory) {
        this.ftpDirectory = ftpDirectory;
    }

    public String getFtpLogin() {
        return ftpLogin;
    }

    public void setFtpLogin(String ftpLogin) {
        this.ftpLogin = ftpLogin;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword= ftpPassword;
    }

    public int getLastCheckId() {
        return lastCheckId;
    }
    public void setLastCheckId(int lastCheckId) {
        this.lastCheckId = lastCheckId;
    }

    public int getLastProvidedId() {
        return lastProvidedId;
    }
    public void setLastProvidedId(int lastProvidedId) {
        this.lastProvidedId = lastProvidedId;
    }

    public Timestamp getLastProvidedDate() {
        return lastProvidedDate;

    }
    public void setLastProvidedDate(Timestamp lastProvidedDate) {
        this.lastProvidedDate = lastProvidedDate;
    }
    public String getOwnerPrefix() {
        return ownerPrefix;
    }
    public void setOwnerPrefix(String ownerPrefix) {
        this.ownerPrefix = ownerPrefix;
    }

    public int getRejected() {
        return rejected;
    }
    public void setRejected(int rejected) {
        this.rejected = rejected;
    }
}
