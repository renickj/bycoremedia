#!/bin/bash
DIR="`dirname \"$0\"`"

if [ "$1" != "-y" ]; then
  while true
  do
    read -p "uninstall @APPLICATION_NAME@ now (y/n) ? " answer
    case $answer in
     [yY]* )
             break;;

     [nN]* ) exit;;

     * )     echo "Enter Y or N.";;
    esac
  done
fi

echo "Start uninstalling @APPLICATION_NAME@ from /etc/httpd/conf.d"
$DIR/INSTALL/preuninstall.sh 0
$DIR/INSTALL/postuninstall.sh 0

for file in `cat $DIR/installed-files` ; do
  echo "deleting $file"
  rm -f $file
done
rm -f $DIR/installed-files
