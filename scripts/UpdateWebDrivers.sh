#!/bin/bash

set -e

resource_dir=src/test/resources/webdrivers

rm -rf $resource_dir/*

chromedrivers=(chromedriver_mac64.zip chromedriver_linux64.zip chromedriver_linux32.zip chromedriver_win32.zip)
chromedriver_latest_version=$(curl -s https://chromedriver.storage.googleapis.com/LATEST_RELEASE)

geckodrivers=(macos.tar.gz win32.zip win64.zip linux32.tar.gz linux64.tar.gz)
geckodriver_latest_url=$(curl -w "%{url_effective}\n" -I -L -s -S  https://github.com/mozilla/geckodriver/releases/latest -o /dev/null)
geckodriver_latest_version=(${geckodriver_latest_url//releases\/tag\/v/ })
geckodriver_latest_version=${geckodriver_latest_version[1]}

echo "Chromedriver latest version: $chromedriver_latest_version"
echo "Geckodriver latest version: $geckodriver_latest_version"

for chromedriver in "${chromedrivers[@]}"
do :
    echo "Downloading $chromedriver"
    chromedriver_url="https://chromedriver.storage.googleapis.com/$chromedriver_latest_version/$chromedriver"
    wget -q -P $resource_dir $chromedriver_url
    echo "Extracting $chromedriver"
    chromedriver_directory=(${chromedriver//./ })
    chromedriver_directory=${chromedriver_directory[0]}
    mkdir -p $resource_dir/$chromedriver_directory
    unzip -q $resource_dir/$chromedriver -d $resource_dir/$chromedriver_directory
    rm -f $resource_dir/$chromedriver
done

for geckodriver in "${geckodrivers[@]}"
do :
    geckodriver_filename="geckodriver-v$geckodriver_latest_version-$geckodriver"
    echo "Downloading $geckodriver_filename"
    geckodriver_url="https://github.com/mozilla/geckodriver/releases/download/v$geckodriver_latest_version/$geckodriver_filename"
    wget -q -P $resource_dir $geckodriver_url
    echo "Extracting $geckodriver_filename"
    geckodriver_directory=(${geckodriver//./ })
    geckodriver_directory="geckodriver_${geckodriver_directory[0]}"
    mkdir -p $resource_dir/$geckodriver_directory
    if [[ $geckodriver == *"tar.gz"* ]]; then
        tar -xf $resource_dir/$geckodriver_filename -C $resource_dir/$geckodriver_directory
    else
        unzip -q $resource_dir/$geckodriver_filename -d $resource_dir/$geckodriver_directory
    fi
    rm -f $resource_dir/$geckodriver_filename
done

echo "WebDrivers updated to latest version!"
