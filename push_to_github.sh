#!/usr/bin/env bash

set -e

REPO="https://github.com/dcoxii/Complet-Android-APP-Installer.git"

git init
git add .
git commit -m "Initial commit - Complete Android APK Installer project"

git branch -M main
git remote add origin "$REPO"

echo " "
echo "🚀 Ready to push!"
echo "When prompted for PASSWORD, paste your GitHub PAT."
echo " "

git push -u origin main