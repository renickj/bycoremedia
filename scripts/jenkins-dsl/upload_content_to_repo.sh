#!/bin/bash

# Verbose output and error handling
set -x
set -e

# Set identity
git config user.email "contentuploadgituser@customer.com"
git config user.name "Jenkins Content Upload To Repo Job"

# Make local branch "contentupload" track remote branch (re-associate it)
git branch -u origin/contentupload contentupload

# Remove downloaded core test-data
rm -rf test-data/content/*

# Remove downloaded livecontext test-data (first upload will merge it anyways)
rm -rf modules/extensions/lc/test-data/content/*

# Export current content to test-data/
/opt/coremedia/cm7-cms-tools/bin/cm serverexport -b test-data/content -r -u admin -p admin -pretty

# Tell git to add all content (under version control and new content)
git add --all .

# Commit changes locally
git commit -m "Content uploaded to repository"

# Push them to the repository
git push origin

set +e
set +x
