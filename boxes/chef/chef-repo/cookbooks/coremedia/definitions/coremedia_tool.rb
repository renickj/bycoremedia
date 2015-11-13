define :coremedia_tool do
  package_name = params[:name]

  if platform? 'windows'
    coremedia_tool_windows package_name
  else
    coremedia_tool_unix package_name
  end
end
