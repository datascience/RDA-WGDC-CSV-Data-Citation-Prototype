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

package at.stefanproell.PersistentIdentifierMockup;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * API for generating, updating and resolving PIDs
 */
public class PersistentIdentifierAPI {

    private Logger logger;
    private Session session;

    /**
     * Constructor
     */
    public PersistentIdentifierAPI() {
        this.logger = Logger.getLogger(PersistentIdentifierAPI.class.getName());


    }

    /**
     * Insert a new organization
     *
     * @param organizationName
     * @param prefix
     * @return
     */
    public Organization createNewOrganitation(String organizationName, int prefix) {
        Organization org = null;
        if (this.checkOrganitationalPrefix(prefix)) {
            this.logger.info("new organization created. Prefix: " + prefix);

            this.session = HibernateUtil.getSessionFactory().openSession();
            this.session.beginTransaction();
            org = new Organization(organizationName, prefix);
            this.session.save(org);
            this.session.getTransaction().commit();
            this.session.flush();
            this.session.close();

        } else {
            this.logger.severe("PREFIX incorrect");
        }

        return org;


    }

    /**
     * Write new identifier into database
     *
     * @param pid
     * @param org
     * @param URIString
     * @return
     */
    private PersistentIdentifier persistNewIdentifier(PersistentIdentifier pid, Organization org, String URIString) {

        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();

        pid.setOrganization(org);
        pid.generateIdentifierString();
        pid.setURI(URIString);
        pid.setFQNidentifier(pid.getOrganization().getOrganization_prefix() + "/" + pid.getIdentifier());
        this.session.save(pid);
        this.session.getTransaction().commit();
        this.session.flush();
        this.session.close();


        return pid;

    }


    /**
     * Get an alphanumeric identifier
     *
     * @param org
     * @param URIString
     * @return
     */
    public PersistentIdentifierAlphaNumeric getAlphaNumericPID(Organization org, String URIString) {

        PersistentIdentifierAlphaNumeric pid = new PersistentIdentifierAlphaNumeric();
        pid = (PersistentIdentifierAlphaNumeric) this.persistNewIdentifier(pid, org, URIString);



        return pid;
    }

    /**
     * Get an numeric identifier
     *
     * @param org
     * @param URIString
     * @return
     */
    public PersistentIdentifierNumeric getNumericPID(Organization org, String URIString) {
        PersistentIdentifierNumeric pid = new PersistentIdentifierNumeric();
        pid = (PersistentIdentifierNumeric) this.persistNewIdentifier(pid, org, URIString);


        return pid;
    }

    /**
     * Get an alpha identifier
     *
     * @param org
     * @param URIString
     * @return
     */
    public PersistentIdentifierAlpha getAlphaPID(Organization org, String URIString) {
        PersistentIdentifierAlpha pid = new PersistentIdentifierAlpha();
        pid = (PersistentIdentifierAlpha) this.persistNewIdentifier(pid, org, URIString);


        return pid;
    }

    /**
     * Create a new Subcomponent of the specified identifier
     * @param parentPID
     * @param URIString
     * @return
     */
    public PersistentIdentifierSubcomponent getSubComponentAlphaNummeric(PersistentIdentifier parentPID, String URIString) {

        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();
        PersistentIdentifierSubcomponent subPID = new PersistentIdentifierSubcomponent();
        subPID.setParentIdentifier(parentPID);
        subPID.generateIdentifierString();
        subPID.setURI(URIString);
        subPID.setOrganization(parentPID.getOrganization());
        subPID.setFQNidentifier(this.getFullyQuallifiedIdentifierNameForSubcomponent(subPID));
        this.session.save(subPID);
        this.session.getTransaction().commit();
        this.session.flush();
        this.session.close();


        return subPID;
    }

