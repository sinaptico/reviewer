Mysql 
1.	mysql -uroot -p
2.	Delete the databases
	⁃	drop database reviewer;
	⁃	drop database feedback;
	⁃	drop database glosser;
	⁃	drop database glosser_elec5619;
 	⁃       drop database glosser_uws300053;
3.	Create databases
	⁃	create database reviewer;
	⁃	create database feedback;
	⁃	create database glosser;
	⁃	create database glosser_elec5619;
        ⁃       create database glosser_uws300053;	
Run the scripts in the following order:
1.	mysql -uroot -p reviewer <reviewer.sql
2.	mysql -uroot -p reviewer <Rubric.sql
3.	mysql -uroot -p reviewer <DocumentType.sql
4.	mysql -uroot -p reviewer <DocumentType_Rubrics.sql 
5.	mysql -uroot -p reviewer <FeedbackTemplate.sql 
6.	mysql -uroot -p reviewer <Rubric_FeedbackTemplates.sql
7.	mysql -uroot -p reviewer <Inserts.sql
8.	mysql -uroot -p reviewer <newOrganizations.sql

Configurate the organizations
1.	Go to http://dev-02.sinapti.co/ReviewerAdmin.html
2.	Go Edit Organizations and load all the organization (search text field must be empty)
3.	For each organization do
	⁃	Save the organization to create their emails
	⁃	Go Edit the properties and save the properties related to password in order to encrypt them
	⁃	Check the properties to see if there are OK. If there OK the organization will be activated.
4.	Logout

Configurate the server
1.	cd /home/ubutu/reviewer/organizations
2.	delete all the files and subfolders

Clean Google
1.	Go to Google docs with the admin users for the organizations
2.	Delete all the folders and documents 

New Organization
Follow the steps in newOrganization.txt file
