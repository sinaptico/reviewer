== Welcome to reviewer

== Installation

Dependencies:

* Java 1.6 
* MySql
* Tomcat
* Maven 2

To install Maven 2:

    $ sudo apt-get install maven2
    $ cd /home/{user}/.mv2
    $ mkdir repository
    $ cd repository    
      copy all the third parties libs here.
    
To install the code:

    $ cd /home/{user}
    $ git clone git@github.com:sinaptico/reviewer.git
    $ cd /home/{user}
    $ touch reviewer.properties
    $ vi reviewer.properties
        reviewer.email.username={google doc email account}
        reviewer.email.password={google doc email account password}
        reviewer.google.username={google doc email account}
        reviewer.google.password={google doc email account password}
        reviewer.google.domain={google doc email account domain}
        reviewer.privatekey.path=
        reviewer.publickey.path=
        reviewer.documents.home=/home/{user}/documents
        reviewer.empty.document=/home/{user}/documents/empty.pdf
        reviewer.uploads.home=/home/{user}/documents/uploads
        reviewer.empty.file=/home/{user}/documents/empty.pdf
        reviewer.admin.users=admin1
        system.http.proxySet=false
        system.http.proxyHost=
        system.http.proxyPort=
        system.https.proxySet=false
        system.https.proxyHost=
        system.https.proxyPort=
        aqg.loadExcelPath=Questions.xls
        aqg.insertToExcelPath=Qscore.xls

    $ cd /home/{user}/reviewer/src/main/resources
    $ vi hibernate.cfg.xml
        <!-- Connection -->
        <property name="connection.driver_class">com.mysql.jdbc.Driver</property>
        <property name="connection.url">jdbc:mysql://localhost/reviewer</property>
        <property name="connection.username">reviewer</property>
        <property name="connection.password">reviewer</property>
        <property name="connection.provider_class">org.hibernate.connection.C3P0ConnectionProvider</property>

To install the database:


        

