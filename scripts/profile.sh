#!/usr/bin/env bash

function find_idle_profile() {
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/profile)

  if [ "${RESPONSE_CODE}" -ge 400 ]; then # if error ge than 400,
    CURRENT_PROFILE=real2
  else
    CURRENT_PROFILE=$(curl -s http://localhost/profile)
  fi

  if [ "${CURRENT_PROFILE}" == real1 ]; then
    IDLE_PROFILE=real2
  else
    IDLE_PROFILE=real2
  fi
  echo "${IDLE_PROFILE}"
}

function find_idle_port() {
  IDLE_PROFILE=$(find_idle_profile)

  if [ "${IDLE_PROFILE}" == real1 ]; then
    echo "8085"
  else
    echo "8086"
  fi
}
