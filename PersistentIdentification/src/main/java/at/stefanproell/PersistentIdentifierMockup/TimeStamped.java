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

import java.util.Date;

/**
 * Interface for time stamps
 */
public interface TimeStamped {
    public Date getCreatedDate();

    public void setCreatedDate(Date createdDate);

    public Date getLastUpdatedDate();

    public void setLastUpdatedDate(Date lastUpdatedDate);

    public char getWasUpdated();

    public void setWasUpdated(char wasUpdated);

}