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

POST_ID=$(echo "${DOCUMENT_RETRIEVAL_RESPONSE}" | jq '.documents[0].postId')

echo '====================================================='
echo "Retrieve the post for post id: ${POST_ID}"
echo '====================================================='

POST_RESPONSE=$(http https://jsonplaceholder.typicode.com/post/$POST_ID)

# Not sure why this is coming out as: {} because I can see on openfeign the response request.
echo "Add post response from placeholder: ${POST_RESPONSE}"

echo '====================================================='
echo 'Create comment'
echo '====================================================='

COMMENT_PAYLOAD=$(cat comment.json | sed "s/ID/$POST_ID/g")
http -v POST http://localhost:9080/cms/comment body="$COMMENT_PAYLOAD"
