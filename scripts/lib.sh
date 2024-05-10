#!/usr/bin/env bash

## This file is intended to be sourced by other scripts

function err() {
  echo >&2 "$@"
}

function curl_gh() {
  if [[ -n "$CLASSIC_TOKEN" ]]; then
    curl \
      --silent \
      --header "Authorization: token $CLASSIC_TOKEN" \
      "$@"
  else
    err "WARNING: No CLASSIC_TOKEN found. Skipping API call"
  fi
}
