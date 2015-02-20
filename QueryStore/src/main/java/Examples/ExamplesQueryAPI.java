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

package Examples;

import QueryStore.Query;
import QueryStore.QueryStoreAPI;
import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlpha;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlphaNumeric;


import java.util.logging.Logger;

/**
 * Examples for the usage of the query API
 */
public class ExamplesQueryAPI {
    private Logger logger;

    public static void main(String args[]) {
        System.out.println("Query Store Test");
        ExamplesQueryAPI app = new ExamplesQueryAPI();

        app.createNewQuery();

        System.out.println("Done");
        System.exit(0);
    }

    public void createNewQuery() {

        // Init the API
        PersistentIdentifierAPI pidAPI = new PersistentIdentifierAPI();
        // create a dummy organization and provide a prefix
        Organization researchOrganization = pidAPI.createNewOrganitation("Research Int.", 1234);
        // create test identifiers
        PersistentIdentifierAlphaNumeric pid = pidAPI.getAlphaNumericPID(researchOrganization, "www.repository," +
                "org/queries/q1");
        PersistentIdentifierAlpha dataSourcePid = pidAPI.getAlphaPID(researchOrganization, "www.repository," +
                "bases/DB");


        // get the prefix and identifier as String in the form 1234/identifier
        String queryPID = pidAPI.getIdentifierStringWithPrefix(pid);
        String dataSourcePID = pidAPI.getIdentifierStringWithPrefix(dataSourcePid);

        // Queries
        // Initialize Query Store
        QueryStoreAPI queryAPI = new QueryStoreAPI();
        // Create a query
        Query query = queryAPI.createNewQuery("username@repository.org", queryPID);

        // some filters
        queryAPI.addFilter(query, "Filter1", "Value1");
        queryAPI.addFilter(query, "Filter2", "Value2");
        queryAPI.addFilter(query, "Filter3", "Value3");
        queryAPI.addFilter(query, "Filter4", "Value4");
        queryAPI.addFilter(query, "Filter5", "Value5");

        // some sortings
        queryAPI.addSorting(query, "ColumnA", "DESC");
        queryAPI.addSorting(query, "ColumnB", "ASC");
        queryAPI.addSorting(query, "ColumnC", "ASC");

        // dummy hash calculation
        queryAPI.calculateResultSetHashFull(query);

        // get query pid
        String queryPid = queryAPI.getQueryPID(query);

        // get query object from PID string
        Query retrievedQuery = queryAPI.getQueryByPID(queryPid);

        // retrieve query hash
        String queryHash = queryAPI.getQueryHash(retrievedQuery);
        System.out.println("Query hash: " + queryHash);

        queryAPI.finalizeQuery(retrievedQuery);


        Organization evilOrganization = pidAPI.createNewOrganitation("Evil Corp", 2345);

        // create identifiers
        PersistentIdentifierAlphaNumeric newPid = pidAPI.getAlphaNumericPID(researchOrganization,
                "www.evlil.org/query");
        String newQueryPID = pidAPI.getIdentifierStringWithPrefix(newPid);
        // second query
        Query newQuery = queryAPI.createNewQuery("username@repository.org", newQueryPID);


        // some filters
        queryAPI.addFilter(newQuery, "Filter1", "Value1");
        queryAPI.addFilter(newQuery, "Filter2", "Value2");
        queryAPI.addFilter(newQuery, "Filter3", "Value3");
        queryAPI.addFilter(newQuery, "Filter4", "Value4");
        queryAPI.addFilter(newQuery, "Filter5", "Value5");

        // some sortings
        queryAPI.addSorting(newQuery, "ColumnA", "DESC");
        queryAPI.addSorting(newQuery, "ColumnB", "ASC");
        queryAPI.addSorting(newQuery, "ColumnC", "ASC");

        queryAPI.finalizeQuery(newQuery);



/*
        // create a dummy organization and provide a prefix
        Organization evilOrganization = pidAPI.createNewOrganitation("Evil Corp",2345);
        System.out.println("Organization created");

        // create identifiers
        PersistentIdentifierAlphaNumeric newpid = pidAPI.getAlphaNumericPID(researchOrganization, "www.dertest.org");
        PersistentIdentifierAlphaNumeric newpid2 = pidAPI.getAlphaNumericPID(researchOrganization, "www.dertest.org");
        PersistentIdentifierAlphaNumeric newpid3 = pidAPI.getAlphaNumericPID(researchOrganization, "www.dertest.org");
        System.out.println("PID created");

*/


    }


}
