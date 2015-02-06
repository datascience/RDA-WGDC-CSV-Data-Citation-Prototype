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

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Table(name = "filter")
public class Filter implements Serializable {
    private Long filterId;


    private Query queryObject;
    private String filterName;
    private String filterValue;
    private int filterSequence;


    public Filter() {

    }

    public Filter(Query q) {
        this.queryObject = q;
    }

    public Filter(Query q, String filterName, String filterValue) {
        this.queryObject = q;
        this.filterName = filterName;
        this.filterValue = filterValue;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filter_id", unique = true, nullable = false)
    protected Long getFilterId() {
        return filterId;
    }

    protected void setFilterId(Long filterId) {
        this.filterId = filterId;
    }


    @ManyToOne
    protected Query getQueryObject() {
        return queryObject;
    }

    protected void setQueryObject(Query queryObject) {
        this.queryObject = queryObject;
    }

    @Column(name = "filterName")
    protected String getFilterName() {
        return filterName;
    }

    protected void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    @Column(name = "filterValue")
    protected String getFilterValue() {
        return filterValue;
    }

    protected void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    @Column(name = "filter_sequence")
    protected int getFilterSequence() {
        return filterSequence;
    }

    protected void setFilterSequence(int filterSequence) {
        this.filterSequence = filterSequence;
    }
}
