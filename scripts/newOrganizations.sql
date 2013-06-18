use reviewer

INSERT INTO Organization (name) VALUES ('University of Sydney');
INSERT INTO Organization (name) VALUES ('University of New South Wales');
INSERT INTO Organization (name) VALUES ('University of Western Sydney');

INSERT INTO Organization_Emails_Domains (organization_Id,emailDomains) VALUES (3,'iwrite.sydney.edu.au');
INSERT INTO Organization_Emails_Domains (organization_Id,emailDomains) VALUES (3,'uni.sydney.edu.au');
INSERT INTO Organization_Emails_Domains (organization_Id,emailDomains) VALUES (3,'sydney.edu.au');
INSERT INTO Organization_Emails_Domains (organization_Id,emailDomains) VALUES (4,'uws.edu.au');
INSERT INTO Organization_Emails_Domains (organization_Id,emailDomains) VALUES (4,'student.uws.edu.au');

INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,1,'marieladg@iwrite.sydney.edu.au');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,2,'marie2148');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,3,'marieladg@iwrite.sydney.edu.au');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,4,'marie2148');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,5,'iwrite.sydney.edu.au');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,6,'smtp.gmail.com');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,7,'465');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,8,'dev-02.sinapti.co');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,9,'80');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,10,'');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,11,'YES');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,12,'USYD');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (3,13,'usyd.dev-02.sinapti.co');

INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,1,'marieladg@iwrite.sydney.edu.au');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,2,'marie2148');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,3,'marieladg@iwrite.sydney.edu.au');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,4,'marie2148');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,5,'iwrite.sydney.edu.au');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,6,'smtp.gmail.com');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,7,'465');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,8,'dev-02.sinapti.co');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,9,'80');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,10,'');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,11,'YES');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,12,'UWS');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (4,13,'uws.dev-02.sinapti.co');

INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,1,'marieladg@iwrite.sydney.edu.au');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,2,'marie2148');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,3,'marieladg@iwrite.sydney.edu.au');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,4,'marie2148');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,5,'iwrite.sydney.edu.au');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,6,'smtp.gmail.com');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,7,'465');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,8,'dev-02.sinapti.co');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,9,'80');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,10,'');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,11,'YES');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,12,'UNSW');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (5,13,'unsw.dev-02.sinapti.co');

INSERT INTO User (email,organizationId,username,lastname,firstname) VALUES ('eie.latte@sydney.edu.au',3,'eie.latte','eie','latte');
INSERT INTO User_roles VALUES('eie.latte@sydney.edu.au','Admin');

