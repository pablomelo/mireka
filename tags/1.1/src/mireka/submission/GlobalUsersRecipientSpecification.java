package mireka.submission;

import java.util.HashSet;
import java.util.Set;

import mireka.filter.builtin.local.RecipientSpecification;
import mireka.mailaddress.LocalPart;
import mireka.mailaddress.RemotePartContainingRecipient;

public class GlobalUsersRecipientSpecification implements
        RecipientSpecification {
    private final Set<LocalPart> usernamesAsLocalParts =
            new HashSet<LocalPart>();

    public void setUsers(GlobalUsers users) {
        if (!usernamesAsLocalParts.isEmpty())
            throw new IllegalStateException();
        
        for (GlobalUser user : users) {
            usernamesAsLocalParts.add(new LocalPart(user.getUsername()
                    .toString()));
        }
    }

    @Override
    public boolean isSatisfiedBy(RemotePartContainingRecipient recipient) {
        return usernamesAsLocalParts.contains(recipient.getAddress()
                .getLocalPart());
    }
}