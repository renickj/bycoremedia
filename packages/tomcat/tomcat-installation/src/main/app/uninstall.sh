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
if [ -d @APPLICATION_INSTALL_ROOT@ ]; then
 rm -rf @APPLICATION_INSTALL_ROOT@
fi
