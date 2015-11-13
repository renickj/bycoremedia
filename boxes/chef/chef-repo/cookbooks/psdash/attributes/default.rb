# The version to install, 'latest' means latest release.
default['psdash']['version']  = 'latest'
# An array of patterns to scan for logfiles.
default['psdash']['logs']     = ['/var/log/**/*.log']
# The network to listen on, `0.0.0.0` means listen to all.
default['psdash']['host']     = '0.0.0.0'
# The port to listen on.
default['psdash']['port']     = '8999'
