Mireka is a mail server with SMTP, Mail Submission and POP3 services. It is also an SMTP proxy. As a proxy, it can help to prevent or diagnose mail problems, like outgoing backscatter spam.

Features:
  * detailed logging
  * basic mail traffic statistics
  * filtering by DNSBL
  * filtering by SPF
  * accepting mail only for local domains and recipients
  * wildcards (regular expressions) can be used to specify local recipients
  * basic tarpit, to prevent e-mail address harvesting
  * loop detection
  * configurable maximum message size
  * easy implementation and installation of custom filters written in Java
  * separate Message Transfer Agent and Message Submission Agent ports: 25 and 587 by default
  * proxy functionality for incoming mails (both MSA and MTA)
  * the proxy can select from more than one backend server, based on the recipient
  * standalone (non-proxy) Message Submission Agent implementation with file system based mail queues
  * submission port authentication by IP address or by username-password pair through SMTP authentication
  * POP3 service for retrieving mail, file system based mail store
  * aliases, forward lists and (very) simple mail lists
  * Sender Rewriting Scheme (SRS) for SPF compatible forwarding
  * secure communication using STARTTLS on all services
  * "delayed" DSN reports on temporary failures
  * embeddable
  * runs equally well on any OS, where Java is available: Windows, Linux etc.

How it works in proxy mode:

Mireka can proxy both incoming and outgoing SMTP connections.
It accepts an SMTP connection, logs communication between client and server, runs filters in various stages of the mail transaction, and sends the mail to a back-end SMTP server. It relays steps of the mail transaction immediately, without queuing the mail. In this way the back-end server can also reject recipients and message content before accepting irrevocable responsibility for delivery. Proxy mode works for both outgoing and incoming mail.

Any number of ports can be configured, and the proxy and standalone modes can be mixed. For example incoming mails can be proxied on one port, while outgoing mails received on another port are transmitted directly by Mireka alone.

Documentation:
  * [Mireka Documentation](http://mireka.googlecode.com/svn/doc/index.html)
  * [Javadoc](http://mireka.googlecode.com/svn/doc/javadoc/index.html)

Mireka is written in Java. A large part of the functionality is provided by components produced by other projects:
  * [SubEthaSMTP](http://code.google.com/p/subethasmtp/) for receiving mails
  * [Logback](http://logback.qos.ch/) for logging
  * [Mime4J](http://james.apache.org/mime4j/) for parsing message content and constructing DSN messages
  * [dnsjava](http://www.dnsjava.org/) for querying DNSBLs and determining the SMTP servers of recipient domains
  * [jSPF](http://james.apache.org/jspf/) for checking the sender

Current usage:
Mireka was used as a proxy in front of two [Apache James Servers](http://james.apache.org/server/index.html) receiving 50.000 mail transactions daily. Now it is used both as full mail server and a send only mail server with similar traffic.

<p align='right'>
<a href='http://petition.stopsoftwarepatents.eu/731004171782/'>
<img src='http://petition.stopsoftwarepatents.eu/banner/731004171782/ssp-181-30.gif' alt='stopsoftwarepatents.eu petition banner' width='181' height='30' />
</a>
</p>