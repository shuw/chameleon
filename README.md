This project and the following description are from 2008, migrated from Google code.

# Chameleon
http://www.youtube.com/watch?feature=player_embedded&v=xtP56M_eAF0

## About
Chameleon is a open-source project I built to experiment and play with social networking data. Currently it is hooked up to the Flickr API, but I can easily support for Facebook or other social networks.

Please keep in mind it may be a little rough around the edges as it is the product of a few weekends of cramming in January. I'll likely add new features and polish it more when I have time.

## Instructions
1. Ensure that the latest version of Java (Version 6+ recommended) is installed. Also have a zip-file opener ready.
2. Download: http://chameleon-flickr.googlecode.com/files/Chameleon.zip
3. Unzip the contents of Chameleon.zip into an new empty directory and run

Note
Even though  Chameleon caches data on disk for optimizing performance, you must be connected to the Internet to do most operations.

## Source
Developed using Eclipse and Java.

I am still finalizing my graphics library built on top of Java Swing and Piccolo, the source will be released when it is stable. Until that is checked in, the project won't build.
That graphics library was built while I was working on the UI for Nengo http://nengo.ca/development. Here's a video of how that UI looked: http://www.youtube.com/watch?v=crX5V61MtiE.

## Acknowledgements
Flickr Data Access Layer: http://sourceforge.net/projects/flickrj/
Graph layout algorithms: http://jung.sourceforge.net/
2D Graphics Framework: http://www.cs.umd.edu/hcil/jazz/