/**
 * *************************************************************************
 * Copyright (C) 2019 Ping Identity Corporation All rights reserved.
 *
 * The contents of this file are subject to the terms of the Ping Identity
 * Corporation SDK Developer Guide.
 *
 * Purpose built POC for proof of concept testing
 *
 * Validates password against JDBC with Bcrypt or SHA-1, depending on
 * payload
 *
 *************************************************************************
 */
package com.pingidentity.rsa.pcv.sql.tpci;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.DecoderException;
import org.sourceid.saml20.adapter.conf.Configuration;
import org.sourceid.util.log.AttributeMap;

import com.pingidentity.sdk.PluginDescriptor;
import com.pingidentity.sdk.password.PasswordCredentialValidator;
import com.pingidentity.sdk.password.PasswordValidationException;
import com.pingidentity.sdk.password.PasswordCredentialValidatorAuthnException;
import com.pingidentity.access.DataSourceAccessor;

/**
 * The SQLPasswordCredentialValidator class validates username and password credentials against a SQL database via a JDBC driver.
 */
public class SQLPasswordCredentialValidator implements PasswordCredentialValidator {
	
	// initialize logger into PF
    private final Log logger = LogFactory.getLog(this.getClass());
    
    // instantiate and obtain config object
    private SQLPasswordCredentialValidatorConfiguration config = new SQLPasswordCredentialValidatorConfiguration();

    /**
     * The decodeToBytes method decodes an encoded string based on binary encoding type.
     *
     * @param encodedString
     * @return
     * @throws DecoderException
     */

    private Boolean validatePassword(String username, String password, String db_password_hash) throws PasswordValidationException, PasswordCredentialValidatorAuthnException {

        // test if db_password_hash begins with Bcrypt

        if (db_password_hash.startsWith("bcrypt")) {

            logger.debug("validatePassword :: Bcrypt comparison");

            Boolean passBcrypt = false;

            // replace root prefix of bcrypt$$2b$ to $2a$

            db_password_hash = db_password_hash.replace("bcrypt$$2b$", "$2a$");

            try {

                passBcrypt = BCrypt.checkpw(password, db_password_hash);

            } catch (Exception ex) {

                logger.debug("validatePassword :: Bcrypt failed", ex);
                logger.debug("validatePassword :: db hash: " + db_password_hash);

                return false;

            }

            if (passBcrypt) {

                logger.debug("validatePassword :: Bcrypt match");

                return true;

            } else {

                logger.debug("validatePassword :: Bcrypt failed");
                logger.debug("validatePassword :: db hash: " + db_password_hash);

                return false;

            }

        // test if db_password_hash begins with SHA-1

        } else if (db_password_hash.startsWith("sha1")) {

            logger.debug("validatePassword :: SHA-1 comparison begin");

            // split the db_password_hash into an array on the $

            String[] array_database_hash = db_password_hash.split("\\$", 3);

            db_password_hash = array_database_hash[2];
            String salt = array_database_hash[1];

            try {

                String divorce = org.apache.commons.codec.digest.DigestUtils.sha1Hex(salt + password);

                if (db_password_hash.equalsIgnoreCase(divorce)) {

                    logger.debug("validatePassword :: SHA-1 match");

                    return true;

                } else {

                    logger.debug("validatePassword :: SHA-1 failed");
                    logger.debug("validatePassword :: db hash: " + db_password_hash);
                    logger.debug("validatePassword :: password hash: " + divorce);

                    return false;

                }

            } catch (Exception ex) {

                logger.debug("validatePassword :: SHA-1 failed", ex);
                logger.debug("validatePassword :: db hash: " + db_password_hash);

                return false;

            }

        } else {

            // db_password_hash unexpectedly does not begin with Bcrypt or SHA-1

            logger.debug("validatePassword :: unexpected database hash algorithm");
            logger.debug("validatePassword :: db hash: " + db_password_hash);

            return false;
        }

    }

    
   /**
	 * Validates the given username and password in the manner appropriate to the plugin implementation.
	 * 
	 * @param username
	 *            the given username/id
	 * @param password
	 *            the given password
	 * @return An AttributeMap with at least one entry representing the principal. The key of the entry does not matter,
	 *         so long as the map is not empty. If the map is empty or null, the username and password combination is
	 *         considered invalid.
	 * @throws PasswordValidationException
	 *             runtime exception when the validator cannot process the username and password combination due to
	 *             system failure such as data source off line, host name unreachable, etc.
	 */
    @Override
    public AttributeMap processPasswordCredential(String username, String password) throws PasswordValidationException {
    	logger.debug("processPasswordCredential :: BEGIN");
    	
        AttributeMap attrs = null;

        ResultSet results = null;
        PreparedStatement stmt = null;
        Connection conn = null;

        logger.debug("processPasswordCredential :: username: " + username);


        try {
            if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {

                logger.debug("processPasswordCredential :: creating database connection");

                DataSourceAccessor dataSourceAccessor = new DataSourceAccessor();
                conn = dataSourceAccessor.getConnection(config.databaseDatasource);

                logger.debug("processPasswordCredential :: executing database query");
                stmt = conn.prepareStatement(config.dbQuery);
                stmt.setString(1, username);
                results = stmt.executeQuery();

                if (results.next()) {
                	
                	String db_password = null;
                	
                    logger.debug("processPasswordCredential :: user record found");

                    db_password = results.getString(config.hashColumnName);

                    if (validatePassword(username, password, db_password)) {
                        logger.debug("processPasswordCredential :: password verification success");
                        attrs = new AttributeMap();
                        attrs.put("username", username);
                    } else {
                        logger.debug("processPasswordCredential :: password verification failed");
                    }

                } else {
                    logger.debug("processPasswordCredential :: user record not found");

                    throw new PasswordCredentialValidatorAuthnException(false, "processPasswordCredential :: invalid username and/or password");
                }
            }
        } catch (PasswordCredentialValidatorAuthnException ex) {
            logger.debug("processPasswordCredential :: Exception is: " + ex + ", with message: " + ex.getMessageKey());
            throw new PasswordCredentialValidatorAuthnException(false, ex.getMessageKey());
        } catch (Exception ex) {
            logger.debug("Exception is " + ex);
            throw new PasswordValidationException("processPasswordCredential :: other error validating username/password", ex);
        } finally {
            try {
                if (results != null) {
                    results.close();
                }
                
                if (stmt != null) {
                    stmt.close();
                }
                
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                logger.debug("processPasswordCredential :: Exception is " + ex);
                throw new PasswordValidationException("processPasswordCredential :: other SQL error validating username/password", ex);
            }
        }

        logger.debug("processPasswordCredential :: END");
        return attrs;
    }

	/**
	 * The getSourceDescriptor method returns the configuration details.
	 */
	@Override
	public PluginDescriptor getPluginDescriptor() {
		return config.getPluginDescriptor(this);
	}

	/**
	 * The configure method sets the configuration details.
	 */
	@Override
	public void configure(Configuration configuration) {
		config.configure(configuration);
	}    
}
