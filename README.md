Earthquake Bulletin
===================

[![Latest release](https://img.shields.io/github/release/Albertus82/EarthquakeBulletin.svg)](https://github.com/Albertus82/EarthquakeBulletin/releases/latest)
[![Build status](https://travis-ci.org/Albertus82/EarthquakeBulletin.svg?branch=master)](https://travis-ci.org/Albertus82/EarthquakeBulletin)
[![Build status](https://ci.appveyor.com/api/projects/status/github/Albertus82/EarthquakeBulletin?branch=master&svg=true)](https://ci.appveyor.com/project/Albertus82/EarthquakeBulletin)

A cross-platform desktop client for the [**GEOFON Program GFZ Potsdam Earthquake Bulletin**](http://geofon.gfz-potsdam.de/eqinfo/list.php) written in Java.

![Screenshot](https://user-images.githubusercontent.com/8672431/28755268-208fa9be-7557-11e7-9f69-ef70c9f38027.png)

## Download

Download the [latest release](https://github.com/Albertus82/EarthquakeBulletin/releases/latest) from the [releases page](https://github.com/Albertus82/EarthquakeBulletin/releases).

## Installation

* **Windows:** run the installer to install the application.

  If Windows complains with a ***Windows protected your PC*** popup, you may need to click ***Run anyway*** to proceed with the installation.

  ![Windows protected your PC](https://user-images.githubusercontent.com/8672431/31048995-7145b034-a62a-11e7-860b-c477237145ce.png)

  In order to enable the *Run anyway* button, you may need to open the *Properties* of the installer, tab *General*, section *Security* (if available), and check the ***Unblock*** option.
* **Linux:** unpack the archive and run the [`earthquake-bulletin.sh`](src/main/scripts/earthquake-bulletin.sh) shell script.
* **macOS:** unpack the archive and run the program using its icon.

## Flinn-Engdahl Regions

This package includes a port to Java of Bob Simpson's [**`feregion.pl`**](https://goo.gl/1BVgMx) Perl script that returns [Flinn-Engdahl Region](https://earthquake.usgs.gov/learn/topics/flinn_engdahl.php) names from decimal longitude & latitude values given on command line. You can recall this bonus program using the **`feregion`** executable located in the *Earthquake Bulletin* application folder.
```
Usage:  feregion  <lon> <lat>
As In:  feregion  -122.5  36.2
As In:  feregion   122.5W 36.2N
```

## Acknowledgements

Locations were obtained from the [GEOFON](http://geofon.gfz-potsdam.de) programme of the [GFZ](http://www.gfz-potsdam.de) German Research Centre for Geosciences using data from the [GEVN](http://geofon.gfz-potsdam.de/eqinfo/gevn/) partner networks.

Seismic data were obtained from the GEOFON data centre of the GFZ German Research Centre for Geosciences.

This application includes software developed by the Eclipse Foundation that is distributed under the [Eclipse Public License](https://eclipse.org/org/documents/epl-v10.php).
