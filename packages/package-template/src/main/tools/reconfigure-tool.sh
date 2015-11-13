#!/bin/bash
DIR="`dirname \"$0\"`"
if [ "$1" != "-y" ]; then
  while true
  do
    read -p "Reconfigure @APPLICATION_NAME@ now (y/n) ? " answer
    case $answer in
     [yY]* )
             break;;

     [nN]* ) exit;;
     * )     echo "Enter Y or N.";;
    esac
  done
fi

su @INSTALL_USER@ - $DIR/INSTALL/reconfigure.sh @APPLICATION_NAME@ @APPLICATION_INSTALL_ROOT@
