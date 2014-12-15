package mireka.transmission;

import mireka.destination.MailDestination;
import mireka.smtp.RejectExceptionExt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.RejectException;

/**
 * The TransmitterDestination marks a recipient for which the mail must be
 * transmitted asynchronously to a remote MTA as specified by the remote part of
 * the address. Usually the remote part is a domain name, and the MTA must be
 * find by looking up the DNS MX record of that domain.
 */
public class TransmitterDestination implements MailDestination {
    private final Logger logger = LoggerFactory
            .getLogger(TransmitterDestination.class);
    private Transmitter transmitter;

    @Override
    public void data(Mail mail) throws RejectExceptionExt {
        try {
            transmitter.transmit(mail);
        } catch (LocalMailSystemException e) {
            logger.warn("Cannot accept mail because of a "
                    + "transmission failure", e);
            throw new RejectException(e.errorStatus().getSmtpReplyCode(), e
                    .errorStatus().getMessagePrefixedWithEnhancedStatusCode());
        }
    }

    /**
     * @category GETSET
     */
    public void setTransmitter(Transmitter transmitter) {
        this.transmitter = transmitter;
    }

    /**
     * @category GETSET
     */
    public Transmitter getTransmitter() {
        return transmitter;
    }

    @Override
    public String toString() {
        return "TransmitterDestination";
    }
}
