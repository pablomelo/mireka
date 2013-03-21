package mireka.destination;

import mireka.smtp.RejectExceptionExt;
import mireka.transmission.Mail;

/**
 * A MailDestination is a {@link ResponsibleDestination} which is only interested 
 * in the complete mail at the end of the mail transaction, not in the steps of 
 * the mail transaction. 
 */
public interface MailDestination extends ResponsibleDestination {
    /**
     * Processes the mail. This function is called after the SMTP DATA command
     * has been received. It is only called if there is at least one accepted
     * recipient.
     */
    void data(Mail mail) throws RejectExceptionExt;
}