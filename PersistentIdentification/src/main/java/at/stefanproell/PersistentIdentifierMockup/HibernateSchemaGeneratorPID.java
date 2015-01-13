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
