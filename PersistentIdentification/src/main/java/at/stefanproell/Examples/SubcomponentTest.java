package at.stefanproell.Examples;

import at.stefanproell.PersistentIdentifierMockup.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by stefan on 20.11.14.
 */
public class SubcomponentTest {

    public static void main(String[] args) {
        System.out.println("API Mini Test");
        SubcomponentTest apiTest = new SubcomponentTest();

        apiTest.one();

        System.exit(0);


    }

    private void one() {
        PersistentIdentifierAPI api = new PersistentIdentifierAPI();
        Organization org = api.getOrganizationObjectByPrefix(56789);
        PersistentIdentifierAlphaNumeric newpid = api.getAlphaNumericPID(org, "root1");
        PersistentIdentifierAlphaNumeric newpid2 = api.getAlphaNumericPID(org, "root2");
        PersistentIdentifierSubcomponent sub = api.getSubComponentAlphaNummeric(newpid, "sub");
        PersistentIdentifierSubcomponent sub2 = api.getSubComponentAlphaNummeric(newpid, "sub2");
        PersistentIdentifierSubcomponent subsub = api.getSubComponentAlphaNummeric(sub, "subsub");
        PersistentIdentifierSubcomponent subsubsub = api.getSubComponentAlphaNummeric(subsub, "subsubsub");
        PersistentIdentifierSubcomponent subsubsubsub = api.getSubComponentAlphaNummeric(subsubsub, "subsubsub");

        api.getAllParentsFromSubcompoment(subsubsubsub);
        api.getRootParentOfIdentifier(subsubsubsub);

        PersistentIdentifierAlphaNumeric manual1 = api.getAlphaNumericPID(org, "root1");
        PersistentIdentifierSubcomponent mansub = api.getSubComponentWithManualIdentifierAlphaNummeric(manual1, "sub1", "set1");
        PersistentIdentifierSubcomponent mansub2 = api.getSubComponentWithManualIdentifierAlphaNummeric(manual1, "sub2", "set2");
        PersistentIdentifierSubcomponent mansubsub = api.getSubComponentWithManualIdentifierAlphaNummeric(mansub, "subset", "subset1");
        PersistentIdentifierSubcomponent mansubsub3 = api.getSubComponentWithManualIdentifierAlphaNummeric(mansub2, "subset", "subset1");
        api.getAllParentsFromSubcompoment(mansubsub);
        api.getRootParentOfIdentifier(mansubsub);





    }
}
