Earthquake Bulletin
===================

[![Latest release](https://img.shields.io/github/release/albertus82/earthquake-bulletin.svg)](https://github.com/albertus82/earthquake-bulletin/releases/latest)
[![Build status](https://github.com/albertus82/earthquake-bulletin/workflows/build/badge.svg)](https://github.com/albertus82/earthquake-bulletin/actions)
[![Build status](https://ci.appveyor.com/api/projects/status/github/albertus82/earthquake-bulletin?branch=master&svg=true)](https://ci.appveyor.com/project/albertus82/earthquake-bulletin)
[![Known Vulnerabilities](https://snyk.io/test/github/albertus82/earthquake-bulletin/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/albertus82/earthquake-bulletin?targetFile=pom.xml)

A cross-platform desktop client for the [**GEOFON Program GFZ Potsdam Earthquake Bulletin**](https://geofon.gfz-potsdam.de/eqinfo/list.php) written in Java.

![Screenshot](https://user-images.githubusercontent.com/8672431/112748093-e8a85600-8fb9-11eb-8b86-ba8a498bf377.png)

## Download

Download the [latest release](https://github.com/albertus82/earthquake-bulletin/releases/latest) from the [releases page](https://github.com/albertus82/earthquake-bulletin/releases).

## Installation

* **Windows**: if you downloaded a ZIP package, simply unpack the archive; otherwise run the installer (EXE) to install the application.

  If the OS complains with a ***Windows protected your PC*** popup, you may need to click ***Run anyway*** to proceed with the installation.

  ![Windows protected your PC](https://user-images.githubusercontent.com/8672431/31048995-7145b034-a62a-11e7-860b-c477237145ce.png)

  In order to enable the *Run anyway* button, you may need to open the *Properties* of the installer, tab *General*, section *Security* (if available), and tick the ***Unblock*** option.
  > This workaround is required because the installer executables are not *signed*, and there are no free certificates I can use to sign them.
* **Linux** & **macOS**: unpack the archive.

**This application requires [Java SE Runtime Environment (JRE)](https://www.java.com) v1.8 (or newer) to run.**

## Flinn-Engdahl Regions

This package includes a port to Java of Bob Simpson's [**`feregion.pl`**](https://bit.ly/feregion) Perl script that returns [Flinn-Engdahl Region](https://earthquake.usgs.gov/data/flinn_engdahl.php) names from decimal longitude & latitude values given on command line. You can recall this bonus program using the **`feregion`** executable located in the *Earthquake Bulletin* application folder.
```
Usage:  feregion  <lon> <lat>
As In:  feregion  -122.5  36.2
As In:  feregion   122.5W 36.2N
```

## Acknowledgements

Locations were obtained from the [GEOFON](https://geofon.gfz-potsdam.de) programme of the [GFZ](https://www.gfz-potsdam.de) German Research Centre for Geosciences using data from the [GEVN](https://geofon.gfz-potsdam.de/eqinfo/gevn/) partner networks.

Seismic data were obtained from the GEOFON data centre of the GFZ German Research Centre for Geosciences.

The stylized [OpenStreetMap](https://www.openstreetmap.org) icon has been downloaded from [Iconfinder](https://www.iconfinder.com/icons/4691290/openstreetmap_icon) ([Creative Commons Attribution 3.0 Unported](https://creativecommons.org/licenses/by/3.0/) license) and has been converted and optimized using [PNG to SVG](https://www.pngtosvg.com) and [TinyPNG](https://tinypng.com) online services.

This application uses or includes portions of the following third party software:

|Component                    |Author               |License                                                 |Home                                          |
|-----------------------------|---------------------|--------------------------------------------------------|----------------------------------------------|
|Activation, iStack, JAXB     |Eclipse Foundation   |[License](https://eclip.se/tmpolicA)                    |[Home page](https://eclip.se/tmpolicz)        |
|Eclipse Platform, Nebula, SWT|Eclipse Foundation   |[License](https://www.eclipse.org/legal/epl-2.0/)       |[Home page](https://www.eclipse.org)          |
|Inno Setup                   |Jordan Russell       |[License](https://jrsoftware.org/files/is/license.txt)  |[Home page](https://jrsoftware.org/isinfo.php)|
|jsoup                        |Jonathan Hedley      |[License](https://jsoup.org/license)                    |[Home page](https://jsoup.org)                |
|Launch4j                     |Grzegorz Kowal       |[License](https://opensource.org/licenses/BSD-3-Clause) |[Home page](http://launch4j.sourceforge.net)  |
|Logback                      |Quality Open Software|[License](http://logback.qos.ch/license.html)           |[Home page](http://logback.qos.ch)            |
|Picocli                      |Remko Popma          |[License](https://git.io/JUqAY)                         |[Home page](https://picocli.info)             |
|Reflections                  |ronmamo              |[License](https://git.io/Jtp8i)                         |[Home page](https://git.io/Jtp81)             |
|Resilience4j                 |Robert Winkler et al.|[License](https://resilience4j.readme.io/docs/apache-20)|[Home page](https://resilience4j.readme.io)   |
|SLF4J                        |Quality Open Software|[License](http://www.slf4j.org/license.html)            |[Home page](http://www.slf4j.org)             |
|universalJavaApplicationStub |Tobias Fischer       |[License](https://git.io/JUqAq)                         |[Home page](https://git.io/JUqAF)             |
