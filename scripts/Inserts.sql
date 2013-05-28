ALTER TABLE DocumentType ENGINE = innodb;
ALTER TABLE DocumentType_Rubrics ENGINE = innodb;
ALTER TABLE FeedbackTemplate ENGINE = innodb;
ALTER TABLE Rubric ENGINE = innodb;
ALTER TABLE Rubric_FeedbackTemplates ENGINE = innodb;
ALTER TABLE Organization_Emails_Domains ENGINE = innodb;

ALTER TABLE Organization  auto_increment=1;

INSERT INTO Organization (name) VALUES ('Demo Sinaptico');
INSERT INTO Organization (name) VALUES ('Smart-Sourcing');

UPDATE Organization SET activated = 1 WHERE id=1;

ALTER TABLE User auto_increment=1;

INSERT INTO User (email,firstname,lastname, password, organizationId,wasmuser) VALUES ('tomcat','Admin','Tomcat',MD5('s1n4pt1c0'),1,'');
INSERT INTO User_roles (email,role_name) VALUES ('tomcat','manager-gui');

INSERT INTO User (email,firstname,lastname, password, organizationId,wasmuser) VALUES ('superAdmin@demo-sinaptico.com','SuperAdmin','For Demo Sinaptico',MD5('reviewer'),1,'');
INSERT INTO User_roles (email,role_name) VALUES ('superAdmin@demo-sinaptico.com','SuperAdmin');

INSERT INTO User (email,firstname,lastname, password, organizationId,wasmuser) VALUES ('admin@demo-sinaptico.com','Admin','For Demo Sinaptico',MD5('reviewer'),1,'');
INSERT INTO User_roles (email,role_name) VALUES ('admin@demo-sinaptico.com','Admin');

INSERT INTO User (email,firstname,lastname, password, organizationId,wasmuser) VALUES ('admin@smart-sourcing.com.ar','Admin','For Smart',MD5('reviewer'),2,'');
INSERT INTO User_roles (email,role_name) VALUES ('admin@smart-sourcing.com.ar','Admin');

INSERT INTO User (email,firstname,lastname, password, organizationId,wasmuser) VALUES ('lecturer@demo-sinaptico.com','Lecturer','For Demo Sinaptico',MD5('reviewer'),1,'');
INSERT INTO User_roles (email,role_name) VALUES ('lecturer@demo-sinaptico.com','Admin');
INSERT INTO User_roles (email,role_name) VALUES ('lecturer@demo-sinaptico.com','Guest');

INSERT INTO User (email,firstname,lastname, password, organizationId,wasmuser) VALUES ('tutor@demo-sinaptico.com','Tutor','For Demo Sinaptico',MD5('reviewer'),1,'');
INSERT INTO User_roles (email,role_name) VALUES ('tutor@demo-sinaptico.com','Admin');
INSERT INTO User_roles (email,role_name) VALUES ('tutor@demo-sinaptico.com','Guest');

INSERT INTO User (email,firstname,lastname, password, organizationId,wasmuser) VALUES ('student1@demo-sinaptico.com','Student 1','For Demo Sinaptico',MD5('reviewer'),1,'');
INSERT INTO User_roles (email,role_name) VALUES ('student1@demo-sinaptico.com','Guest');

INSERT INTO User (email,firstname,lastname, password, organizationId,wasmuser) VALUES ('student2@demo-sinaptico.com','Student 2','For Demo Sinaptico',MD5('reviewer'),1,'');
INSERT INTO User_roles (email,role_name) VALUES ('student2@demo-sinaptico.com','Guest');

INSERT INTO User (email,firstname,lastname, password, organizationId,wasmuser) VALUES ('student@smart-sourcing.com.ar','Student','For Smart',MD5('reviewer'),2,'');
INSERT INTO User_roles (email,role_name) VALUES ('student@smart-sourcing.com.ar','Admin');

INSERT INTO User (email,firstname,lastname, password, organizationId,wasmuser) VALUES ('mariela.dagraca@smart-sourcing.com.ar','Lecturer','For Smart',MD5('reviewer'),2,'');
INSERT INTO User_roles (email,role_name) VALUES ('student@smart-sourcing.com.ar','Admin');

ALTER TABLE ReviewerProperty  auto_increment=1;

INSERT INTO ReviewerProperty (name) VALUE ('reviewer.email.username');
INSERT INTO ReviewerProperty (name) VALUE ('reviewer.email.password');
INSERT INTO ReviewerProperty (name) VALUE ('reviewer.google.username');
INSERT INTO ReviewerProperty (name) VALUE ('reviewer.google.password');
INSERT INTO ReviewerProperty (name) VALUE ('reviewer.google.domain');
INSERT INTO ReviewerProperty (name) VALUE ('reviewer.smtp.host'); 
INSERT INTO ReviewerProperty (name) VALUE ('reviewer.smtp.port');
INSERT INTO ReviewerProperty (name) VALUE ('reviewer.glosser.host');
INSERT INTO ReviewerProperty (name) VALUE ('reviewer.glosser.port');
INSERT INTO ReviewerProperty (name) VALUE ('organization.logo.file');
INSERT INTO ReviewerProperty (name) VALUE ('organization.shibboleht.enabled');
INSERT INTO ReviewerProperty (name) VALUE ('organization.password.new.users');
INSERT INTO ReviewerProperty (name) VALUE ('reviewer.domain');

INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,1,'admin@demo-sinaptico.com');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,2,'3fe44aff77c3a349');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,3,'admin@demo-sinaptico.com');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,4,'3fe44aff77c3a349');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,5,'demo-sinaptico.com');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,6,'smtp.gmail.com');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,7,'465');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,8,'dev-01.sinapti.co');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,9,'80');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,10,'');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,11,'NO');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,12,'NewPassword');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (1,13,'dev-02.sinapti.co');

INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,1,'admin@smart-sourcing.com.ar');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,2,'3f923c4d96751840');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,3,'admin@smart-sourcing.com.ar');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,4,'3f923c4d96751840');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,5,'smart-sourcing.com.ar');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,6,'smtp.gmail.com');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,7,'465');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,8,'dev-01.sinapti.co');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,9,'80');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId,value) VALUES (2,10,'');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,11,'YES');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,12,'PasswordSmart');
INSERT INTO Organization_Properties_ReviewerProperty (organizationId,propertyId, value) VALUES (2,13,'dev-02.sinapti.co');

