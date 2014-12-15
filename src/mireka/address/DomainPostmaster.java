package mireka.address;

/**
 * represents the special "Postmaster@"domain recipient. This is always treated
 * case-insensitively.
 * 
 * @see <a href="http://tools.ietf.org/html/rfc5321#section-4.1.1.3">RFC 5321
 *      4.1.1.3</a>
 */
public class DomainPostmaster implements RemotePartContainingRecipient {
    private final Address address;

    public DomainPostmaster(Address address) {
        this.address = address;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public boolean isDomainPostmaster() {
        return true;
    }

    @Override
    public boolean isGlobalPostmaster() {
        return false;
    }

    @Override
    public boolean isPostmaster() {
        return true;
    }

    @Override
    public LocalPart localPart() {
        return address.getLocalPart();
    }

    @Override
    public String sourceRouteStripped() {
        return address.toString();
    }

    @Override
    public String toString() {
        return address.toString();
    }

}