    /**
     * Create a new Subcomponent of the specified identifier
     * @param parentPID
     * @param URIString
     * @return
     */
    public PersistentIdentifierSubcomponent getSubComponentWithManualIdentifierAlphaNummeric(PersistentIdentifier parentPID, String URIString, String manualIdentifier) {

        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();
        PersistentIdentifierSubcomponent subPID = new PersistentIdentifierSubcomponent();
        subPID.setParentIdentifier(parentPID);
        subPID.setIdentifier(manualIdentifier);
        subPID.setURI(URIString);
        subPID.setOrganization(parentPID.getOrganization());
        subPID.setFQNidentifier(this.getFullyQuallifiedIdentifierNameForSubcomponent(subPID));
        this.session.save(subPID);
        this.session.getTransaction().commit();
        this.session.flush();
        this.session.close();


        return subPID;
    }

    /**
     * retrieve a list of parents of an identifier.
     *
     * @param subcomponent
     * @return
     */
    public LinkedList<PersistentIdentifier> getAllParentsFromSubcompoment(PersistentIdentifier subcomponent) {

        LinkedList<PersistentIdentifier> parentList = new LinkedList<PersistentIdentifier>();
        boolean hasParent = true;

        PersistentIdentifier currentIdentifier = subcomponent;
        while (hasParent) {

            /**
             * If the current identifeir is a subcomponent itself, retrieve the parent and proceed
             */
            if (currentIdentifier instanceof PersistentIdentifierSubcomponent) {
                PersistentIdentifierSubcomponent currentSubcomponent = (PersistentIdentifierSubcomponent) currentIdentifier;

                PersistentIdentifier pid = (PersistentIdentifier) currentSubcomponent.getParentIdentifier();
                parentList.addFirst(pid);
                this.logger.info("added parent " + pid.getIdentifier());
                currentIdentifier = pid;

            } else {
                hasParent = false;

            }

        }
        this.printIdentifiersFromList(parentList);
        return parentList;


    }

    /**
     * Get the fully qualified identifier including prefix and all intermediate parent identifiers
     *
     * @param subcomponent
     * @return
     */
    public String getFullyQuallifiedIdentifierNameForSubcomponent(PersistentIdentifierSubcomponent subcomponent) {
        String fqn = "";
        int parentPrefix = this.getRootParentOfIdentifier(subcomponent).getOrganization().getOrganization_prefix();
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(parentPrefix);

        fqn = sb.toString();

        LinkedList<PersistentIdentifier> parentList = this.getAllParentsFromSubcompoment(subcomponent);
        if (parentList.size() > 0) {
            for (PersistentIdentifier pid : parentList) {
                fqn += "/" + pid.getIdentifier();
            }

        }
        fqn += "/" + subcomponent.getIdentifier();

        return fqn;

    }


    /**
     * Return the root node of a given composite identifier
     * @param pid
     * @return
     */
    public PersistentIdentifier getRootParentOfIdentifier(PersistentIdentifier pid) {
        PersistentIdentifier root = getAllParentsFromSubcompoment(pid).getFirst();
        this.logger.info("The root node of " + pid.getIdentifier() + " is " + root.getIdentifier());
        return root;
    }


    /**
     * Resolve a PID and return its URL
     *
     * @param
     * @return
     */
    public String resolveIdentifierToURI(int prefixInput, String identifierInput) {
        String stringURI = null;
        //Validate if identifier exists and retrieve URL
        if (this.validatePID(prefixInput, identifierInput)) {
            this.session = HibernateUtil.getSessionFactory().openSession();
            this.session.beginTransaction();

            Criteria criteria = this.session.createCriteria(PersistentIdentifier.class, "pid");
            criteria.createAlias("pid.organization", "o");
            criteria.add(Restrictions.eq("pid.identifier", identifierInput));
            criteria.add(Restrictions.eq("o.organization_prefix", prefixInput));
            criteria.setProjection(
                    Projections.distinct(
                            Projections.projectionList()

                                    .add(Projections.property("pid.URI"), "URI")
                    )
            );

            // retrieve the result <Integer><String>
            List resultList = criteria.list();


            this.session.getTransaction().commit();
            this.session.close();

            this.logger.info("List size: " + resultList.size());

            // There should only be one result.
            for (Iterator it = resultList.iterator(); it.hasNext(); ) {
                //    Object[] resultArray = (Object[]) it.next();
                // get the Prefix and the identifier

                stringURI = new String((String) it.next());

            }

        } else {
            this.logger.severe("identifier NOT found");
        }

        return stringURI;

    }

