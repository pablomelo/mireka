package mireka.address;

/**
 * This class represents a generic recipient, which is neither the special
 * global nor the special domain specific postmaster address.
 */
public class GenericRecipient implements RemotePartContainingRecipient {
    private final String forwardPath;
    private Address address;

    public GenericRecipient(String recipient) {
        this.forwardPath = recipient;
        parseAddress();
    }

    private void parseAddress() {
        String mailbox = sourceRouteStripped();
        address = new Address(mailbox);
    }

    public boolean isPostmaster() {
        return false;
    }

    public boolean isGlobalPostmaster() {
        return false;
    }

    public boolean isDomainPostmaster() {
        return false;
    }

    public String sourceRouteStripped() {
        String mailBox;
        int iColon = forwardPath.indexOf(':');
        if (iColon == -1)
            mailBox = forwardPath;
        else
            mailBox = forwardPath.substring(iColon + 1, forwardPath.length());
        return mailBox;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GenericRecipient other = (GenericRecipient) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return forwardPath;
    }
}
