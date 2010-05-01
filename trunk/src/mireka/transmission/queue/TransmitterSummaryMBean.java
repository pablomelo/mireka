package mireka.transmission.queue;

public interface TransmitterSummaryMBean {
    int getMailTransactions();

    int getSuccessfulMailTransactions();

    int getFailures();

    int getPartialFailures();

    int getPermanentFailures();

    int getTransientFailures();

    double getFailuresPercentage();

    double getPermanentFailuresPercentage();

    double getTransientFailuresPercentage();

    Throwable getLastFailure();

    int getErrors();

    Throwable getLastError();
}
