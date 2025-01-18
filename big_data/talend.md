- ## Using TALEND
- Demo 1
  1. Creating job
  2. Adding TRowGenerator, TLogGenerator
  3. Open TRowGenerator
  4. Add columns
  5. Exit and run
- Demo 2
  1. In the repository, click on MetaData/File Delimited
  2. Upload the csv file in step 2
  3. In step 3, change the field seperator to comma and set the header the 1, and select the use header near preview
  4. DateTime format `YYYY-MM-DD HH:MM:SS`
  5. Click on preview to see refined table
  6. In step 4 click on `guess` button to update schema
  7. Update the date-time schema `YYYY-MM-DD HH:MM:SS`
  8. click finsh
  9. In the repository, click on MetaData/DB Connections
  10. Create a new Job Design
  11. Add tfilelist, tfileinputdelimited, tdboutput
  12. Double click tfilelist
  13. Select tfileinputdelimited and change the schema to repository
  14. Beside schema click on dbconnection->yellowtrip_con->schema->metadata
  15. Next in file name/scheme use "((String)globalMap.get("tFileList_1_CURRENT_FILEPATH"))"
  16. Change the field seperator to ","
  17. Select checkbox `CSV options`
  18. Select tdboutput and change database to mysql and hit apply
  19. change property type to repository and beside repository selec the db connection
  20. Select table name and below it select `create table if it does not exist` for action on table
