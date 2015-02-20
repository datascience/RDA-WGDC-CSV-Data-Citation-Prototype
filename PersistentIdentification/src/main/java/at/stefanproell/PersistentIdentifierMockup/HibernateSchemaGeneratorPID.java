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

package at.stefanproell.PersistentIdentifierMockup;

import org.hibernate.cfg.Configuration;
import org.hibernate.envers.tools.hbm2ddl.EnversSchemaGenerator;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Export SQL schema and export it into a file.
 */
public class HibernateSchemaGeneratorPID {
    public static void main(String[] args) {

        String outputFilePath = "PersistentIdentification/additional_configuration/PID-Hibernate-schema.sql";

        Configuration config = new Configuration();
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.addAnnotatedClass(PersistentIdentifier.class);
        config.addAnnotatedClass(Organization.class);
        SchemaExport export = new EnversSchemaGenerator(config).export().setOutputFile(outputFilePath);
        export.setDelimiter(";");
        export.execute(true, false, false, false);

        // Update Schema
        //updateSchema(config);
    }


}
