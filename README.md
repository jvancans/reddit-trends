# reddit-trends

## Running the app (multiple options)
- Using bash and gradle wrapper: ```./gradlew bootRun```
- Using gradle: ```gradle bootRun``` (requires gradle)
- Using IDE: Run the main class ```RedditTrendsApplication``` as Java/Spring Boot application
- Building the JAR artifact with ```gradle build``` and then running standalone JAR with ```java -jar build/libs/reddit-trends-1.0-SNAPSHOT.jar```

## Running tests
- Using gradle: ```gradle test```
- Report can be found here: ```build/reports/tests/test/index.html```

## Endpoints (supports application/json media types)
- localhost:8080/api/trends GET
- localhost:8080/api/trends/activity GET
- localhost:8080/api/trends/subreddits GET
- localhost:8080/api/trends/users GET
- see ```TrendsResource``` and ```RedditTrendsApplicationTest``` for examples

## Additional notes
For IDE support, Lombok plugin is required