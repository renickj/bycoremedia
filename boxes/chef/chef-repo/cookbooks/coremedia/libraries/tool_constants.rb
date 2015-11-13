class Chef
  class Resource
    class Execute
      def cm_exec
        if platform_family?("windows")
          '\\bin\\cm64'
        else
          '/bin/cm'
        end
      end
    end
  end
end