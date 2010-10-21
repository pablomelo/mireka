package mireka.filter.local.table;

import mireka.address.RemotePartContainingRecipient;

public interface RecipientSpecification {
    public boolean isSatisfiedBy(RemotePartContainingRecipient recipient);
}
