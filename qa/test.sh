#!/bin/sh

echo '====================================================='
echo 'Test document upload'
echo '====================================================='
DOCUMENT_UPLOAD_RESPONSE=$(http -f POST http://localhost:9081/document/upload submitter='1023' upload@'valid.pdf')

UUID=$(echo "${DOCUMENT_UPLOAD_RESPONSE}" | jq '.uuid')

echo '====================================================='
echo 'Test document retrieval'
echo '====================================================='
DOCUMENT_RETRIEVAL_RESPONSE=$(http http://localhost:9081/document/submitter/1023)

DOCUMENT_ID=$(echo "${DOCUMENT_RETRIEVAL_RESPONSE}" | jq '.documents[0].id')

echo '====================================================='
echo 'Create entry post for document'
echo '====================================================='

http POST http://localhost:9080/cms/post/$DOCUMENT_ID < add-post.json

echo '====================================================='
echo 'Get document again for the submitter and confirm the post id is there'
echo '====================================================='

DOCUMENT_RETRIEVAL_RESPONSE=$(http http://localhost:9081/document/submitter/1023)

POST_ID=$(echo "${DOCUMENT_RETRIEVAL_RESPONSE}" | jq '.documents[0].post_id')

echo "Post ID: ${POST_ID}"