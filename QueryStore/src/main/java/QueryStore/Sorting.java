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
    private Query queryObject;
    private String sortingColumn;
    private String direction;

    public void setSortingSequence(int sortingSequence) {
        this.sortingSequence = sortingSequence;
    }

    private int sortingSequence;
    protected Sorting() {

    }

    /**
     * @param queryObject
     * @param sortingColumn
     * @param direction
     */
    public Sorting(Query queryObject, String sortingColumn, String direction) {
        super();
        this.queryObject = queryObject;
        this.sortingColumn = sortingColumn;
        this.direction = direction;
    }

    @ManyToOne
    protected Query getQueryObject() {
        return queryObject;

    }

    protected void setQueryObject(Query queryObject) {
        this.queryObject = queryObject;
    }

    @Column(name = "sortingColumn")
    protected String getSortingColumn() {
        return sortingColumn;
    }

    protected void setSortingColumn(String sortingColumn) {
        this.sortingColumn = sortingColumn;
    }

    @Column(name = "direction")
    protected String getDirection() {
        return direction;
    }

    protected void setDirection(String direction) {
        this.direction = direction;
    }

    @Column(name = "sorting_sequence")
    protected int getSortingSequence() {
        return this.sortingSequence;
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
