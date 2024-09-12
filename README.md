# Pekko-Test

Use this CURL to reproduce ECONNRESET:
```
curl -X POST -H "Content-Type: application/json" -d @request.json http://localhost:8090/pekko-test
```