/*
 * Copyright [2014] [Stefan Pröll]
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

/*
 * Copyright [2014] [Stefan Pröll]
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
        SchemaExport export = new EnversSchemaGenerator(config).export().setOutputFile
                ("QueryStore/additional_configuration/Query-Hibernate-schema.sql");
        export.setDelimiter(";");
        export.execute(true, false, false, false);
    }

}
