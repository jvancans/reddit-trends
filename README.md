# reddit-trends

## Running the app (multiple options)
- Using bash and gradle wrapper: ```./gradlew bootRun```
- Using gradle: ```gradle bootRun``` (requires gradle)
- Using IDE: Run the main class ```RedditTrendsApplication``` as Java/Spring Boot application

## Running tests
- Using gradle: ```gradle test```
- Report can be found here: ```build/reports/tests/test/index.html```

## Endpoints (supports application/json media types)
- localhost:8080/api/trends GET
- localhost:8080/api/trends/activity GET
- localhost:8080/api/trends/subreddits GET
- localhost:8080/api/trends/users GET
- see ```TrendsResource``` and ```RedditTrendsApplicationTest``` for examples