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

import java.util.HashSet;

import org.sourceid.saml20.adapter.conf.Configuration;
import org.sourceid.saml20.adapter.gui.FieldDescriptor;
import org.sourceid.saml20.adapter.gui.JdbcDatastoreFieldDescriptor;
import org.sourceid.saml20.adapter.gui.SelectFieldDescriptor;
import org.sourceid.saml20.adapter.gui.TextAreaFieldDescriptor;
import org.sourceid.saml20.adapter.gui.TextFieldDescriptor;
import org.sourceid.saml20.adapter.gui.validation.impl.RequiredFieldValidator;
import org.sourceid.saml20.adapter.gui.RadioGroupFieldDescriptor;
import org.sourceid.saml20.adapter.gui.validation.impl.IntegerValidator;

import com.pingidentity.sdk.GuiConfigDescriptor;
import com.pingidentity.sdk.PluginDescriptor;

/**
 * The SQLPasswordCredentialValidatorConfiguration class contains PingFederate web GUI configuration parameters for the SQLPasswordCredentialValidator.
 */
public class SQLPasswordCredentialValidatorConfiguration {

	// initialize configuration object
    protected Configuration configuration = null;
    
    private static final String JDBC_DATASOURCE = "JDBC Datasource";
    private static final String JDBC_DATASOURCE_DESC = "The JDBC DataSource.";
    private static final String DB_QUERY = "Database Query";
    private static final String DB_QUERY_DESC = "The query to retrieve password hash from the database (use ? for the username bind variable).";
    private static final String HASH_COLUMN_NAME = "Password Hash Column Name";
    private static final String HASH_COLUMN_NAME_DESC = "The column name of the returned password hash.";

    protected final String queryOption_Query = "SQL Query";

    protected String databaseDatasource = null;
    protected String dbQuery = null;
    protected String hashColumnName = null;

	/**
	 * This method is called by the PingFederate server to push configuration values entered by the administrator via
	 * the dynamically rendered GUI configuration screen in the PingFederate administration console. Your implementation
	 * should use the {@link Configuration} parameter to configure its own internal state as needed. <br/>
	 * <br/>
	 * Each time the PingFederate server creates a new instance of your plugin implementation this method will be
	 * invoked with the proper configuration. All concurrency issues are handled in the server so you don't need to
	 * worry about them here. The server doesn't allow access to your plugin implementation instance until after
	 * creation and configuration is completed.
	 * 
	 * @param configuration
	 *            the Configuration object constructed from the values entered by the user via the GUI.
	 */    
    public void configure(Configuration configuration) {
        this.databaseDatasource = configuration.getFieldValue(JDBC_DATASOURCE);
        this.dbQuery = configuration.getFieldValue(DB_QUERY);
        this.hashColumnName = configuration.getFieldValue(HASH_COLUMN_NAME);
    }

	/**
	 * Returns the {@link PluginDescriptor} that describes this plugin to the PingFederate server. This includes how
	 * PingFederate will render the plugin in the administrative console, and metadata on how PingFederate will treat
	 * this plugin at runtime.
	 * 
	 * @return A {@link PluginDescriptor} that describes this plugin to the PingFederate server.
	 */    
    public PluginDescriptor getPluginDescriptor(SQLPasswordCredentialValidator scv) {
    	RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
    	IntegerValidator integerFieldValidator = new IntegerValidator();
    	
    	GuiConfigDescriptor guiDescriptor = new GuiConfigDescriptor();
		guiDescriptor.setDescription("Bcrypt and SHA-1 password credential validator");
		
        FieldDescriptor jdbcDS = new JdbcDatastoreFieldDescriptor(JDBC_DATASOURCE, JDBC_DATASOURCE_DESC);
        jdbcDS.addValidator(requiredFieldValidator);
        guiDescriptor.addField(jdbcDS);

        TextAreaFieldDescriptor dbQuery = new TextAreaFieldDescriptor(DB_QUERY, DB_QUERY_DESC, 5, 75);
        dbQuery.setDefaultValue("SELECT password as hash FROM users WHERE username = ?");
        guiDescriptor.addField(dbQuery);

        TextFieldDescriptor hashColumnName = new TextFieldDescriptor(HASH_COLUMN_NAME, HASH_COLUMN_NAME_DESC);
        hashColumnName.addValidator(requiredFieldValidator);
        hashColumnName.setDefaultValue("hash");
        guiDescriptor.addField(hashColumnName);

        PluginDescriptor pluginDescriptor = new PluginDescriptor("Bcrypt and SHA-1 password credential validator", scv, guiDescriptor);
		//pluginDescriptor.setAttributeContractSet(Collections.singleton(USERNAME));
        HashSet<String> attributes = new HashSet<String>();
        attributes.add("username");
        pluginDescriptor.setAttributeContractSet(attributes);
		pluginDescriptor.setSupportsExtendedContract(false);
    	
		return pluginDescriptor;
    }
    

	/**
	 * The buildName method returns the name and version from the information in META-INF/MANIFEST.MF, in order to name the jar within this package.
	 * 
	 * @return name of the plug-in
	 */
	private String buildName() {
		Package plugin = SQLPasswordCredentialValidator.class.getPackage();
		String title = plugin.getImplementationTitle(); // Implementation-Title
		String version = plugin.getImplementationVersion(); // Implementation-Version:
		String name = title + " " + version;
		return name;
	}     
}
