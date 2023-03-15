##
## Install FE dependencies ##
##
FROM node:19.4.0 AS npm
WORKDIR /opt
COPY package.json yarn.lock ./
RUN yarn install

##
## BUILD BE ##
##
FROM clojure:temurin-19-tools-deps-1.11.1.1208 AS builder

WORKDIR /opt

COPY . .

# Pre-download Frontend deps so they will be cached even if build fails and must be re-run
RUN clojure -A:cljs -Spath
# Pre-download Backend deps so they will be cached even if build fails and must be re-run
RUN clojure -Spath

COPY --from=npm /opt/node_modules node_modules
#RUN env TIMBRE_LEVEL=:warn clojure -M:shadow-cljs release main
RUN env TIMBRE_LEVEL=:info clojure -M:shadow-cljs release main

# Note: With AOC, the build below takes 10 min when run directly on my PC 🥶
RUN clojure -Sdeps '{:mvn/local-repo "./.m2/repository"}' -T:build uber

##
## RUNTIME IMAGE ##
##
FROM amazoncorretto:19.0.1-alpine AS runtime
COPY --from=builder /opt/target/app-0.0.1-standalone.jar /app.jar

EXPOSE 8008

# NOTE: The VM only has 256MB mem (and OS needs some too)
# NOTE: Could use `java -jar` as long as we do AOT
ENTRYPOINT ["java", "-Xms140m", "-Xmx350m", "-Dclojure.main.report=stderr", "-cp", "app.jar", "clojure.main", "-m", "com.example.server.main"]