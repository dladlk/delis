FILE=/work/2019.03.25_b2brouter_test/Invoice_Version_1.0.xml
USER_PASS=user:password
curl --user "${USER_PASS}" -X POST "http://localhost:8080/rest/send" -H  "Content-Type: multipart/form-data" -F "file=@${FILE}"