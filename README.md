# comfort-checker

A simple application that loads data from the Spark Works infrastructure and the GAIA store and then calculates the comfort index of the areas for the data acquired.
The results are then stored on the local machine in a csv file.

To select the schools you are interested in update the following properties:
+ `sparks.cargo.oauth2.client.clientId`
+ `sparks.cargo.oauth2.client.clientSecret`
+ `sparks.cargo.oauth2.client.username`
+ `sparks.cargo.oauth2.client.password`
+ `comfort.paths`

Thermal Comfort Calculation based on the following tools:

+ http://comfort.cbe.berkeley.edu/EN
+ https://github.com/CenterForTheBuiltEnvironment/comfort_tool
