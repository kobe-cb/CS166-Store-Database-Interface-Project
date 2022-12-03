#!/bin/bash
psql -h localhost -p $PGPORT "$USER"_DB < $DIR/../src/create_tables.sql > /dev/null
psql -h localhost -p $PGPORT "$USER"_DB < $DIR/../src/load_data.sql > /dev/null
sleep 5

echo "Query time without indexes"
cat <(echo '\timing') $DIR/../src/queries.sql | psql -h localhost -p $PGPORT $DB_NAME | grep Time | awk -F "Time" '{print "Query" FNR $2;}'

psql -h localhost -p $PGPORT $DB_NAME < $DIR/../src/create_indexes.sql > /dev/null

echo "Query time with indexes"
cat <(echo '\timing') $DIR/../src/queries.sql |psql -h localhost -p $PGPORT $DB_NAME | grep Time | awk -F "Time" '{print "Query" FNR $2;}'

