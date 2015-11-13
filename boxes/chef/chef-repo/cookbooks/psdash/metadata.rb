name "psdash"
maintainer "CoreMedia AG"
description "Performs configuration of an installed psdash"
version "0.1.1"

recipe "psdash", "configures and starts psdash"

%w{ redhat centos amazon}.each do |os|
  supports os
end

depends "python", "= 1.4.6"
