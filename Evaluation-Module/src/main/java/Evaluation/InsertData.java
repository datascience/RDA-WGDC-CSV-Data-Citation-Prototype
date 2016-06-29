/*
 * Copyright [2016] [Stefan Pröll]
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

package Evaluation;

import Database.DatabaseOperations.HibernateUtilData;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by stefan on 29.06.16.
 */
public class InsertData {
    private static final Logger logger = Logger.getLogger(InsertData.class.getName());

    public InsertData() {

    }

    public void insertRecord() {
        Connection connection = null;
        Statement statement;
        try {

            Session session = HibernateUtilData.getSessionFactory().openSession();
            SessionImpl sessionImpl = (SessionImpl) session;
            connection = sessionImpl.connection();

            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO `CitationDB`.`timestampsTable`(`identifier`,`timestamp`) VALUES (1,NOW())");
            connection.commit();
            connection.close();

            logger.info("Going to sleep...");
            TimeUnit.MILLISECONDS.sleep(1050);
            logger.info("Wakeing up...");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }
    }


}
