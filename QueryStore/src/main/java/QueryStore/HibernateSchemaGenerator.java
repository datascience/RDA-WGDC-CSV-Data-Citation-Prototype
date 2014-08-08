package QueryStore;

import org.hibernate.cfg.Configuration;
import org.hibernate.envers.tools.hbm2ddl.EnversSchemaGenerator;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateSchemaGenerator {
    public static void main(String[] args) {
        Configuration config = new Configuration();

        //don't forget to get the right dialect for Oracle, MySQL, etc
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.addAnnotatedClass(Query.class);
        config.addAnnotatedClass(Filter.class);
        config.addAnnotatedClass(Sorting.class);
        SchemaExport export = new EnversSchemaGenerator(config).export().setOutputFile("QueryStore/additional_configuration/Query-Hibernate-schema.sql");
        export.setDelimiter(";");
        export.execute(true, false, false, false);
    }

}
