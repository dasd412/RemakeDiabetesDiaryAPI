#!/usr/bin/env bash

ABSPATH=$(readlink -f "$0")
ABSDIR=$(dirname "$ABSPATH")
soucre "${ABSDIR}"/profile.sh

IDLE_PORT=$(find_idle_port)

echo "> $IDLE_PORT에서 running 중인 애플리케이션 pid check!"
IDLE_PID=$(lsof -ti tcp:"${IDLE_PORT}")

if [ -z "${IDLE_PID}" ]; then
  echo "> 현재 구동중인 어플리케이션이 없으므로 종료 안함."
else
  echo "> kill -15 $IDLE_PID"
  kill -15 "${IDLE_PID}"
  sleep 5
fi