    /**
     * Resolve a PID and return its URL by resolving its FQN
     *
     * @param
     * @return
     */
    public String resolveIdentifierToURIFromFQNIdentifier(String identifierInput) {
        String stringURI = null;
        //Validate if identifier exists and retrieve URL

        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();

        Criteria criteria = this.session.createCriteria(PersistentIdentifier.class, "pid");

        criteria.add(Restrictions.eq("pid.FQNidentifier", identifierInput));

        criteria.setProjection(
                Projections.distinct(
                        Projections.projectionList()

                                .add(Projections.property("pid.URI"), "URI")
                )
        );

        // retrieve the result <Integer><String>
        List resultList = criteria.list();


        this.session.getTransaction().commit();
        this.session.close();

        this.logger.info("List size: " + resultList.size());

        // There should only be one result.
        for (Iterator it = resultList.iterator(); it.hasNext(); ) {
            //    Object[] resultArray = (Object[]) it.next();
            // get the Prefix and the identifier

            stringURI = new String((String) it.next());

        }


        this.logger.info(identifierInput + " resolves to " + stringURI);
        return stringURI;

    }


    /**
     * Retrieve a PID object from the DB
     *
     * @param prefixInput
     * @param identifierInput
     * @return
     */
    public PersistentIdentifier getPIDObjectFromPIDString(int prefixInput, String identifierInput) {
        PersistentIdentifier pid = null;

        //Validate if identifier exists and retrieve URL
        if (this.validatePID(prefixInput, identifierInput)) {
            this.session = HibernateUtil.getSessionFactory().openSession();
            this.session.beginTransaction();

            Criteria criteria = this.session.createCriteria(PersistentIdentifier.class, "pid");
            criteria.createAlias("pid.organization", "o");
            criteria.add(Restrictions.eq("pid.identifier", identifierInput));
            criteria.add(Restrictions.eq("o.organization_prefix", prefixInput));


            pid = (PersistentIdentifier) criteria.uniqueResult();


            this.session.getTransaction().commit();
            this.session.close();


        } else {
            this.logger.severe("identifier NOT found");
        }

        return pid;

    }


    /**
     * List all identifiers of a given organization and return list of these identifiers
     *
     * @param org
     * @return
     */
    public List<PersistentIdentifier> listAllPIDsOfOrganization(Organization org) {

        List<PersistentIdentifier> listOfPIDs = new ArrayList<PersistentIdentifier>();
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();

        int org_id = org.getOrganization_id();
        Query query = session.createQuery("from PersistentIdentifier where organization_id= :org_id");
        query.setParameter("org_id", org_id);
        List identifierList = query.list();
        for (Object obj : identifierList) {
            PersistentIdentifier pid = (PersistentIdentifier) obj;
            listOfPIDs.add(pid);
            this.logger.info("PID_ID " + pid.getPersitentIdentifier_id() + " Identifier: " + pid.getIdentifier() + " " +
                    "URI: " + pid.getURI());
        }
        this.session.getTransaction().commit();
        this.session.close();

        this.logger.info("Retrieved " + listOfPIDs.size() + " PIDs for " + org.getOrganization_name());
        return listOfPIDs;

    }

    /**
     * List all organizations
     *
     * @return
     */
    public Map<Integer, String> listAllOrganizations() {


        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();
        Criteria criteria = session.createCriteria(Organization.class);

        ProjectionList properties = Projections.projectionList();
        properties.add(Projections.property("organization_prefix"));
        properties.add(Projections.property("organization_name"));
        criteria.setProjection(properties);
        criteria.addOrder(Order.asc("organization_prefix"));
        List<Object[]> rows = criteria.list();


        this.session.getTransaction().commit();
        this.session.close();

        Map<Integer, String> organitationsMap = new HashMap();


        for (Object[] row : rows) {


            int prefix = Integer.parseInt(row[0].toString());
            String orgName = (String) row[1];
            organitationsMap.put(prefix, orgName);
        }


        this.logger.info("Found " + organitationsMap.size() + " organizations ");

        return organitationsMap;

    }


