
package at.stefanproell.Examples;

import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlphaNumeric;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierSubcomponent;

/**
 * Created by stefan on 20.11.14.
 */
public class ResolverTest {

    public static void main(String[] args) {
        System.out.println("API Mini Test");
        ResolverTest apiTest = new ResolverTest();

        apiTest.one();

        System.exit(0);


    }

    private void one() {
        PersistentIdentifierAPI api = new PersistentIdentifierAPI();
        Organization org = api.getOrganizationObjectByPrefix(6789);
        PersistentIdentifierAlphaNumeric newpid = api.getAlphaNumericPID(org, "root1");
        PersistentIdentifierAlphaNumeric newpid2 = api.getAlphaNumericPID(org, "root2");
        PersistentIdentifierSubcomponent sub = api.getSubComponentAlphaNummeric(newpid, "sub");
        PersistentIdentifierSubcomponent sub2 = api.getSubComponentAlphaNummeric(newpid, "sub2");
        PersistentIdentifierSubcomponent subsub = api.getSubComponentAlphaNummeric(sub, "subsub");
        PersistentIdentifierSubcomponent subsubsub = api.getSubComponentAlphaNummeric(subsub, "subsubsub");
        PersistentIdentifierSubcomponent subsubsubsub = api.getSubComponentAlphaNummeric(subsubsub, "subsubsub");

        String fqn = subsubsubsub.getFQNidentifier();
        api.resolveIdentifierToURIFromFQNIdentifier(fqn);
        api.resolveIdentifierToURIFromFQNIdentifier("6789/Y00xbz1Px7/VflE/wVY8");


    }
}
