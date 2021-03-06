# author: Martin K. Scherer
require 'octokit'

# github api token 
token = ENV['token']
# tag name, which is being released
tag = ENV['TRAVIS_TAG']
# filename of jar file which has just been published
jar = ENV['jarname']
  
raise "no token given" unless token.to_s != ''
raise "no jar name given" unless jar.to_s != ''


# authorize with github api token
client = Octokit::Client.new(:access_token => token)

filename = "#{jar}-jar-with-dependencies.jar"

# generate hash for jar
text = "sha256: " + `cd target/; sha256sum #{filename}`

# get latest release, assumes latest rel is first list element. Seems to be true.
latest = Octokit.releases({:user => "markovmodel", :repo => "stallone" })[0]

# extract release url of current tag
release_url = latest.url

# this can be nil, since only annotated commits fill up the body.
old_body = latest.body

new_body = "#{old_body}<br><br>#{text}"

# update release body message
client.update_release(release_url, {:body => new_body})
