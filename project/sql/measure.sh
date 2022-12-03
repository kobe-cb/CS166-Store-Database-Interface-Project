#!/bin/bash
echo "------Measuring------"

psql -h localhost -p $PGPORT "$USER"_DB < $DIR/../src/create_tables.sql > /dev/null
psql -h localhost -p $PGPORT "$USER"_DB < $DIR/../src/load_data.sql > /dev/null
sleep 5

echo "Query time without indexes"
cat <(echo '\timing') $DIR/../queries.sql | psql -h localhost -p $PGPORT "$USER"_DB | grep Time | awk -F "Time" '{print "Query" FNR $2;}'

psql -h localhost -p $PGPORT "$USER"_DB < $DIR/../src/create_indexes.sql > /dev/null
sleep 5

echo "Query time with indexes"
cat <(echo '\timing') $DIR/../queries.sql | psql -h localhost -p $PGPORT "$USER"_DB | grep Time | awk -F "Time" '{print "Query" FNR $2;}'

echo "Done."
