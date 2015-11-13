# psdash

This cookbook is an application cookbook to install [psdash](https://github.com/Jahaja/psdash), a process dashboard.

## attributes

* ```['psdash']['logs']``` - An array of log pattern to scan, defaults to ```/var/log/**/*.log```.
       
* ```['psdash']['host']``` - The network address to bind to, defaults to ```0.0.0.0```
       
* ```['psdash']['port']``` - The port to listen to, defaults to ```8999```.
       

* default - installs and starts psdash.

## dependencies

* python
