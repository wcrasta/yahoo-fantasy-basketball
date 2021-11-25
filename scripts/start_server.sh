#!/bin/bash
# https://docs.aws.amazon.com/codedeploy/latest/userguide/troubleshooting-deployments.html#troubleshooting-long-running-processes
java -jar -Dspring.profiles.active=prod /home/ubuntu/yahoo-fantasy-basketball/target/yahoo-fantasy-basketball-*.jar > /dev/null 2> /dev/null < /dev/null &