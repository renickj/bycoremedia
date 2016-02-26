include_recipe 'cron::default'
package 'curl'
if node['coremedia'].attribute?('sitemaps')
  tld = node['coremedia']['configuration']['configure.DELIVERY_TLD']
  node['coremedia']['sitemaps'].each_pair do |key, value|
    cron_d "coremedia-sitemap-for-#{key}" do
      minute value['minute']
      hour value['hour']
      day value['day']
      month value['month']
      weekday value['weekday']
      command "curl -X GET \"http://localhost:49080/blueprint/servlet/internal/perfectchef/sitemap-org?repositoryPath=#{value['path']}&domain=#{key}.#{tld}\""
      user node['coremedia']['user']
    end
  end
end