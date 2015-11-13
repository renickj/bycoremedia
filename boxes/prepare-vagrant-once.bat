@ECHO OFF

:: check if HOME is set 
IF "%HOME%"=="" (
 ECHO HOME is NOT defined, please set HOME environment variable to your user directory.
 exit
) 

:: remove and recreate dummyvm 
vboxmanage unregistervm hostonly-adapter-blocker --delete
vboxmanage createvm --name hostonly-adapter-blocker --register
:: remove and recreate hostonly adapter
vboxmanage hostonlyif remove "VirtualBox Host-Only Ethernet Adapter"
vboxmanage hostonlyif create ipconfig "VirtualBox Host-Only Ethernet Adapter" --ip 192.168.252.1 --netmask 255.255.255.0
:: set adapter to dummy vm
vboxmanage modifyvm hostonly-adapter-blocker --nic2 hostonly --cableconnected2 on --hostonlyadapter2 "VirtualBox Host-Only Ethernet Adapter"  
:: ping for 5 seconds (wait in windows), to see if we get an apipa address or a valid one
ping 127.0.0.1 -n 5 -w 1000
vboxmanage list hostonlyifs
echo check if above listed network adapter with the name "VirtualBox Host-Only Ethernet Adapter" 
echo has the ip 192.168.252.1 and subnet 255.255.255.0. If that is not the case, and you see an APIPA address 
echo e.g. 169.254.x.x you need to rerun the script.
