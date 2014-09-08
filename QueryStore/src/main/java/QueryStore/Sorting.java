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
@Table(name = "sorting")

public class Sorting implements Serializable {
    private Long sortingId;
    private Query query;
    private String sorting_column;
    private String direction;

    protected Sorting() {

    }

    /**
     * @param query
     * @param sorting_column
     * @param direction
     */
    public Sorting(Query query, String sorting_column, String direction) {
        super();
        this.query = query;
        this.sorting_column = sorting_column;
        this.direction = direction;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "query_id")
    protected Query getQuery() {
        return query;

    }

    protected void setQuery(Query query) {
        this.query = query;
    }

    @Column(name = "sorting_column")
    protected String getSorting_column() {
        return sorting_column;
    }

    protected void setSorting_column(String sorting_column) {
        this.sorting_column = sorting_column;
    }

    @Column(name = "direction")
    protected String getDirection() {
        return direction;
    }

    protected void setDirection(String direction) {
        this.direction = direction;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sorting_id", unique = true, nullable = false)
    protected Long getSortingId() {
        return sortingId;
    }

    protected void setSortingId(Long sortingId) {
        this.sortingId = sortingId;
    }

}
