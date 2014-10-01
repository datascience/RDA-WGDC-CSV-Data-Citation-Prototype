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

package Database;

import java.util.Date;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */

/**
 * Store the metadata of a record in one object
 */
public class RecordMetadata {
    private int ID_SYSTEM_SEQUENCE;
    private Date INSERT_DATE;
    private Date LAST_UPDATE;
    private String RECORD_STATUS;


    public int getID_SYSTEM_SEQUENCE() {
        return ID_SYSTEM_SEQUENCE;
    }

    public void setID_SYSTEM_SEQUENCE(int ID_SYSTEM_SEQUENCE) {
        this.ID_SYSTEM_SEQUENCE = ID_SYSTEM_SEQUENCE;
    }

    public Date getINSERT_DATE() {
        return INSERT_DATE;
    }

    public void setINSERT_DATE(Date INSERT_DATE) {
        this.INSERT_DATE = INSERT_DATE;
    }

    public Date getLAST_UPDATE() {
        return LAST_UPDATE;
    }

    public void setLAST_UPDATE(Date LAST_UPDATE) {
        this.LAST_UPDATE = LAST_UPDATE;
    }

    public String getRECORD_STATUS() {
        return RECORD_STATUS;
    }

    public void setRECORD_STATUS(String RECORD_STATUS) {
        this.RECORD_STATUS = RECORD_STATUS;
    }

    public RecordMetadata(int ID_SYSTEM_SEQUENCE, Date INSERT_DATE, Date LAST_UPDATE, String RECORD_STATUS) {
        this.ID_SYSTEM_SEQUENCE = ID_SYSTEM_SEQUENCE;
        this.INSERT_DATE = INSERT_DATE;
        this.LAST_UPDATE = LAST_UPDATE;
        this.RECORD_STATUS = RECORD_STATUS;
    }
}
