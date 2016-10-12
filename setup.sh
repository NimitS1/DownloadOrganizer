#!/bin/bash

options=("Windows" "Mac OS" "Linux")

select opt in "${options[@]}"
do
    case $opt in
        "Windows")
            SLASH="\\"
            break
            ;;
        "Mac OS")
            SLASH='/'
            break
            ;;
        "Linux")
            SLASH='/'
            break
            ;;
        *) echo invalid option;;
    esac
done

mvn clean
mvn package assembly:single

DIR=$(pwd)
CONFIG="config.yml"
ORGANIZE="organize.sh"

echo "java -jar ${DIR}${SLASH}target${SLASH}DownloadOrganizer-0.0.1-SNAPSHOT-jar-with-dependencies.jar ${CONFIG}" > $ORGANIZE

echo "Please put your configurations inside of ${CONFIG}. If you'd like to put it elsewhere, modify ${ORGANIZE} to read from it."

if [[ $SLASH == "/" ]] # NOT SURE IF THIS'LL WORK ON WINDOWS, DON'T HAVE ONE TO TEST IN
then
    echo "alias organize=\"bash \\\"${DIR}${SLASH}${ORGANIZE}\\\"\"" >> ~/.bash_profile
    echo "The alias organize should execute the script once you've restarted your shell. Please ensure you've created the config file ${CONFIG}."
fi
