#!/usr/bin/env bash
set -eux

varpath=/var/db/newrelic-infra/custom-integrations
eventfile=$varpath/sfevents.log
errfile=$varpath/sfevents.err
pidfile=$varpath/sfevents.pid

if [ ! -f "${pidfile}" ] || !(ps -u $(whoami) -opid= | grep -w "$(cat $pidfile)" &> /dev/null); then
  nohup java -Dpidfile=$pidfile -cp emp-connector-0.0.1-SNAPSHOT-phat.jar \
  com.salesforce.emp.connector.example.LoginExample \
  'cmccarthy@platformevents.demo' \
  '<PASSWORD>' \
  /event/Cloud_News__e \
  > $eventfile 2> $errfile \
  & echo $! > $pidfile
fi


cat $eventfile >> $varpath/asdfasdf
cp $eventfile "$eventfile.tmp"
truncate $eventfile --size 0

while read l; do
  echo $l
done <"$eventfile.tmp"

  
