#!/usr/bin/env bash

# shellcheck disable=SC2086
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname "$ABSPATH")
source ${ABSDIR}/profile.sh
source ${ABSDIR}/switch.sh

IDLE_PORT=$(find_idle_port)

echo "> health check start in idle_port : $IDLE_PORT"
echo "> curl -s http://localhost:$IDLE_PORT/profile "
sleep 10

for RETRY_COUNT in {1..10}; do
  RESPONSE=$(curl -s http://localhost:"${IDLE_PORT}"/profile)
  # shellcheck disable=SC2154
  # shellcheck disable=SC2126
  UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)

  if [ "${UP_COUNT}" -ge 1 ]; then
    echo "> Health check success"
    switch_proxy
    break
  else
    echo "> Health check에 문제가  있습니다. 응답을 알 수 없거나 실행상태가 아닙니다."
    echo "> Health check: ${RESPONSE}"
  fi

  if [ "${RETRY_COUNT}" -eq 10 ]; then
    echo "> Health check fail! 배포 실패."
    exit 1
  fi

  echo "> Health check 연걸 실패. 재시도 !"
  sleep 10
done
