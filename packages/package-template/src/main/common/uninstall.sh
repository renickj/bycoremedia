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

echo "Start uninstalling @APPLICATION_NAME@ from @APPLICATION_INSTALL_ROOT@"
$DIR/INSTALL/preuninstall.sh 0
$DIR/INSTALL/postuninstall.sh 0

if [ -d @APPLICATION_INSTALL_ROOT@ ]; then
 rm -rf @APPLICATION_INSTALL_ROOT@
fi
