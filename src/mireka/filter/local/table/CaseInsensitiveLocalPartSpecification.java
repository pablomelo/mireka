package mireka.filter.local.table;

import java.util.Locale;

import mireka.address.LocalPart;

/**
 * This implementation does case-insensitive comparisons using the US locale.
 * The standard SMTP only allows US-ASCII characters. Note that case sensitive
 * mailbox names are also allowed by the standard. An extension allows non-ASCII
 * characters and case insensitivity, but doesn't specify rules for that. E.g.
 * the final delivery mailbox address must also specify a Locale.
 * 
 * @see <a href="http://tools.ietf.org/html/rfc5336">RFC 5336 SMTP Extension for
 *      Internationalized Email Addresses</a>
 */
public class CaseInsensitiveLocalPartSpecification implements
        LocalPartSpecification {
    private final String unescapedText;
    private final String unescapedTextInLowerCase;

    public CaseInsensitiveLocalPartSpecification(String displayableName) {
        this.unescapedText = displayableName;
        this.unescapedTextInLowerCase = displayableName.toLowerCase(Locale.US);
    }

    @Override
    public boolean matches(LocalPart localPart) {
        String recipientLocalPartInLowerCase =
                localPart.displayableName().toLowerCase(Locale.US);
        return recipientLocalPartInLowerCase.equals(unescapedTextInLowerCase);
    }

    @Override
    public String toString() {
        return unescapedText;
    }

}
