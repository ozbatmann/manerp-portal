#!/bin/sh
#for env in $(compgen -v) ; do
#    if [[ `jq "select(.$env)" $1` ]]; then
#        jq ".$env=\"${!env}\"" $1 > _temp.json && mv _temp.json $1
#    fi
#done
if which python; then echo "python exists";
else
    if which yum; then
        yum python
    elif which apt-get; then
        apt-get install python
    elif which apk; then
        apk add --no-cache python
    else
      exit 1;
    fi
fi
chmod +x $1
python ./replace.py < $1 > new.json; mv new.json $1