    /**
     * Get latest added PID in the database
     *
     * @param
     */
    public PersistentIdentifier getLatestAddedPID() {
        PersistentIdentifier pid = null;
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();


        DetachedCriteria maxId = DetachedCriteria.forClass(PersistentIdentifier.class)
                .setProjection(Projections.max("persitentIdentifier_id"));

        List identifierList = session.createCriteria(PersistentIdentifier.class)
                .add(Property.forName("persitentIdentifier_id").eq(maxId))
                .list();


        for (Object obj : identifierList) {
            pid = (PersistentIdentifier) obj;
            System.out.println("PID_ID " + pid.getPersitentIdentifier_id() + " Identifier: " + pid.getIdentifier() +
                    " URI: " + pid.getURI());


        }
        this.session.getTransaction().commit();
        this.session.close();
        this.logger.info("Latest PID was " + pid.getIdentifier());

        return pid;
    }

    /**
     * Get latest added PID by organization
     *
     * @param
     */
    public PersistentIdentifier getLatestAddedPIDbyOrganization(Organization org) {
        PersistentIdentifier pid = null;
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();

        // Get the ID of the PID which has the highest ID per organization
        Long count = (Long) session.createCriteria(PersistentIdentifier.class)
                .setProjection(Projections.max("persitentIdentifier_id"))
                .add(Restrictions.eq("organization", org)).uniqueResult();

        // Get the PID object
        pid = (PersistentIdentifier) session.createCriteria(PersistentIdentifier.class)
                .add(Property.forName("persitentIdentifier_id").eq(count)).uniqueResult();


        this.session.getTransaction().commit();
        this.session.close();
        this.logger.info("Latest PID of " + org.getOrganization_name() + " was " + pid.getIdentifier());

        return pid;
    }


    /**
     * Update a PID's URL when the identifier is provided
     *
     * @param stringPID
     * @param newURI
     */

    public void updateURI(String stringPID, String newURI) {
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();

        Query query = session.createQuery("from PersistentIdentifier  where identifier= :identifier");
        query.setParameter("identifier", stringPID);
        PersistentIdentifier pid = (PersistentIdentifier) query.list().get(0);
        pid.setURI(newURI);
        session.update(pid);
        this.session.getTransaction().commit();
        this.session.flush();
        this.session.close();
        this.logger.info("Update PID ");

    }


    /**
     * Check if the prefix is within the range of 1000 and 9999
     *
     * @param prefix
     */
    private boolean checkOrganitationalPrefix(int prefix) {
        if (prefix >= 1000 || prefix <= 9999) {

            return true;

        } else {
            this.logger.severe("Prefix is out of range");
            return false;
        }

    }

    /**
     * Print record details
     *
     * @param pid
     */
    public void printRecord(PersistentIdentifier pid) {
        System.out.println("PID details" + " System ID " +
                pid.getPersitentIdentifier_id() + " PID" + pid.getIdentifier()
                + " URI" + pid.getURI() + " Created " + pid.getCreatedDate()
                + " Updated " + pid.getLastUpdatedDate());

    }

    /**
     * Print record details
     *
     * @param
     */
    public void printRecord(int prefixInput, String identifierInput) {

        PersistentIdentifier pid = this.getPIDObjectFromPIDString(prefixInput, identifierInput);
        this.printRecord(pid);
    }

