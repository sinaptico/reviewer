== Welcome to reviewer
Reviewer is a tool to support collaborative writing activities using Google Docs.

You might also be interested in our [https://bitbucket.org/latteresearch/glosserproject/|Glosser project] that provides automated feedback and allows for human feedback and reviewing.

The following papers describe both systems:
* R.A. Calvo. Affect-Aware Reflective Writing Studios. (2015). In R.A. Calvo, S.K. D’Mello, J. Gratch and A. Kappas (Eds). Chapter 33. Handbook of Affective Computing. Oxford University Press.
* Southavilay, V, Yacef, K, Reimann P. Calvo RA “Analysis of Collaborative Writing Processes Using Revision Maps and Probabilistic Topic Models” Learning Analytics and Knowledge – LAK 2013. Leuven, Belgium, 8-12 April, 2013
* R.A. Calvo, A. Aditomo, V. Southavilay and K. Yacef. (2012) “The use of text and process mining techniques to study the impact of feedback on students’ writing processes”. International Conference on the Learning Sciences. Sydney, pp 416-423
* R.A Calvo, S.T O’Rourke, J. Jones, K. Yacef, P. Reimann. (2011) “Collaborative Writing Support Tools on the Cloud”. IEEE Transactions on Learning Technologies. 4 (1) pp 88-97

== Acknowledgements
This project was supported by a Google Research Award, and grants from the Australian research Council and The Office of Learning and Teaching.
Improvements were made by [[Southern-Path|http://southern-path.com/]]. For commercial support contact them directly.

== Installation

Dependencies: ( check server installation )

* Java 6 or 7
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

    $ mysql
    mysql> create database reviewer;
    mysql> use reviewer;
    mysql> CREATE USER 'reviewer'@'localhost' IDENTIFIED BY 'reviewer';
    mysql> GRANT ALL ON reviewer.* TO 'reviewer'@'localhost';
    mysql> exit

Build and run:
    
    Firs time
        $ mvn -DskipTests package

    Post builds:
        $ mvn -DskipTests package -o
    
    Run:
        $ mvn gwt:run -o
        
    Debug:
        $ mvn gwt:debug
    
    

    
    
        

