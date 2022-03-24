#!/usr/bin/env bash

# get absolute path of file
ABSPATH=$(readlink -f "$0")
# print directory
ABSDIR=$(dirname "$ABSPATH")
# import profile.sh
source  "${ABSDIR}"/profile.sh

REPOSITORY=/home/ec2-user/app/nginx
ARTIFACT_ID=ReFacDiabetesDiary

echo "> build file copy"
echo "> cp $REPOSITORY/zip/*.jar $REPOSITORY/"
cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> new application deploy!"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo ">JAR NAME : $JAR_NAME"

echo ">$JAR_NAME 에 실행권한 추가"
chmod +x "$JAR_NAME"

echo ">$JAR_NAME 실행"
IDLE_PROFILE=$(find_idle_profile)

echo "> $IDLE_PROFILE 에서 실행!"
nohup java -jar \
  -Dspring.config.location=classpath:/application.properties,classpath:/application-"$IDLE_PROFILE".properties,/home/ec2-user/app/application-oauth.properties,/home/ec2-user/app/application-real-db.properties,/home/ec2-user/app/application-email.properties \
  -Dspring.profiles.active="$IDLE_PROFILE" \
  "$JAR_NAME" >$REPOSITORY/nohup.out 2>&1 &
