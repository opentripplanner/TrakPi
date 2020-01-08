


# Importing data
To get started (before we have a UI) to edit data we will use a CSV import tool to populate data into TrackPi.

Run the `org.opentripplanner.trakpi.application.csvimport#main(..)` to import test cases, planners and test plans into 
the database. Use `--help` to list command line options.

Example CSV files are found in [here](csv-examples).


## Tags
Tags are used to select TestCases for a particular TestPlan using a TagExpr. See the unit test and JavaDoc 
on TagExpr on how-to write TagExpressions.




