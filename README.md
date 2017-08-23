Earthquake Bulletin
===================

[![Latest release](https://img.shields.io/github/release/Albertus82/EarthquakeBulletin.svg)](https://github.com/Albertus82/EarthquakeBulletin/releases/latest)
[![Build status](https://travis-ci.org/Albertus82/EarthquakeBulletin.svg?branch=master)](https://travis-ci.org/Albertus82/EarthquakeBulletin)
[![Build status](https://ci.appveyor.com/api/projects/status/github/Albertus82/EarthquakeBulletin?branch=master&svg=true)](https://ci.appveyor.com/project/Albertus82/EarthquakeBulletin)

A cross-platform desktop client for **GEOFON Program GFZ Potsdam Earthquake Bulletin** written in Java.

![Screenshot](https://user-images.githubusercontent.com/8672431/28755268-208fa9be-7557-11e7-9f69-ef70c9f38027.png)

## Download

Download the [latest release](https://github.com/Albertus82/EarthquakeBulletin/releases/latest) from the [releases page](https://github.com/Albertus82/EarthquakeBulletin/releases).

## Flinn-Engdahl Regions

This package includes a port to Java of Bob Simpson's [**`feregion.pl`**](ftp://hazards.cr.usgs.gov/feregion/fe_1995/) Perl script that returns [Flinn-Engdahl Region](https://earthquake.usgs.gov/learn/topics/flinn_engdahl.php) names from decimal longitude & latitude values given on command line. You can recall this *bonus* program using the **`feregion`** shell script available in the *Earthquake Bulletin* application folder.
```
Usage:  feregion  <lon> <lat>
As In:  feregion  -122.5  36.2
As In:  feregion   122.5W 36.2N
```

## Acknowledgements

Locations were obtained from the GEOFON programme of the [GFZ German Research Centre for Geosciences](http://www.gfz-potsdam.de) using data from the GEVN partner networks.

Seismic data were obtained from the GEOFON data centre of the GFZ German Research Centre for Geosciences.

This application includes software developed by the Eclipse Foundation that is distributed under the [Eclipse Public License](https://eclipse.org/org/documents/epl-v10.php).
