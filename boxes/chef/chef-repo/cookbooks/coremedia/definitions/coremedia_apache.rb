define :coremedia_apache do
  package_name = params[:name]

  if platform? 'windows'
    coremedia_apache_windows package_name
  else
    coremedia_apache_unix package_name
  end
end
