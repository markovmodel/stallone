# author: Martin K. Scherer
require 'octokit'

# use token from travis somehow
token = ENV['token']
  
# authorize with github api token
client = Octokit::Client.new(:access_token => token)

# info about release and asset name
tag = ENV['TRAVIS_TAG']
filename = "#{ENV['jarname']}-jar-with-dependencies.jar"

# generate hash for jar
text = "sha256: " + `cd target/; sha256sum #{filename}`

# get latest release, assumes latest rel is first list element. Seems to be true.
latest = Octokit.releases({:user => "markovmodel", :repo => "stallone" })[0]

# extract release url of current tag
release_url = latest.url

old_body = latest.body

new_body = old_body + '<br><br>' + text

# update release body message
client.update_release(release_url, {:body => new_body})
