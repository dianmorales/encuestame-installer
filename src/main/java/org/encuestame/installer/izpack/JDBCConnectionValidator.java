
/*
 ************************************************************************************
 * Copyright (C) 2001-2015 encuestame: system online surveys Copyright (C) 2015
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
package org.encuestame.installer.izpack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.izforge.izpack.api.data.InstallData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.izforge.izpack.api.installer.DataValidator;


/**
 * Validator
 * @author Morales, Diana  paola AT encuestame.org
 * @since  February, 27, 2015
 */
public class JDBCConnectionValidator implements DataValidator {
	/** **/
    private Log log = LogFactory.getLog(this.getClass());

    /** **/
    private String dbType;

    /** **/
    private String dbHostname;

    /** **/
    private String dbPort;

    /** **/
    private String dbName;

	/** **/
	private String dbUser;

	/** **/
	private String dbPassword;




    /**
     *  Validate Database JDBC Connection
     * @param argsData
     * @return
     */
    public Status validateData(InstallData argsData) {
		Status statusData = Status.ERROR;
		try {
            dbType = argsData.getVariable("db.type.selection");
            dbHostname = argsData.getVariable("db.hostname");
            dbPort = argsData.getVariable("db.port");
            dbName = argsData.getVariable("db.name");
            dbUser = argsData.getVariable("db.username");
            dbPassword = argsData.getVariable("db.password");

			log.debug("******************************************************************************************");
            log.debug(" Database type =" + dbType +
                      " Database Host=" + dbHostname +
                      " Database Port=" + dbPort +
                      " Database user =" + dbUser +
                      " Database Password=" + dbPassword +
                      " Database=" + dbName);
            log.debug("******************************************************************************************");

            registerJDBCDriver(this.dbType);


            try {
               verifyJDBCConnection(dbType, dbHostname, dbName, dbPort, dbUser, dbPassword);
                statusData = Status.OK;
            } catch (SQLException e) {
                System.out.println("Connection Failed! Check output console");
                e.printStackTrace();
                return statusData.ERROR;
            }
            } catch (Throwable throwable) {
                throwable.printStackTrace();

        }
        return statusData;
	}

    /**
	 * Loads the JDBC Driver
	 *
	 * @throws Throwable
	 */
	private void registerJDBCDriver(final String dbtype) throws Throwable {
        try {
            if (dbtype.equals("mysql")) {
                Class.forName("com.mysql.jdbc.Driver");
            } else if (dbtype.equals("postgres")) {
                Class.forName("org.postgresql.Driver");
            }
        } catch (ClassNotFoundException e) {
           log.debug("Where is your JDBC Driver?");
           e.printStackTrace();
           return;
        }
	}

    /**
     *
     * @param dbtype
     * @param hostname
     * @param dbname
     * @param port
     * @param username
     * @param password
     * @throws SQLException
     */
    private void verifyJDBCConnection(final String dbtype,
                                      final String hostname,
                                      final String dbname,
                                      final String port,
                                      final String username,
                                      final String password
                                      ) throws SQLException {

        String url = buildUrlConnection(dbtype, hostname, dbname, port, username, password);
        Connection connection = DriverManager.getConnection(url);
        connection.close();
    }

    /**
     *
     * @param dbtype
     * @param hostname
     * @param dbname
     * @param port
     * @param username
     * @param password
     * @return
     */
    private String buildUrlConnection(final String dbtype,
                                      final String hostname,
                                      final String dbname,
                                      final String port,
                                      final String username,
                                      final String password) {
        StringBuffer url = new StringBuffer();

        url.append("jdbc:"+dbtype+"://");
        url.append(hostname).append(":");
        url.append(port).append("/");
    /*    if ((database != null) && (database != " ")) {
            url.append(database);
        }*/
        url.append("?user="+username);
        if ((password != null) && (password != " ")) {
            url.append("&password="+password);
        }
        log.debug("URL to Database connection");
        System.out.println("URL --------> "+ url.toString());
    return url.toString();
    }

    @Override
    public String getErrorMessageId() {
        return "Can not connect to " + this.dbType +  "using given parameters! \n" +
                "Please verify that Database server is online and the parameters are correct.";
    }

    @Override
    public String getWarningMessageId() {
        return "Error connecting to MySQL Server. ";
    }

    @Override
    public boolean getDefaultAnswer() {
        return false;
    } 
}