#!/usr/bin/env sh
set -e

if [ "$1" = 'replace' ]; then
    chmod +x replace.sh
    ./replace.sh /www/config.json
fi

echo "Starting nginx"
nginx &
echo "Nginx started"
exec java -Djava.security.egd=file:/dev/./urandom -jar /usr/app/portal.jar $@
