CS166 Store Database Interface Project

# Steps
- cd project/serverManagement/
- source startPostgreSQL.sh
- source createPostgreDB.sh
- pg_ctl status (optional but recommended, this is to ensure the database is up and running)
- cd ../sql/scripts/
- source create_db.sh
- cd ../../java/scripts/
- source compile.sh
- Use application for however long you like
- If done, cd ../../serverManagement/
- source stopPostgreDB.sh

