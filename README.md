# Movie Genres

This repository was created for the purpose of learning data structures at Brooklyn College's Department of CIS.

This program reads in a CSV file that contain movies id, titles and genres from MovieLens. 

The 'report1.csv' file produced by the program in folder 'output' contains information how many movies are classified under each genre.

The 'index.html' file produced by the program contains visualization for "report1.csv" and also shows how many movies came out for each genre each year. You need the Internet connection to see it.

## Dependencies

* [Java 8](https://docs.oracle.com/javase/8/docs/api/index.html)
* [OpenCSV](http://opencsv.sourceforge.net/)
* [Highcharts](https://www.highcharts.com/)
* [Travis CI](https://travis-ci.com/)
* [IntelliJ Idea](https://www.jetbrains.com/idea/)

Java 8 is used here because it's the department's officially supported language and version.

OpenCSV is a library which is used to read a CSV file (MovieLens chart in this case) and write data to CSV files (output of this program). 

This repository is linked to Travis-CI by way of a `.travis.yml` file in the root of the directory.

Highcharts is a JavaScript library which is used for data visualization.

IntelliJ Idea as my IDE.


## Folder Structure

* Code is saved into the `src` folder.
* Data is saved into the `data` folder.
* Vizualization is saved into the `charts` folder.
* Output files are saved into the `output` folder.
* The required libraries are in the `lib` folder.

## How to build and run

1. Open terminal window
2. Navigate to go "scripts" folder
3. Execute `./run.sh`

Note: You may need to give execute permission to the script by running `chmod +x *.sh` from "scripts" folder