#Makefile

.DEFAULT_GOAL:=start

clean:
	./gradlew clean

build:
	./gradlew clean build

install: 
	./gradlew clean installDist

start:
	./gradlew bootRun --args='--spring.profiles.active=development'

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=production'

start-dist:
	./build/install/app/bin/app

check-updates: 
	./gradlew dependencyUpdates

test:
	./gradlew test

report:
	./gradlew jacocoTestReport


generate-migrations:
	gradle diffChangeLog

lint:
	./gradlew checkstyleMain checkstyleTest

list:
	@grep '^[^#[:space:]].*:' makefile

 .PHONY: build

