define :coremedia_service do
  package_name = params[:name]

  if platform? 'windows'
    coremedia_service_windows package_name
  else
    coremedia_service_unix package_name
  end
end
