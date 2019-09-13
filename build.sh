#!/bin/bash

mvn clean test

if [[ $TRAVIS_PULL_REQUEST == "false" || $TRAVIS_PULL_REQUEST_SLUG == $TRAVIS_REPO_SLUG ]]; then
    mvn org.jacoco:jacoco-maven-plugin:prepare-agent install sonar:sonar -Dsonar.projectKey=sunxuia_website-user
    echo "end sonarcloud scan"
fi

if [[ $TRAVIS_PULL_REQUEST == "false" && $TRAVIS_BRANCH == "master" ]]; then
    echo "deploy project productions"

    docker login -u $docker_username -p $docker_password
    mvn deploy -Dmaven.test.skip=true -Dgithub.global.oauth2Token=$github_token
fi
