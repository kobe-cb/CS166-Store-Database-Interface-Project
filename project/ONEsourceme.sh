#!/bin/bash

source /extra/kbrag003/project_phase_3/project/toRun/startPostgreSQL.sh
sleep 5
source /extra/kbrag003/project_phase_3/project/toRun/createPostgreDB.sh
sleep 5
source /extra/kbrag003/project_phase_3/project/sql/scripts/create_db.sh
sleep 5
echo ">>>>>> DONE. psql -h localhost -p \$PGPORT \"\$USER\"_DB"
