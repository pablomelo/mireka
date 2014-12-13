package mireka.filter.local;

import mireka.filter.FilterReply;
import mireka.filter.RecipientContext;
import mireka.filter.StatelessFilterType;

import org.subethamail.smtp.RejectException;

public class AcceptAllRecipient extends StatelessFilterType {

    @Override
    public FilterReply verifyRecipient(RecipientContext recipientContext)
            throws RejectException {
        return FilterReply.ACCEPT;
    }
}
