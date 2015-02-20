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

package Examples;

import QueryStore.HibernateUtilQueryStore;
import QueryStore.Query;
import QueryStore.QueryStoreAPI;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stefan on 11.02.15.
 */
public class BaseTableTest {

    private Logger logger;

    public static void main(String args[]) {
        System.out.println("Query Store Test");

        BaseTableTest test = new BaseTableTest();
        test.run();


        System.out.println("Done");
        System.exit(0);
    }

    private void run() {
        QueryStoreAPI queryAPI = new QueryStoreAPI();

        Session session = HibernateUtilQueryStore.getSessionFactory().openSession();
        session.beginTransaction();
        // get all queries with the given base table


        Criteria criteria = session.createCriteria(Query.class, "q");
        criteria.add(Restrictions.eq("q.baseTableId", new Long(1)));
        List queryList = criteria.list();


        session.getTransaction().commit();

        session.close();


    }

}