    /**
     * Query database for the combination of prefix and identifier. If it exists, return true
     *
     * @param prefixInput
     * @param identifierInput
     * @return
     */
    public boolean validatePID(int prefixInput, String identifierInput) {
        boolean isValid = false;
        String identifierStringDB = null;
        Integer prefixIntegerDB = null;


        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();

        Criteria criteria = this.session.createCriteria(PersistentIdentifier.class, "pid");
        criteria.createAlias("pid.organization", "o");
        criteria.add(Restrictions.eq("pid.identifier", identifierInput));
        criteria.add(Restrictions.eq("o.organization_prefix", prefixInput));
        criteria.setProjection(
                Projections.distinct(
                        Projections.projectionList()
                                .add(Projections.property("o.organization_prefix"), "prefix")
                                .add(Projections.property("pid.identifier"), "identifier")
                )
        );

        // retrieve the result <Integer><String>
        List resultList = criteria.list();


        this.session.getTransaction().commit();
        this.session.close();

        this.logger.info("List size: " + resultList.size());

        // There should only be one result.
        for (Iterator it = resultList.iterator(); it.hasNext(); ) {
            Object[] resultArray = (Object[]) it.next();
            // get the Prefix and the identifier
            prefixIntegerDB = new Integer((Integer) resultArray[0]);
            identifierStringDB = new String(resultArray[1].toString());

        }

        // Test if DB returned values
        if (prefixIntegerDB != null && identifierStringDB != null && prefixInput == prefixIntegerDB &&
                identifierInput.equals(identifierStringDB)) {
            this.logger.info("Identifier validated.");
            isValid = true;
        } else {
            isValid = false;
            this.logger.info("Identifier NOT validated.");

        }
        return isValid;
    }

    /**
     * Append prefix and identifier
     *
     * @param prefixInput
     * @param identifierInput
     * @return
     */
    public String getIdentifierString(int prefixInput, String identifierInput) {
        return prefixInput + "/" + identifierInput;
    }

    /**
     * Conctenate the prefix and the identifier
     *
     * @param pid
     * @return
     */
    public String getIdentifierStringWithPrefix(PersistentIdentifier pid) {
        int prefix = pid.getOrganization().getOrganization_prefix();
        String identifier = pid.getIdentifier();
        return prefix + "/" + identifier;
    }

    /**
     * Get the organization object
     *
     * @param prefix
     * @return
     */
    public Organization getOrganizationObjectByPrefix(int prefix) {
        Organization org = null;
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();
        Criteria criteria = this.session.createCriteria(Organization.class, "org");
        criteria.add(Restrictions.eq("org.organization_prefix", prefix));
        org = (Organization) criteria.uniqueResult();
        this.session.getTransaction().commit();
        this.session.close();

        if (org != null) {
            this.logger.info("Organization prefix " + prefix + " found");
            return org;
        } else {
            this.logger.severe("Organization prefix " + prefix + " NOT found");
            return null;
        }


    }

    /**
     * Check if there is a organization with the provided prefix. Return false if not.
     *
     * @param prefix
     * @return
     */
    public boolean checkOrganizationPrefix(int prefix) {
        Organization org = null;
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();
        Criteria criteria = this.session.createCriteria(Organization.class, "org");
        criteria.add(Restrictions.eq("org.organization_prefix", prefix));
        org = (Organization) criteria.uniqueResult();
        this.session.getTransaction().commit();
        this.session.close();

        if (org != null) {
            this.logger.info("Organization prefix " + prefix + " found: " + org.getOrganization_name());

            return true;
        } else {
            this.logger.severe("Organization prefix " + prefix + " NOT found");
            return false;
        }


    }

    /**
     * Prints the list of identifeirs
     *
     * @param identifierList
     */
    public void printIdentifiersFromList(LinkedList<PersistentIdentifier> identifierList) {
        PersistentIdentifier pid = null;
        for (int i = 0; i < identifierList.size(); i++) {
            pid = (PersistentIdentifier) identifierList.get(i);


            this.logger.info("Identifier (" + i + ") : " + pid.getIdentifier());
        }
    }

    /**
     * Extract the prefix from the URL string
     *
     * @param FQNString
     * @return
     */
    public int getOrganizationPrefixFromURL(String FQNString) {


        String regexPrefix = "^(\\d{4})";    // Any 4 digit number


        Pattern pattern = Pattern.compile(regexPrefix);
        Matcher matcher = pattern.matcher(FQNString);
        this.printRegexMatches(matcher);
        matcher.find();
        int prefix = Integer.parseInt(matcher.group());
        return prefix;


    }


    private void printRegexMatches(Matcher m) {
        System.out.println("Testing String");
        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                System.out.println("matched text: " + m.group(i));
                //    System.out.println("matched start: " + m.start(i));
                //    System.out.println("matched end: " + m.end(i));
            }
        }
    }


}
