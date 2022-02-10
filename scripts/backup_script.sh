#!/usr/bin/env bash
# clean up before starting
set -xe

echo "Starting backup $(date +%Y-%m-%d\ %H:%M:%S)"

# going into the directory to build the backup file
mkdir -p /backup/jira-backups/backup
cd /backup/jira-backups/backup
git-lfs prune

# remove old backup files to keep it clean, leave the last 1
rm -f $(find . -type f -name "*.tar.gz" -printf "%f\n" | sort -r | tail -n+1)

# dump the database
echo "${PG_HOST}:${PG_PORT}:${PG_DB}:${PG_USER}:${PG_PASSWORD}" > ~/.pgpass
chmod 0600 /root/.pgpass

pg_dump -h ${PG_HOST} -p ${PG_PORT} --username=${PG_USER} -f db.sql ${PG_DB}

# copy all relevant data from jira (and remove unnessecary files)
cp -Lr /var/atlassian/application-data/jira jira_home
chmod -R 777 jira_home
rm -rf jira_home/export/*
rm -rf jira_home/data/git-plugin/*

# tar all files to a dict which then can be provisioned
FILENAME=backup-$(date +%Y-%m-%d-%H-%M-%S).tar.gz
tar -czf $FILENAME jira_home db.sql

# clean up all helper files
rm -rf jira_home db.sql

# push the backup into the repo
git add $FILENAME
# add all tar-gz files, also the removal once
cd ..
git add --all *.tar.gz
git commit -m "$(date +%Y-%m-%d\ %H:%M:%S) Backup"
git push origin develop

echo "Finishing backup $(date +%Y-%m-%d\ %H:%M:%S)"