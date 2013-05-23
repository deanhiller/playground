#!/bin/bash

#NOTE: Developers, PLEASE do not start this script and start using the 
#production database!!!!

cp conf/prod.logback.xml conf/logback.xml

play-1.2.5/play run --%prod -Xmx1024M &
disown

#-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1044
