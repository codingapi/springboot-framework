### url test

# login test
curl -X POST http://localhost:8090/user/login -d '{"username":"admin","password":"admin"}' -H "Content-Type: application/json"

# index test
curl -X GET http://localhost:8090/api/index  -H "Content-Type: application/json" -H "Authorization:eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhdXRob3JpdGllc1wiOltcIlJPTEVfQURNSU5cIl0sXCJleHBpcmVcIjpmYWxzZSxcImV4cGlyZVRpbWVcIjoxNjU4MTkxMjExODY5LFwicmVtaW5kVGltZVwiOjE2NTgxOTA5MTE4NjksXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0ifQ.2S_6NyzaBUBVAQrKeUY-YSMeCIkry-a4Lt5tBThp97Inz-hSGMMe--DT_oeoR4wgBQ6OcYOaW4ZseKZ3H1XIMw"

# error test
curl -X GET http://localhost:8090/api/error  -H "Content-Type: application/json" -H "Authorization:eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhdXRob3JpdGllc1wiOltcIlJPTEVfQURNSU5cIl0sXCJleHBpcmVcIjpmYWxzZSxcImV4cGlyZVRpbWVcIjoxNjU4MTkxMjExODY5LFwicmVtaW5kVGltZVwiOjE2NTgxOTA5MTE4NjksXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0ifQ.2S_6NyzaBUBVAQrKeUY-YSMeCIkry-a4Lt5tBThp97Inz-hSGMMe--DT_oeoR4wgBQ6OcYOaW4ZseKZ3H1XIMw"

# logout test
curl -X GET http://localhost:8090/user/logout