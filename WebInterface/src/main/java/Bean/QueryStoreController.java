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

package Bean;

import QueryStore.Filter;
import QueryStore.HibernateUtil;
import QueryStore.Query;
import QueryStore.Sorting;

import at.stefanproell.PersistentIdentifierMockup.PIGenerator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hibernate.Session;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by stefan on 21.06.14.
 */

@ManagedBean(name = "queryStoreController")
@SessionScoped
public class QueryStoreController implements Serializable {
    private Session session = null;
    private Logger logger;

    public Query getQuery() {
        if (this.query == null) {
            this.initializeQueryStore();
        }
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    Query query;


    public Map<String, String> getFilterMap() {


        if (filterMap != null) {
            this.logger.info("Filter map size: " + filterMap.size());
        } else {
            this.logger.warning("Filter map is NULL");
        }


        return filterMap;
    }

    public void setFilterMap(Map<String, String> filterMap) {

        this.logger.info("Setting filtermap inside the bean!!! " +
                "------------------------aaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        this.filterMap = filterMap;
    }

    private Map<String, String> filterMap;


    public QueryStoreController() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.session = HibernateUtil.getSessionFactory().openSession();


    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }


    public void initializeQueryStore() {

        if (this.query == null) {
            this.setQuery(new Query());


        }

        PIGenerator pIGenerator = new PIGenerator();
        String pid = pIGenerator.getRandomAlpaString(5);


        session.beginTransaction();
        this.query.setExecution_timestamp(new Date());
        this.query.setPID(pid);
        this.query.setDatasourcePID(pIGenerator.getRandomAlpaString(5));
        this.query.setQuery_text("SELECT * FROM table");
        this.query.setQueryHash(pIGenerator.getRandomAlpaString(5));
        this.query.setUserName("stefan");
        session.save(query);
        session.getTransaction().commit();


    }

    public void finalizeDataSet() {
        this.logger.info("finalize ");
    }

    public void storeCurrentSelection() {
        this.logger.info("Store selection");
        String filterMapJSON = this.getJSONFromWebService("?lastFilters=1");
        Map<String, String> filterMap = this.convertJSON2Map(filterMapJSON);
        this.printMap(filterMap);


        // Iterate over Filters
        // TODO: externalize in own method
        session.beginTransaction();
        for (Map.Entry<String, String> entry : filterMap.entrySet()) {
            String filterName = entry.getKey();
            String filterValue = entry.getValue();
            Filter filter = new Filter(this.query, filterName, filterValue);
            this.logger.info("new Filter persisted");
            session.save(filter);


        }
        session.save(query);
        session.getTransaction().commit();

        String sortingsMapJSON = this.getJSONFromWebService("?lastSortings=1");
        Map<String, String> sortingsMap = this.convertJSON2Map(filterMapJSON);
        this.printMap(sortingsMap);


        // Iterate over Sorting
        // TODO: externalize in own method
        session.beginTransaction();
        for (Map.Entry<String, String> entry : sortingsMap.entrySet()) {
            String sortingColumn = entry.getKey();
            String direction = entry.getValue();
            Sorting sorting = new Sorting(this.query, sortingColumn, direction);
            this.logger.info("new sorting persisted");
            session.save(sorting);


        }
        session.save(query);
        session.getTransaction().commit();


    }


    private void printMap(Map<String, String> map) {
        if (map == null) {
            this.logger.info("Map was NULL");
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                this.logger.info("Map entry: " + key + " value : " + value);

            }

        }


    }

    /*
        Converts the DatatableModel.JSON string into a Map
     */
    private Map<String, String> convertJSON2Map(String json) {
        Gson gson = new Gson();
        Type typeOfHashMap = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> newMap = gson.fromJson(json, typeOfHashMap); // This type must match TypeToken

        return newMap;
    }

    private String getJSONFromWebService(String parameterRequest) {
        String line;
        StringBuilder builder = new StringBuilder(2048);
        // call Web Service and retrieve json
        String str = "http://localhost:8080/cite/csvdata" + parameterRequest;

        URLConnection urlc = null;
        BufferedReader bfr = null;
        try {
            URL url = null;
            url = new URL(str);

            urlc = url.openConnection();
            bfr = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

            while ((line = bfr.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String jsonString = builder.toString();

        return jsonString;

    }

}