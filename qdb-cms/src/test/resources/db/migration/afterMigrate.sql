INSERT INTO DOCUMENT(submitter, filename, uuid, content) VALUES(5555, 'test.pdf', 'ef9f784f-a6cf-4d1f-b435-1ba73e529f0b', RAWTOHEX('test data'));
INSERT INTO DOCUMENT(submitter, filename, uuid, content) VALUES(5555, 'test1.pdf', '7654a932-a4b1-43b0-8300-b9bdc48ca8f4', RAWTOHEX('more test data'));

INSERT INTO DOCUMENT(submitter, filename, uuid, content, post_id) VALUES(778, 'test1.pdf', 'df3fda8f-85a1-4898-90f2-0f639467536b', RAWTOHEX('more test data'), 565656);