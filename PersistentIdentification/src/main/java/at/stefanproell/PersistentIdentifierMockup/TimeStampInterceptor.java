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

package at.stefanproell.PersistentIdentifierMockup;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Hibernate interceptor for created and updated timestamps
 * This class is used in order to implement the time stamps for inserts and updates. It uses Hibernate in order
 * to store the Timestamps
 */
public class TimeStampInterceptor extends EmptyInterceptor {

    private static final Logger logger = Logger.getLogger(String.valueOf(TimeStampInterceptor.class));

    /**
     * Store the timestamp of an update
     *
     * @param entity
     * @param id
     * @param currentState
     * @param previousState
     * @param propertyNames
     * @param types
     * @return
     */
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
                                Object[] previousState, String[] propertyNames, Type[] types) {


        /**
         * Update the lastUpdateDate value and set the wasUpdated flag to 'Y'
         */
        if (entity instanceof TimeStamped && previousState != null) {
            logger.info("UPDATE operation");
            /*
            logger.info("onFlushDirty: Object Details are as below: ");

            for (int i = 0; i < length; i++) {
                logger.info("onFlushDirty[" + i + "]: propertyName : " + propertyNames[i]
                        + " ,type :  " + types[i]
                        + " , previous state : " + previousState[i]
                        + " , current state : " + currentState[i]);
            }
            */

            int indexOfLastUpdate = ArrayUtils.indexOf(propertyNames, "lastUpdatedDate");
            int indexOfCreatedDate = ArrayUtils.indexOf(propertyNames, "createdDate");
            int indexOfWasUpdated = ArrayUtils.indexOf(propertyNames, "wasUpdated");

            Date createdDate = (Date) previousState[indexOfCreatedDate];
            Date lastUpdateDate = (Date) currentState[indexOfLastUpdate];


            /**
             * If createdDate equals lastUpdateDate, this is the first update.
             * Set the updated column to Y
             */
            if (createdDate.equals(lastUpdateDate)) {
                logger.warning("This is the first update of the record.");
                currentState[indexOfWasUpdated] = 'Y';
            }
            // set the new date of the update event
            currentState[indexOfLastUpdate] = new Date();


            return true;
        }
        return false;
    }


    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
                          String[] propertyNames, Type[] types) {

        logger.info("Save operation");
        /*

        logger.info("onSave: Saving object " + entity + " with id " + id);
        final int length = state.length;
        logger.info("onSave: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            logger.info("onSave: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " , state : " + state[i]);
        }
        */

        /**
         * Set the createdDate and the lastUpdateDate to the current Date and indicate that this is a
         * new record by setting the value of wasUpdated to 'N'.
         */
        if (entity instanceof TimeStamped) {

            Date insertDate = new Date();
            int indexOfCreateDateColumn = ArrayUtils.indexOf(propertyNames, "createdDate");
            int indexOfUpdatedDateColumn = ArrayUtils.indexOf(propertyNames, "lastUpdatedDate");
            int indexOfWasUpdated = ArrayUtils.indexOf(propertyNames, "wasUpdated");

            state[indexOfCreateDateColumn] = insertDate;
            state[indexOfUpdatedDateColumn] = insertDate;
            state[indexOfWasUpdated] = 'N';

            return true;
        }
        return false;
    }

    // called on load events
    @Override
    public boolean onLoad(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types) {

        // log loading events
        /*
        logger.info("Load Operation");
        logger.info("onLoad: Attempting to load an object " + entity + " with id "
                + id);
        final int length = state.length;
        logger.info("onLoad: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            logger.info("onLoad: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " ,state : " + state[i]);
        }
        */
        return true;
    }


    //called before commit into database
    @Override
    public void preFlush(Iterator iterator) {
        System.out.println("Before commiting");
    }

    //called after committed into database
    @Override
    public void postFlush(Iterator iterator) {
        System.out.println("After commiting");
    }
}