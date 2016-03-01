Extensible Hash Index
======================

## Description
This program is used to demonstrate the basic logic behind managing an extensible hash index. This program also displays basic timing on inserting and deleting from the extensible hash index. This index design is more useful as an indexing tool in a database system, but this project is used to demonstrate how one might start handling the index aspect of the database. These records in the index would realistically be written and read from disk, but in this proof of concept the index is used to help understand how the index works.

#### Notes
This project faces one of the issues with extensible hashing, which is dealing with duplicate hashes.
* To deal with this, there are two types of hashing included for the hash bucket types: STANDARD and HASH.
* The HASH bucket can be used to remove duplicates and prevent duplicate insertions based on equals() and hashCode() values.
  * This realistically would be handled by the database.
* STANDARD does not deal with this, and therefore will run out of bucket space trying to fit multiple objects (object count > max bucket size) that are equal. In this proof of concept, STANDARD should be used with a large enough range of objects to be inserted, and a large enough maximum bucket size.

## Resources
* For more information on extensible hash indexes, see here:
  * [Wikipedia](https://en.wikipedia.org/wiki/Extendible_hashing)

## Building
This project can be imported into Eclipse, and built normally.

## Usage
To run the basic program, run the main class called "ehindex.main.Main.java"
