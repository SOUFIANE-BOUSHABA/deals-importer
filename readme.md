to  test  after  clone just 

1  : mvn -DskipTests package

2  : docker compose up --build 

------------------------------
to test  upload csv (ex) : 
                        
                        curl -v -F "file=@02_duplicate_deals.csv;type=text/csv" http://localhost:8080/api/deals/upload


csv files  is on  sample folder