package mireka.filter.local;

import mireka.filter.AbstractDataRecipientFilter;
import mireka.filter.DataRecipientFilterAdapter;
import mireka.filter.Filter;
import mireka.filter.FilterBase;
import mireka.filter.FilterReply;
import mireka.filter.FilterType;
import mireka.filter.MailTransaction;
import mireka.filter.RecipientContext;
import mireka.filter.local.table.RecipientDestinationMapper;

import org.subethamail.smtp.RejectException;

/**
 * The LookupDestination filter assigns a destination to recipients in the
 * {@link FilterBase#verifyRecipient(RecipientContext)} phase.
 */
public class LookupDestination implements FilterType {
    private RecipientDestinationMapper recipientDestinationMapper;

    @Override
    public Filter createInstance(MailTransaction mailTransaction) {
        FilterImpl filterInstance = new FilterImpl(mailTransaction);
        return new DataRecipientFilterAdapter(filterInstance, mailTransaction);
    }

    /**
     * @category GETSET
     */
    public RecipientDestinationMapper getRecipientDestinationMapper() {
        return recipientDestinationMapper;
    }

    /**
     * @category GETSET
     */
    public void setRecipientDestinationMapper(
            RecipientDestinationMapper recipientDestinationMapper) {
        this.recipientDestinationMapper = recipientDestinationMapper;
    }

    private class FilterImpl extends AbstractDataRecipientFilter {

        public FilterImpl(MailTransaction mailTransaction) {
            super(mailTransaction);
        }

        @Override
        public FilterReply verifyRecipient(RecipientContext recipientContext)
                throws RejectException {
            recipientContext.setDestination(recipientDestinationMapper
                    .lookup(recipientContext.recipient));
            return FilterReply.NEUTRAL;
        }
    }
}
