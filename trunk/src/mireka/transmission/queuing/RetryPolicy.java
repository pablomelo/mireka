package mireka.transmission.queuing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mireka.address.Recipient;
import mireka.transmission.LocalMailSystemException;
import mireka.transmission.Mail;
import mireka.transmission.Transmitter;
import mireka.transmission.dsn.DsnMailCreator;
import mireka.transmission.dsn.PermanentFailureReport;
import mireka.transmission.immediate.PostponeException;
import mireka.transmission.immediate.RecipientRejection;
import mireka.transmission.immediate.RecipientsWereRejectedException;
import mireka.transmission.immediate.RemoteMtaErrorResponseException;
import mireka.transmission.immediate.SendException;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryPolicy {
    private final Logger logger = LoggerFactory.getLogger(RetryPolicy.class);
    private List<Period> retryPeriods = Arrays.asList(new Period[] {
            Period.minutes(3), Period.minutes(27), Period.minutes(30),
            Period.hours(2), Period.hours(2), Period.hours(2), Period.hours(2),
            Period.hours(2), Period.hours(2), Period.hours(2), Period.hours(2),
            Period.hours(2), Period.hours(2), Period.hours(3) });
    private DsnMailCreator dsnMailCreator;
    private Transmitter dsnTransmitter;
    private Transmitter retryTransmitter;

    /**
     * this constructor can be used with setter injection
     */
    public RetryPolicy() {
        // nothing to do
    }

    public RetryPolicy(DsnMailCreator dsnMailCreator,
            Transmitter dsnTransmitter, Transmitter retryTransmitter) {
        this.dsnMailCreator = dsnMailCreator;
        this.dsnTransmitter = dsnTransmitter;
        this.retryTransmitter = retryTransmitter;
    }

    /**
     * @throws LocalMailSystemException
     *             if a bounce (DSN) mail cannot be created or passed to a queue
     */
    public void actOnEntireMailFailure(Mail mail, SendException exception)
            throws LocalMailSystemException {
        EntireMailFailureHandler failureHandler =
                new EntireMailFailureHandler(mail, exception);
        failureHandler.onFailure();
    }

    /**
     * @throws LocalMailSystemException
     *             if a bounce (DSN) mail cannot be created or passed to a queue
     */
    public void actOnRecipientsWereRejected(Mail mail,
            RecipientsWereRejectedException exception)
            throws LocalMailSystemException {
        RecipientsRejectedFailureHandler failureHandler =
                new RecipientsRejectedFailureHandler(mail, exception.rejections);
        failureHandler.onFailure();
    }

    public void actOnPostponeRequired(Mail mail, PostponeException e)
            throws LocalMailSystemException {
        mail.postpones++;
        if (mail.postpones <= 3) {
            Instant newScheduleDate =
                    new DateTime().plusSeconds(e.getRecommendedDelay())
                            .toInstant();
            mail.scheduleDate = newScheduleDate.toDate();
            retryTransmitter.transmit(mail);
            logger.debug("Delivery must be postponed to all hosts. "
                    + "Rescheduling the attempt. This is the " + mail.postpones
                    + ". postponing of this delivery attempt.");

        } else {
            logger.debug("Too much postponings of delivery attempt. "
                    + "The next would be the " + mail.postpones
                    + ". Attempt is considered to be a failure.");
            SendException sendException =
                    new SendException(
                            "Too much postponings of delivery attempt, attempt is considered to be a failure.",
                            e, e.getEnhancedStatus(), e.getRemoteMta());
            EntireMailFailureHandler failureHandler =
                    new EntireMailFailureHandler(mail, sendException);
            failureHandler.onFailure();
        }
    }

    private int maxAttempts() {
        return retryPeriods.size();
    }

    /**
     * @category GETSET
     */
    public void setRetryPeriods(List<Period> retryPeriods) {
        this.retryPeriods = retryPeriods;
    }

    /**
     * @category GETSET
     */
    public void setDsnMailCreator(DsnMailCreator dsnMailCreator) {
        this.dsnMailCreator = dsnMailCreator;
    }

    /**
     * @category GETSET
     */
    public void setDsnTransmitter(Transmitter dsnTransmitter) {
        this.dsnTransmitter = dsnTransmitter;
    }

    /**
     * @category GETSET
     */
    public void setRetryTransmitter(Transmitter retryTransmitter) {
        this.retryTransmitter = retryTransmitter;
    }

    private class RecipientsRejectedFailureHandler extends FailureHandler {
        private final List<RecipientRejection> rejections;

        public RecipientsRejectedFailureHandler(Mail mail,
                List<RecipientRejection> rejections) {
            super(mail);
            this.rejections = rejections;
        }

        @Override
        protected List<SendingFailure> createFailures() {
            List<SendingFailure> result = new ArrayList<SendingFailure>();
            for (RecipientRejection rejection : rejections) {
                result.add(new SendingFailure(rejection.recipient,
                        rejection.sendException));
            }
            return result;
        }
    }

    private class EntireMailFailureHandler extends FailureHandler {

        private final SendException sendException;

        public EntireMailFailureHandler(Mail mail, SendException sendException) {
            super(mail);
            this.sendException = sendException;
        }

        @Override
        protected List<SendingFailure> createFailures() {
            List<SendingFailure> result = new ArrayList<SendingFailure>();
            for (Recipient recipient : mail.recipients) {
                result.add(new SendingFailure(recipient, sendException));
            }
            return result;
        }
    }

    private abstract class FailureHandler {
        private final Logger logger = LoggerFactory
                .getLogger(EntireMailFailureHandler.class);
        protected final Mail mail;

        private List<SendingFailure> failures;
        private List<SendingFailure> permanentFailures =
                new ArrayList<SendingFailure>();
        private List<SendingFailure> transientFailures =
                new ArrayList<SendingFailure>();
        private List<PermanentFailureReport> permanentFailureReports =
                new ArrayList<PermanentFailureReport>();

        public FailureHandler(Mail mail) {
            this.mail = mail;
        }

        public final void onFailure() throws LocalMailSystemException {
            mail.deliveryAttempts++;
            mail.postpones = 0;
            failures = createFailures();
            separatePermanentAndTemporaryFailures();
            createPermanentFailureReports();
            bouncePermanentFailures();
            rescheduleTemporaryFailures();
        }

        protected abstract List<SendingFailure> createFailures();

        private void separatePermanentAndTemporaryFailures() {
            for (SendingFailure failure : failures) {
                if (failure.exception.errorStatus().shouldRetry())
                    transientFailures.add(failure);
                else
                    permanentFailures.add(failure);
            }
            if (mail.deliveryAttempts > maxAttempts()
                    && !transientFailures.isEmpty()) {
                logger.debug("Giving up after the " + mail.deliveryAttempts
                        + ". transient failure. Considering it as "
                        + "a permanent failure.");
                permanentFailures.addAll(transientFailures);
                transientFailures.clear();
                return;
            }
        }

        private void createPermanentFailureReports() {
            for (SendingFailure failure : permanentFailures) {
                permanentFailureReports.add(createPermanentFailureReport(
                        failure.recipient, failure.exception));
            }
        }

        private PermanentFailureReport createPermanentFailureReport(
                Recipient recipient, SendException exception) {
            PermanentFailureReport failure = new PermanentFailureReport();
            failure.recipient = recipient;
            failure.status = exception.errorStatus();
            failure.remoteMta = exception.remoteMta();
            if (exception instanceof RemoteMtaErrorResponseException)
                failure.remoteMtaDiagnosticStatus =
                        ((RemoteMtaErrorResponseException) exception)
                                .remoteMtaStatus();
            failure.failureDate = exception.failureDate;
            failure.logId = exception.getLogId();
            return failure;
        }

        private void bouncePermanentFailures() throws LocalMailSystemException {
            if (permanentFailureReports.isEmpty())
                return;
            if (mail.from.isEmpty()) {
                logger.debug("Permanent failure, but reverse-path is null, "
                        + "DSN must not be sent. "
                        + "Original mail itself was a notification.");
                return;
            }
            Mail dsnMail = dsnMailCreator.create(mail, permanentFailureReports);
            dsnTransmitter.transmit(dsnMail);
            logger.debug("Permanent failure, DSN message is created and passed "
                    + "to the DSN transmitter.");
        }

        private void rescheduleTemporaryFailures()
                throws LocalMailSystemException {
            if (transientFailures.isEmpty())
                return;
            Period waitingPeriod = retryPeriods.get(mail.deliveryAttempts - 1);
            Instant newScheduleDate =
                    new DateTime().plus(waitingPeriod).toInstant();
            mail.scheduleDate = newScheduleDate.toDate();
            mail.recipients = calculateTemporarilyRejectedRecipientList();
            retryTransmitter.transmit(mail);
            logger.debug("Transient failure, the mail is scheduled for a "
                    + (mail.deliveryAttempts + 1) + ". attempt "
                    + waitingPeriod + " later on " + newScheduleDate);
        }

        private List<Recipient> calculateTemporarilyRejectedRecipientList() {
            List<Recipient> result = new ArrayList<Recipient>();
            for (SendingFailure failure : transientFailures) {
                result.add(failure.recipient);
            }
            return result;
        }
    }

    /**
     * SendingFailure stores failure information for a specific recipient.
     */
    private static class SendingFailure {
        public final Recipient recipient;
        public final SendException exception;

        public SendingFailure(Recipient recipient, SendException exception) {
            this.recipient = recipient;
            this.exception = exception;
        }
    }
}
